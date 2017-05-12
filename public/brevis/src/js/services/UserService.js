import APIService from './APIService';

import BrevisDB from '../db/BrevisDB';

class UserService {
    constructor() {
        this.isSyncing = false;
    }

    getCurrentUser() {
        return APIService.get('/api/users')
            .catch((err) => {
                console.log(`There was an error getting the user: ${err}`);
                throw err;
            })
    }

    updateCurrentUser(user) {
        return APIService.post('/api/users', user)
            .catch((err) => {
                console.log(`There was an error getting the user: ${err}`);
                throw err;
            })
    }

    syncActiveUser() {
        if (this.isSyncing) { return Promise.resolve(); } // Don't sync multiple times

        return BrevisDB.findCurrentUserId()
            .then((userId) => {
                return BrevisDB.findUserToSync(userId)
            })
            .then((user) => {
                return this.updateCurrentUser(user)
                    .then(() => {
                        BrevisDB.deleteUserToSync(user);
                    })
            })
            .catch((err) => {
                console.log(err);
                return false;
            })
            .then((toReturn) => {
                this.isSyncing = false;

                return toReturn;
            })
    }
}

export default new UserService()