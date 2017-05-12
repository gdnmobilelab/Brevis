
export default function sendMessage(message) {
    return new Promise(function(resolve, reject) {
        let messageChannel = new MessageChannel();
        messageChannel.port1.onmessage = function(event) {
            if (event.data.error) {
                reject(event.data.error);
            } else {
                resolve(event.data);
            }
        };

        if ('serviceWorker' in navigator) {
            return navigator.serviceWorker.ready.then(() => {
                if (!navigator.serviceWorker.controller) {
                    return resolve();
                } else {
                    navigator.serviceWorker.controller.postMessage(message,
                        [messageChannel.port2]);
                }
            });
        }
    });
}