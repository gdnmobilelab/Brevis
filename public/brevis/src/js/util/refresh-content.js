import UserContentService from '../services/UserContentService';
import ContentService from '../services/ContentService';
import cacheURLOffline from '../util/cache-url-offline';

import BrevisDB from '../db/BrevisDB';

/**
 * Returns { content, user }
 */
export default function refreshContent() {
    return UserContentService.fetchContent()
        .then((resp) => {
            let contents = resp.contents;
            let user = resp.user;
            let imagesInContent = resp.images;
            let brief = resp.brief;

            // Image caching loops shouldn't block
            setTimeout(() => {
                for (let i = 0; i < imagesInContent.length; i++) {
                    for (let j = 0; j < imagesInContent[i].length; j++) {
                        cacheURLOffline(imagesInContent[i][j]);
                    }
                }
            }, 0);

            let wasSyncing = ContentService.isSyncing;
            // Check if we've got content waiting to sync and if it conflicts with any incoming content
            return ContentService.resolveContentConflicts(contents, user.id)
                .then((resolved) => {
                    console.log(resolved);

                    let toUpsert = [];

                    if (wasSyncing) {
                        // We we're syncing when we tried to resolve, ignore all updates from server
                        toUpsert = resolved.content.concat(resolve.conflicts).map((content) => {
                            return { content: content.content }
                        })
                    } else {
                        ContentService.syncWaitingContent(); //Sync our waiting content

                        // Remove any meta on conflicts, but we still want to updateCurrentUser the content itself
                        let conflicts = resolved.conflicts.map((content) => {
                            return { content: content.content }
                        });

                        toUpsert = resolved.content.concat(conflicts);
                    }

                    let toSave = [
                        BrevisDB.makeUserCurrentUser(user)
                    ];

                    if (brief) {
                        toSave.push(BrevisDB.insertBrief(brief, contents, user.id));
                        toSave = toSave.concat(toUpsert.map((content) => {
                            BrevisDB.upsertContent(content, user.id)
                        }));
                    }

                    return Promise.all(toSave)
                        .then((success) => {
                            return BrevisDB.fetchLatestContentPackage(user.id)
                        })
                        .then((content) => {
                            return {
                                content: content,
                                user: user
                            }
                        })
                })
        })

}