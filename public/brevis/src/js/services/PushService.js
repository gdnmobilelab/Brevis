import store from '../store';
import APIService from './APIService';

// Up to the caller to verify geolocation support
class PushService {
    savePushToken(token) {
        return APIService
            .post(`/api/push/subscriptions`, {
                pushSubscriptionId: token,
                pushSubscriptionType: 'WEB'
            })
            .then((saved) => {
                // todo: maybe implement?
                // store.dispatch({
                //     type: 'SAVED_PUSH_SUBSCRIPTION'
                // })
            })
    }

    haveUserPermission() {
        return Notification.permission === "granted";
    }
}

export default new PushService();