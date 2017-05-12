import sendMessage from './service-worker-send-message';

export default function cacheURLOffline(url) {
    return sendMessage({
        command: 'cache',
        data: {
            url: url
        }
    })
}