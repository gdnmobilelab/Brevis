// Consider removing objectAssign for performance optimization
import objectAssign from '../util/object-assign';
import update from '../util/update-ext';

 let PouchDB = require('pouchdb-core')
    .plugin(require('pouchdb-adapter-idb'))
    .plugin(require('pouchdb-adapter-websql'));

const db = new PouchDB('BrevisDB');

function contentId(content, userId) {
    if (typeof content === 'object') {
        return `user_content_userId_${userId}_contentId_${content.id}`
    } else {
        return `user_content_userId_${userId}_contentId_${content}`
    }
}

function briefId(brief, userId) {
    return `user_brief_userId_${userId}_${brief.dateISO}_${brief.id}`;
}

function userId(user) {
    if (typeof user === 'object') {
        return `user_profile_${user.id}`
    } else {
        return `user_profile_${user}`
    }
}

function currentUserId() {
    return 'current_user';
}

function syncUserId(user) {
    if (typeof user === 'object') {
        return `sync_user_profile_userId_${user.id}`
    } else {
        return `sync_user_profile_userId_${user}`
    }
}

function syncContentId(content, userId) {
    if (typeof content === 'object') {
        return `sync_user_content_userId_${userId}_contentId_${content.id}`
    } else {
        return `sync_user_content_userId_${userId}_contentId_${content}`
    }
}

function syncClickObservation(content, userId) {
    if (typeof content === 'object') {
        return `sync_observation_click_userId_${userId}_contentId_${content.id}`
    } else {
        return `sync_observation_click_userId_${userId}_contentId_${content}`
    }
}

class BrevisDB {
    upsertContents(contents, userId) {
        if (!userId) { throw Error("userId is required"); }

        return Promise.all(contents.map((content) => this.upsertContent(content, userId)));
    }

    upsertContent(content, userId, errorCount = 5) {
        if (!userId) { throw Error("userId is required"); }
        if (!content) { throw Error("content is required"); }

        let id = contentId(content.content, userId);
        let onConflict = this.upsertContent.bind(this, content, userId, errorCount - 1);

        return db.get(id)
            .then(function(doc) {
                let rev = doc._rev;
                let result = objectAssign(doc, content);
                result._rev = rev;
                return db.put(result);
            })
            .then(function(response) {
                return response
            })
            .catch((err) => {
                return db.put(objectAssign({
                    _id: id
                }, content))
            })
            .catch(function (err) {
                console.log(err);
                if (err.status === 409 && errorCount >= 0) {
                    return onConflict();
                } else {
                    throw err;
                }
            });
    }

    getContent(id, userId) {
        if (!userId) { throw Error("userId is required"); }

        return db.get(contentId(id, userId))
            .then((query) => {
                return query;
            })
    }

    fetchLatestContentPackage(userId) {
        if (!userId) { throw Error("userId is required"); }

        return this
            .findLatestBrief(userId)
            .then((brief) => {
                return db.allDocs({
                    include_docs: true,
                    keys: brief.contents__
                }).then((result) => {
                    return result.rows.map((row) => {
                        return row.doc;
                    })
                });
            })
    }

    insertBrief(brief, contents, userId, errorCount = 5) {
        if (!userId) { throw Error("userId is required"); }
        if (!brief) { throw Error("brief is required"); }

        let id = briefId(brief, userId);
        let contentIds = contents.map(content => content.content).map((content) => contentId(content, userId));
        let onConflict = this.insertBrief.bind(this, brief, contents, userId, errorCount - 1);

        return db.get(id)
            .then(function(doc) {
                return db.put(objectAssign({
                    _id: id,
                    _rev: doc._rev,
                    contents__: contentIds
                }, brief));
            })
            .then(function(response) {
                return response
            })
            .catch((err) => {
                return db.put(objectAssign({
                        _id: id,
                        contents__: contentIds
                    }, brief));
            })
            .catch(function (err) {
                console.log(err);
                if (err.status === 409 && errorCount >= 0) {
                    return onConflict();
                } else {
                    throw err;
                }
            });
    }

    findLatestBrief(userId) {
        if (!userId) { throw Error("userId is required"); }

        return db.allDocs({
            endkey: `user_brief_userId_${userId}_`,
            startkey: `user_brief_userId_${userId}_\uffff`,
            limit: 1,
            descending: true,
            include_docs: true
        }).then((query) => {
            if (query.rows.length < 1) {
                throw new Error('Could not find latest brief');
            } else {
                return query.rows[0].doc;
            }
        });
    }

    upsertUser(user) {
        let id = userId(user);

        return db.get(id).then(function(doc) {
            return db.put(objectAssign({
                _id: id,
                _rev: doc._rev
            }, user));
        }).then(function(response) {
            return response
        }).catch(function (err) {
            return db
                .put(objectAssign({
                    _id: id,
                }, user))
                .then((response) => {
                    return response;
                })
        });
    }

    findUserById(id) {
        if (!id) { throw new Error("user is required") }

        return db.allDocs({
            startkey: `user_profile_${id}`,
            endkey: `user_profile_${id}`,
            limit: 1,
            include_docs: true
        }).then((query) => {
            if (query.rows.length < 1) {
                throw new Error('Could not find user');
            } else {
                return query.rows[0].doc;
            }
        });
    }

    findUser(user) {
        return this.findUserById(user.id);
    }

    makeUserCurrentUser(user) {
        if (!user) { throw new Error("user is required") }

        let id = currentUserId();

        this.upsertUser(user)
            .then(() => {
                return db.get(id).then(function(doc) {
                    return db.put({
                        _id: id,
                        _rev: doc._rev,
                        userId: user.id
                    });
                }).then(function(response) {
                    return response
                }).catch(function (err) {
                    return db
                        .put({
                            _id: id,
                            userId: user.id
                        })
                        .then((response) => {
                            console.log(response);
                            return response;
                        })
                });
            })
    }

