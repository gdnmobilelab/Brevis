import { brevisFetch as fetch } from './util/brevis-fetch';
import CONSTANTS from './const';
import CONFIG from './config';

import ContentService from './services/ContentService';
import UserService from './services/UserService';
import ObservationService from './services/ObservationService';

import refreshContent from './util/refresh-content';

importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-messaging.js');

let requestCreator = (request) => {
    return new Request(request.url, {
        method: request.method,
        headers: request.headers,
        // credentials: request.credentials,
        redirect: 'manual'
    });
};

// Incrementing CACHE_VERSION will kick off the install event and force previously cached
// resources to be cached again.
let CURRENT_CACHES = CONSTANTS.OFFLINE_CACHE;
const CACHE_ON_INSTALL = CONSTANTS.CACHE_ON_INSTALL;
const CACHE_BUST_ON_INSTALL = CONSTANTS.CACHE_BUST_ON_INSTALL;
const CACHED_ON_INSTALL = CACHE_ON_INSTALL.concat(CACHE_BUST_ON_INSTALL);

function createRequest(url) {
    return new Request(url)
}

function isResponseRedirect(response) {
    if (response.type === 'opaqueredirect' || (response.status >= 300 && response.status <= 399) ) {
        return true;
    } else {
        return false;
    }
}

function isResponseValid(response) {
    if(!response || response.status >= 299 || response.type !== 'basic') {
        return false;
    }

    return true;
}

function isRequestValid(request) {
    let url = new URL(request.url, self.location.href);

    return CACHED_ON_INSTALL.indexOf(url.pathname+url.search) >= 0 || url.pathname.startsWith('/brevis/app/images/');
}

function isImageRequest(request) {
    let url = new URL(request.url, self.location.href);

    return url.pathname.startsWith('/brevis/app/images/');
}

function fetchRequest(fetchRequest, timeout = -1) {
    return fetch(fetchRequest, {}, timeout);
}

function cacheResponse(request, response) {
    let cacheToUse = CURRENT_CACHES.assets;

    // If it's a redirect, evict it from the cache
    if (isResponseRedirect(response) || !isResponseValid(response)) {
        caches.open(cacheToUse)
            .then(function(cache) {
                cache.delete(request);
            });

        return response;
    }

    let responseToCache = response.clone();

    return caches.open(cacheToUse)
        .then(function(cache) {
            cache.put(request, responseToCache);
            return response;
        });
}

function fetchAndCache(request, defaultResponse) {
    return fetchRequest(request)
        .then(cacheResponse.bind(this, request))
        .catch((err) => {
            console.log(err);
            return defaultResponse;
        })
}

function checkCacheAndMaybeFetch(request) {
    console.time('checkCacheAndMaybeFetch');
    return caches.match(requestCreator(request))
        .then(function(response) {
            let clonedRequest = requestCreator(request);

            // This will lazily evict the cache
            if (response) {
                setTimeout(() => fetchAndCache(clonedRequest, response), 0);
                console.timeEnd('checkCacheAndMaybeFetch');
                return response;
            } else {
                console.log('fetching');
                return fetchAndCache(clonedRequest, response);
            }
        })
}

// On install, cache all the assets we have
self.addEventListener('install', event => {
    if (self.skipWaiting) { self.skipWaiting(); }
    
    // Cache without question
    let cacheOnInstall = CACHE_ON_INSTALL.map((url) => {
        let request = createRequest(url);
        return fetchRequest(request)
            .then(cacheResponse.bind(this, request));
    });

    // Bust the cache
    let cacheBustOnInstall = CACHE_BUST_ON_INSTALL.map((url => {
        let request = createRequest(url);
        let cacheBustedRequest = createRequest(url + '?_t=' + Date.now());
        return fetchRequest(cacheBustedRequest)
            .then(cacheResponse.bind(this, request));
    }));

    event.waitUntil(
        Promise.all(cacheOnInstall.concat(cacheBustOnInstall))
    )
});


self.addEventListener('activate', event => {
    let expectedCacheNames = Object.keys(CURRENT_CACHES).map(function(key) {
        return CURRENT_CACHES[key];
    });

    // Should be waiting here?
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    if (expectedCacheNames.indexOf(cacheName) === -1) {
                        console.log('Deleting out of date cache:', cacheName);
                        return caches.delete(cacheName);
                    } else {
                        return Promise.resolve();
                    }
                })
            )
                .then(() => {
                    console.log('Activated service worker');
                    return true;
                })
        })
    );
});

self.addEventListener('fetch', event => {
    if (event.request.method === 'GET' && isRequestValid(event.request)) {
        event.respondWith(
            checkCacheAndMaybeFetch(event.request)
        );
    }
});

const messageCommands = {
    'cache': (data) => {
        let req = createRequest(data.url);

        return checkCacheAndMaybeFetch(req)
            .then(() => {
                return { cached: true }
            })
            .catch((err) => {
                return {
                    error: err
                }
            });
    },
    'cachePurge': (data) => {
        return caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    return caches.delete(cacheName);
                })
            )
        })
            .then(() => {
                return { purged: true }
            })
            .catch((err) => {
                return {
                    error: err
                }
            })
    },
    'sync': (data) => {
        return self.registration.sync.register(data.sync)
            .then(() => {
                return { sync: true }
            })
            .catch((err) => {
                return {
                    error: err
                }
            })
    }
};

self.addEventListener('message', function(event) {
    messageCommands[event.data.command](event.data.data)
        .then((completed) => {
            event.ports[0].postMessage(completed);
        })
});

self.addEventListener('sync', function(event) {
    let toSync = Promise.resolve();

    switch(event.tag) {
        case 'syncContent':
        case 'test-tag-from-devtools':
            toSync = ContentService.syncWaitingContent();
            break;
        case 'syncUser':
            toSync = UserService.syncActiveUser();
            break;
        case 'syncObservationClick':
            toSync = ObservationService.syncObservationClicks();
            break;
    }

    event.waitUntil(
        toSync
    )
});


/** Background Updating */
firebase.initializeApp(CONFIG.FIREBASE.CONFIG);

const messaging = firebase.messaging();

function shuffle(a) {
    let j, x, i;
    for (i = a.length; i; i--) {
        j = Math.floor(Math.random() * i);
        x = a[i - 1];
        a[i - 1] = a[j];
        a[j] = x;
    }
}

//Todo: will this wait until all posts are fetched or will it terminate the service worker
// if it takes too long?
messaging.setBackgroundMessageHandler(function(payload) {
    let type = payload.data.type;

    switch(type) {
        case 'NEW_BRIEF':
            return refreshContent()
                .catch((err) => {
                    console.log(err);
                })
                .then((resp) => {
                    return {
                        title: payload.data.title,
                        data: JSON.parse(payload.data.payload)
                    };
                })
                .then((notification) => {
                    return self.registration.showNotification(
                        notification.title,
                        notification.data);
                })
    }
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close();

    // This looks to see if the current is already open and
    // focuses if it is
    event.waitUntil(clients.matchAll({
        type: "window"
    }).then(function(clientList) {
        for (let i = 0; i < clientList.length; i++) {
            let client = clientList[i];
            if (client.url == '/' && 'focus' in client)
                return client.focus();
        }
        if (clients.openWindow)
            return clients.openWindow(event.notification.data.onClick);
    }));
});
