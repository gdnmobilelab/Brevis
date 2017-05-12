
export default function registerServiceWorker() {
    if ("serviceWorker" in navigator) {
        return navigator.serviceWorker.register('/brevis/app/brevis-service-worker.js', {scope: '/brevis/app/'});
    } else {
        return Promise.resolve()
    }
}