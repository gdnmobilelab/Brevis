import Inferno from 'inferno';
import Component from 'inferno-component';
import { Router, Route, IndexRoute } from 'inferno-router';
import createHashHistory from 'history/createHashHistory';
import { Provider, connect } from 'inferno-redux';
import classnames from 'classnames';
import ZingTouch from 'zingtouch';

const browserHistory = createHashHistory();

import registerServiceWorker from './util/register-service-worker';
import messaging from './util/firebase-messaging';

import store from './store';
import BrevisDB from './db/BrevisDB';

import BrevisHeader from './components/shared/BrevisHeader';
import BrevisSettings from './components/shared/BrevisSettings';
import BrevisNotices from './components/shared/BrevisNotices';

import BrevisBrief from './components/brief/BrevisBrief';
import BrevisContent from './components/content/BrevisContent';
import BrevisOnboarding from './components/onboarding/BrevisOnboarding';
import BrevisProfile from './components/profile/BrevisProfile';

import GeoLocationService from './services/GeoLocationService';
import ObservationService from './services/ObservationService';
import UserService from './services/UserService';
import PushService from './services/PushService';

import sendMessage from './util/service-worker-send-message';
import refreshContent from './util/refresh-content';

registerServiceWorker()
    .then((registration) => {
        messaging.useServiceWorker(registration);

        messaging.requestPermission()
            .catch(function(err) {
                console.log('Unable to get permission to notify.', err);
            })
            .then(() => {
                messaging.getToken()
                    .then((token) => {
                        console.log(`Push Token: ${token}`);
                        PushService.savePushToken(token);
                    });
            });

        messaging.onTokenRefresh(() => {
            console.log(`Refresh Push Token`);
            messaging.getToken()
                .then((token) => {
                    console.log(`Push Token: ${token}`);
                    PushService.savePushToken(token);
                });
        })
    });

window.addEventListener('online', () => {
    // Todo: race condition.
    sendMessage({
        command: 'sync',
        data: {
            sync: 'syncContent'
        }
    });

    sendMessage({
        command: 'sync',
        data: {
            sync: 'syncUser'
        }
    });

    sendMessage({
        command: 'sync',
        data: {
            sync: 'syncObservationClick'
        }
    })
});

class App extends Component {
    constructor(props) {
        super(props);

        this.touchRegion = new ZingTouch.Region(document.body, false, false);
    }

    showSettings() {
        store.dispatch({
            type: 'TOGGLE_SETTINGS'
        })
    }

    onSettingsClick() {
        store.dispatch({
            type: 'CLOSE_SETTINGS'
        });
    }

    onLogout() {
        sendMessage({
            command: 'cachePurge',
            data: {}
        });

        window.location = '/brevis/app/logout/';
        return false;
    }

    getChildContext() {
        return {
            touchRegion: this.touchRegion
        }
    }

    componentDidMount() {
        const store = this.context.store;

        store.dispatch({type: 'REQUEST_BRIEF_CONTENT'});

        // Todo this: move this to app start?
        // Todo: better online/offline check?
        return (navigator && navigator.onLine ? refreshContent() : Promise.reject('Currently offline'))
            .then((resp) => {
                store.dispatch({
                    type: 'RECEIVED_USER',
                    user: resp.user
                });

                return resp.content;
            })
            .catch((err) => {
                console.error(err);
                // Catching here means we're probably offline
                return BrevisDB.findCurrentUserId()
                    .then((userId => {
                        store.dispatch({
                            type: 'RECEIVED_USER_ID',
                            userId: userId
                        });

                        return BrevisDB.fetchLatestContentPackage(userId)
                    }));
            })
            .catch((err) => {
                console.error(err);
                // Catching here means something went off the rails
                return [];
            })
            .then((content) => {
                store.dispatch({
                    type: 'RECEIVED_BRIEF_CONTENT',
                    userContent: content
                })
            })
            .then(() => {
                // Sync our user preferences and observations
                UserService.syncActiveUser();
                ObservationService.syncObservationClicks();
                return true;
            })
            .then(() => {
                // Dead last, this blocks fetch.
                GeoLocationService.getLatLng()
                    .then((latlong) => {
                        return ObservationService.location(latlong);
                    })
                    .catch((err) => {
                        console.log(err);
                    })
            })
    }

    render() {
        let wrapperClass = classnames('brevis-wrapper', {'settings-active': this.props.settings.active});

        return (
            <div>
                <BrevisHeader onSettingsButtonClick={this.showSettings.bind(this)} />
                <div className={wrapperClass}>
                    <BrevisSettings
                        onLogout={this.onLogout.bind(this)}
                        onClick={this.onSettingsClick.bind(this)}
                    />
                    <div className="main">
                        {this.props.children}
                    </div>
                </div>
                <BrevisNotices />
            </div>
        )
    }
}

let app = connect(state => {
    return {
        settings: state.settings
    }
})(App);

const routes = (
    <Provider store={ store }>
        <Router history={ browserHistory }>
            <Route component={ app }>
                <IndexRoute component={ BrevisBrief } />
                <Route path="/content/:contentId" component={ BrevisContent } />
                <Route path="/onboarding" component={ BrevisOnboarding } />
                <Route path="/profile" component={ BrevisProfile } />
            </Route>
        </Router>
    </Provider>
);

Inferno.render(routes, document.getElementById('brevis'));