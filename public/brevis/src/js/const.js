const ASSET_CACHE_VERSION = '__ASSET_TOKEN__';

export default {
    ASSET_TOKEN: ASSET_CACHE_VERSION,
    OFFLINE_CACHE: {
        assets: 'brevis-assets-v' + ASSET_CACHE_VERSION
    },
    CACHE_ON_INSTALL: [
        `/brevis/app/assets/css/brevis.css?_t=${ASSET_CACHE_VERSION}`,
        `/brevis/app/assets/js/brevis.js?_t=${ASSET_CACHE_VERSION}`,
    ],
    CACHE_BUST_ON_INSTALL: [
        '/brevis/app/'
    ]
}