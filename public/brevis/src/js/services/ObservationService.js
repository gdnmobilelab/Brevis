import APIService from './APIService';

import BrevisDB from '../db/BrevisDB';

function readRequest(contentId, latlong) {
    let isoDate = (new Date()).toISOString();

    let request = {
        content: {
            id: contentId
        },
        dateISO: isoDate
    };

    if (latlong) {
        request['latlong'] = {
            latitude: latlong.lat,
            longitude: latlong.lng
        }
    }

    return APIService.post('/api/observations/read', request)
        .catch((err) => {
            console.log(`There was an error observing a user read: ${err}`);
            throw err;
        })
}

function locationRequest(latlong, dateISO) {
    let isoDate = (new Date()).toISOString();

    let request = {
        latlong: {
            latitude: latlong.lat,
            longitude: latlong.lng
        },
        dateISO: isoDate
    };

    APIService.post('/api/observations/location', request)
        .catch((err) => {
            console.log(`There was an error observing a user location: ${err}`);
            throw err;
        })
}

class ObservationService {
    constructor() {
        this.isSyncing = false;
    }

    read(contentId, latlong) {
        return readRequest(
            contentId,
            latlong
        );
    }

    location(latlong) {
        return locationRequest(latlong)
    }

    syncObservationClicks() {
        if (this.isSyncing) { return Promise.resolve([]); } // Don't sync multiple times

        // Todo: make this less nested
        return BrevisDB.findCurrentUserId()
            .then((userId) => {
                return BrevisDB.findContentClickObservationToSync(userId)
                    .then((clickObseravtions) => {
                        return Promise.all(clickObseravtions.map((clickObservation) => {
                            return this.read(clickObservation.contentId, clickObservation.latlong)
                                .then((success) => {
                                    return BrevisDB.deleteContentClickObservationToSync(
                                        clickObservation.contentId, clickObservation.userId
                                    );
                                })

                        }))
                    })
                    .catch((err) => {
                        console.error(err);
                        return false;
                    })
            })
            .then((toReturn) => {
                this.isSyncing = false;

                return toReturn;
            })

    }
}

export default new ObservationService();