    findCurrentUserId() {
        let id = currentUserId();

        return db.get(id)
            .then((doc) => {
                return doc.userId;
            })
            .catch((err) => {
                console.error(err);
                throw err;
            });
    }

    findCurrentUser() {
        let id = currentUserId();

        return db.get(id)
            .then((doc) => {
                return this.findUserById(doc.userId);
            })
            .catch((err) => {
                console.error(err);
                throw err;
            })
    }

    upsertUserToSync(user, errorCount = 5) {
        if (!user) { throw Error("user is required"); }

        let id = syncUserId(user);
        let onConflict = this.upsertContentToSync.bind(this, user, errorCount - 1);

        let userToSync = update(user, {
            $unset: '_id',
            $unset: '_rev'
        });

        console.log(userToSync);

        return db.get(id)
            .then(function(doc) {
                let rev = doc._rev;
                let result = objectAssign(doc, userToSync);
                result._rev = rev;
                return db.put(result);
            })
            .then(function(response) {
                return response
            })
            .catch((err) => {
                return db.put(objectAssign({
                    _id: id
                }, userToSync))
                    .then((success) => {
                        console.log(success);
                        return true;
                    })
            })
            .catch(function (err) {
                console.log(err);
                if (err.status === 409 && errorCount >= 0) {
                    return onConflict();
                } else {
                    throw err;
                }
            });
    }

    findUserToSync(user) {
        if (!user) { throw Error("user is required"); }

        let id = syncUserId(user);

        return db.get(id);
    }

    deleteUserToSync(user) {
        if (!user) { throw Error("user is required"); }

        let id = syncUserId(user);

        return db.get(id).then(function(doc) {
            doc._deleted = true;
            return db.put(doc);
        })
    }

    upsertContentsToSync(contents, userId) {
        console.log(contents, userId);
        return Promise.all(contents.map((content) => {
            return this.upsertContentToSync(content, userId);
        }))
    }

    upsertContentToSync(content, userId, errorCount = 5) {
        if (!userId) { throw new Error("userId is required"); }
        if (!content) { throw new Error("content is required") }

        let id = syncContentId(content.content, userId);
        let onConflict = this.upsertContentToSync.bind(this, content, userId, errorCount - 1);

        // `content` could have an _id
        let contentToSync = {
            content: content.content,
            meta: content.meta
        };

        return db.get(id)
            .then(function(doc) {
                let rev = doc._rev;
                let result = objectAssign(doc, contentToSync, {userId: userId});
                result._rev = rev;
                return db.put(result);
            })
            .then(function(response) {
                return response
            })
            .catch((err) => {
                return db.put(objectAssign({
                    _id: id
                }, contentToSync, {userId: userId}))
            })
            .catch(function (err) {
                console.log(err);
                if (err.status === 409 && errorCount >= 0) {
                    return onConflict();
                } else {
                    throw err;
                }
            });
    }

    findContentToSyncByKeys(contents, userId) {
        if (!userId) { throw Error("userId is required"); }

        let keys = contents.map((content) => {
            return syncContentId(content.content, userId);
        });

        return db.allDocs({
            keys: keys,
            include_docs: true
        }).then((query) => {
            return query.rows.filter((row) => !row.error && !row.value.deleted).map((row) => row.doc);
        });
    }

    findContentToSync(userId) {
        return db.allDocs({
            startkey: `sync_user_content_userId_${userId}`,
            endkey: `sync_user_content_userId_${userId}\uffff`,
            include_docs: true
        }).then((query) => {
            return query.rows.map((row) => row.doc);
        });
    }

    deleteContentToSync(content, userId) {
        if (!userId) { throw Error("userId is required"); }

        let id = syncContentId(content.content, userId);

        return db.get(id).then(function(doc) {
            doc._deleted = true;
            return db.put(doc);
        })
    }

    upsertContentClickObservationToSync(contentId, userId, latlong, errorCount = 5) {
        if (!userId) { throw new Error("userId is required"); }
        if (!contentId) { throw new Error("contentId is required") }

        let id = syncClickObservation(contentId, userId);
        let onConflict = this.upsertContentClickObservationToSync.bind(this, contentId, userId, latlong, errorCount - 1);

        let clickToSync = {
            contentId: contentId,
            userId: userId,
            latlong: latlong
        };

        return db.get(id)
            .then(function(doc) {
                let rev = doc._rev;
                let result = objectAssign(doc, clickToSync);
                result._rev = rev;
                return db.put(result);
            })
            .then(function(response) {
                return response
            })
            .catch((err) => {
                return db.put(objectAssign({
                    _id: id
                }, clickToSync))
            })
            .catch(function (err) {
                console.log(err);
                if (err.status === 409 && errorCount >= 0) {
                    return onConflict();
                } else {
                    throw err;
                }
            });
    }

    findContentClickObservationToSync(userId) {
        return db.allDocs({
            startkey: `sync_observation_click_userId_${userId}`,
            endkey: `sync_observation_click_userId_${userId}\uffff`,
            include_docs: true
        }).then((query) => {
            return query.rows.map((row) => row.doc);
        });
    }

    deleteContentClickObservationToSync(contentId, userId) {
        if (!contentId) { throw Error("contentId is required") }
        if (!userId) { throw Error("userId is required"); }

        let id = syncClickObservation(contentId, userId);

        return db.get(id).then(function(doc) {
            doc._deleted = true;
            return db.put(doc);
        })
    }

    saveLastAccessTime(time) {

    }
}

export default new BrevisDB();