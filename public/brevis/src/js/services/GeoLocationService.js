
// Up to the caller to verify geolocation support
class GeoLocationService {
    hasGeolocation() {
        return "geolocation" in navigator
    }

    getLatLng() {
        if (!this.hasGeolocation()) { return Promise.reject() }

        return new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(function(position) {
                resolve({lat: position.coords.latitude, lng: position.coords.longitude});
            }, (err) => {
                reject(err);
            });
        });
    }

    watchLatLng() {
        if (!this.hasGeolocation()) { return Promise.reject() }

        return new Promise((resolve, reject) => {
            navigator.geolocation.watchPosition(function (position) {
                resolve({lat: position.coords.latitude, lng: position.coords.longitude});
            });
        });
    }
}

export default new GeoLocationService();