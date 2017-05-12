import BrevisDB from '../db/BrevisDB';
import UserContentService from '../services/UserContentService';

class ContentService {
    constructor() {
        this.isSyncing = false;
    }

    resolveContentConflicts(contents, userId) {
        return BrevisDB.findContentToSyncByKeys(contents, userId)
            .then((contentWaitingToSync) => {
                let contentIdsNeedSyncing = contentWaitingToSync.reduce((acc, content) => {
                    acc[content.content.id] = true;

                    return acc;
                }, {});

               return contents.reduce((acc, content) => {
                   if (contentIdsNeedSyncing[content.content.id]) {
                       acc['conflicts'].push(content);
                   } else {
                       acc['content'].push(content);
                   }

                   return acc;
               }, {
                   'conflicts': [],
                   'content' : []
               })
            })
    }

    syncWaitingContent() {
        if (this.isSyncing) { return Promise.resolve([]); } // Don't sync multiple times

        // Todo: make this less nested
        return BrevisDB.findCurrentUserId()
            .then((userId) => {
                return BrevisDB.findContentToSync(userId)
                    .then((contents) => {
                        return Promise.all(contents.map((content) => {

                            return UserContentService
                                .updateContentMeta({
                                    contentId: content.content.id,
                                    meta: content.meta
                                })
                                .then(() => {
                                    let toReturn = {
                                        success: true,
                                        id: content.content.id
                                    };

                                    return BrevisDB.deleteContentToSync(content, content.userId)
                                        .then(() => {
                                            return toReturn
                                        })
                                        .catch((err) => {
                                            console.error(err);
                                            return toReturn;
                                        });
                                })
                                .catch((err) => {
                                    return {
                                        success: false,
                                        id: content.id
                                    }
                                })
                        }))
                    })
            })
            .catch((err) => {
                console.error(err);
                return false;
            })
            .then((toReturn) => {
                this.isSyncing = false;

                return toReturn;
            })
    }
}

export default new ContentService();