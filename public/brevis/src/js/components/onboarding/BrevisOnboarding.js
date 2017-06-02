import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import update from 'immutability-helper';

import BrevisDB from '../../db/BrevisDB';

import UserService from '../../services/UserService';
import GeoLocationService from '../../services/GeoLocationService';
import ObservationService from '../../services/ObservationService';
import PushService from '../../services/PushService';
import NoticesService from '../../services/NoticesService';
import UserContentService from '../../services/UserContentService';

import registerServiceWorker from '../../util/register-service-worker';
import messaging from '../../util/firebase-messaging';

class BrevisOnboardingStart extends Component {
    render() {
        return (
            <div className="brevis-onboarding-start onboarding-outer-wrapper">
                <div className="brevis-onboarding-start onboarding-inner-wrapper">
                    <div className="brevis-onboarding-global onboarding-header">Welcome to Brevis,</div>
                    <div className="brevis-onboarding-global onboarding-header">{this.props.user.firstName}</div>
                    <div className="brevis-onboarding-global onboarding-body">For you to enjoy Brevis, we need you to setup your profile in 3 quick steps.</div>
                </div>
                <div className="brevis-onboarding-start onboarding-button-wrapper">
                    <button color="white" onClick={this.props.onNextScreen} className="brevis-onboarding-start start-button">Start</button>
                </div>
            </div>
        )
    }
}

const ConnectedBrevisOnboardingStart = connect(state => {
    return {
        user: state.user.current
    }
})(BrevisOnboardingStart);


class BrevisOnboardingTime extends Component {
    _nextScreen() {
        let updatedUser = update(this.props.user, {
            morningCommuteLength: { $set: parseInt(this.props.onboarding.morningCommuteLength || this.props.user.morningCommuteLength, 10) },
            eveningCommuteLength: { $set: parseInt(this.props.onboarding.eveningCommuteLength || this.props.user.eveningCommuteLength, 10) }
        });

        UserService.updateCurrentUser(updatedUser)
            .then((updated) => {
                console.warn('updated user');
            })
            .catch((err) => {
                console.warn(err);
            });

        this.props.onNextScreen();
    }

    _setMorningCommuteLength(time) {
        this.props.dispatch({
            type: 'SET_MORNING_COMMUTE_LENGTH',
            morningCommuteLength: time
        })
    }

    _setEveningCommuteLength(time) {
        this.props.dispatch({
            type: 'SET_EVENING_COMMUTE_LENGTH',
            eveningCommuteLength: time
        })
    }

    render() {
        return (
            <div className="brevis-onboarding-time onboarding-outer-wrapper">
                <div className="brevis-onboarding-global onboarding-header-spacer"></div>
                <div className="brevis-onboarding-time onboarding-inner-wrapper">
                    <div className="brevis-onboarding-time onboarding-header-wrapper">
                        <div className="brevis-onboarding-time onboarding-header">Personalize by time</div>
                        <div className="brevis-onboarding-time onboarding-body">By sharing your commute time, Brevis will customize an offline reading list of Guardian articles timed to your morning and evening ride.</div>
                    </div>
                    <div className="brevis-onboarding-time onboarding-timebox-wrapper">
                        <div className="brevis-onboarding-time onboarding-timebox">
                            <div className="brevis-onboarding-time onboarding-timebox-row">
                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-descriptor">Morning</div>

                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-button-wrapper">
                                    <div onClick={this._setMorningCommuteLength.bind(this, this.props.onboarding.morningCommuteLength - 1)} className="brevis-onboarding-time onboarding-timebox-button">
                                        <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M19 13H5v-2h14v2z"/>
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                        </svg>
                                    </div>
                                </div>

                                <div className="brevis-onboarding-time onboarding-timebox-text">{this.props.onboarding.morningCommuteLength} MIN</div>

                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-button-wrapper">
                                    <div onClick={this._setMorningCommuteLength.bind(this, this.props.onboarding.morningCommuteLength + 1)}  className="brevis-onboarding-time onboarding-timebox-button">
                                        <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                        </svg>
                                    </div>
                                </div>
                            </div>
                            <div className="brevis-onboarding-time onboarding-timebox-row">
                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-descriptor">Evening</div>

                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-button-wrapper">
                                    <div onClick={this._setEveningCommuteLength.bind(this, this.props.onboarding.eveningCommuteLength - 1)} className="brevis-onboarding-time onboarding-timebox-button">
                                        <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M19 13H5v-2h14v2z"/>
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                        </svg>
                                    </div>
                                </div>

                                <div className="brevis-onboarding-time onboarding-timebox-text">{this.props.onboarding.eveningCommuteLength} MIN</div>

                                <div className="brevis-onboarding-time onboarding-timebox-row-item onboarding-timebox-button-wrapper">
                                    <div onClick={this._setEveningCommuteLength.bind(this, this.props.onboarding.eveningCommuteLength + 1)} className="brevis-onboarding-time onboarding-timebox-button">
                                        <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="brevis-onboarding-time onboarding-time-helper-text">You can always change this later</div>
                    </div>

                    <div className="brevis-onboarding-time onboarding-navigation">
                        <div className="brevis-onboarding-global onboarding-navigation-next-button-wrapper">
                            <a onClick={this._nextScreen.bind(this)} href="javascript:void(0);" className="brevis-onboarding-global onboarding-navigation-next-button">Next</a>
                        </div>

                        <div className="brevis-onboarding-global onboarding-navigation-dot-wrapper">
                            <div className="brevis-onboarding-global onboarding-navigation-dot-text">1 of 3</div>
                            <div className="brevis-onboarding-global onboarding-navigation-dots">
                                <div className="brevis-onboarding-global onboarding-navigation-dot onboarding-navigation-active-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

const ConnectedBrevisOnboardingTime = connect(state => {
    return {
        onboarding: state.onboarding,
        user: state.user.current
    }
})(BrevisOnboardingTime);

class BrevisOnboardingNotifications extends Component {
    _requestNotificationPermission() {
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
    }

    render() {
        let notificationStatusToShow = PushService.haveUserPermission() ? (
            <div>
                <div className="brevis-onboarding-notifications onboarding-notifications-enabled-text">Notifications are enabled.</div>
                <div className="brevis-onboarding-notifications onboarding-notifications-helper-text">You can always change this later</div>
            </div>
        ) : (
            <div>
                <a onClick={this._requestNotificationPermission.bind(this)} href="javascirpt:void(0)" className="brevis-onboarding-notifications onboarding-enable-notifications-text">Enable Notifications</a>
                <div className="brevis-onboarding-notifications onboarding-notifications-helper-text">You can always change this later</div>
            </div>
        );

        return (
            <div className="brevis-onboarding-notifications onboarding-outer-wrapper">
                <div className="brevis-onboarding-global onboarding-header-spacer"></div>
                <div className="brevis-onboarding-notifications onboarding-inner-wrapper">
                    <div className="brevis-onboarding-notifications onboarding-header-wrapper">
                        <div className="brevis-onboarding-notifications onboarding-header">Enable notifications</div>
                        <div className="brevis-onboarding-notifications onboarding-body">By enabling notifications, Brevis can let you know when your commute read is ready.</div>
                    </div>

                    <div className="brevis-onboarding-notifications onboarding-enable-notifications-text-wrapper">
                        {notificationStatusToShow}
                    </div>

                    <div className="brevis-onboarding-notifications onboarding-navigation">
                        <div className="brevis-onboarding-global onboarding-navigation-next-button-wrapper">
                            <a onClick={this.props.onNextScreen} href="javascript:void(0);" className="brevis-onboarding-global onboarding-navigation-next-button">Next</a>
                        </div>

                        <div className="brevis-onboarding-global onboarding-navigation-dot-wrapper">
                            <div className="brevis-onboarding-global onboarding-navigation-dot-text">2 of 3</div>
                            <div className="brevis-onboarding-global onboarding-navigation-dots">
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot onboarding-navigation-active-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

class BrevisOnboardingLocation extends Component {
    constructor(props) {
        super(props);

        this.state = {
            havePermission: false
        }
    }

    componentDidMount() {
        GeoLocationService.haveUserPermission()
            .then((result) => {
                this.setState({
                    havePermission: result
                })
            })
    }

    _requestLocationPermissions() {
        GeoLocationService.getLatLng()
            .then((latlng) => {
                ObservationService.location(latlng);
            })
    }

    render() {
        let locationStatusToShow = this.state.havePermission ? (
            <div>
                <div className="brevis-onboarding-location onboarding-location-enabled-text">Location Tracking is enabled.</div>
                <div className="brevis-onboarding-location onboarding-location-helper-text">You can always change this later</div>
            </div>
        ) : (
            <div>
                <a onClick={this._requestLocationPermissions.bind(this)} href="javascirpt:void(0)" className="brevis-onboarding-location onboarding-enable-location-text">Enable Location Tracking</a>
                <div className="brevis-onboarding-location onboarding-location-helper-text">You can always change this later</div>
            </div>
        );

        return (
            <div className="brevis-onboarding-location onboarding-outer-wrapper">
                <div className="brevis-onboarding-global onboarding-header-spacer"></div>
                <div className="brevis-onboarding-location onboarding-inner-wrapper">
                    <div className="brevis-onboarding-location onboarding-header-wrapper">
                        <div className="brevis-onboarding-location onboarding-header">Enable location tracking</div>
                        <div className="brevis-onboarding-location onboarding-body">By enabling Brevis to track your device location, we can start building a new upcoming feature - reading suggestions based on device locations.</div>
                    </div>

                    <div className="brevis-onboarding-location onboarding-enable-location-text-wrapper">
                        {locationStatusToShow}
                    </div>

                    <div className="brevis-onboarding-location onboarding-navigation">
                        <div className="brevis-onboarding-global onboarding-navigation-next-button-wrapper">
                            <a onClick={this.props.onNextScreen} href="javascript:void(0);" className="brevis-onboarding-global onboarding-navigation-next-button">Next</a>
                        </div>

                        <div className="brevis-onboarding-global onboarding-navigation-dot-wrapper">
                            <div className="brevis-onboarding-global onboarding-navigation-dot-text">3 of 3</div>
                            <div className="brevis-onboarding-global onboarding-navigation-dots">
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot"></div>
                                <div className="brevis-onboarding-global onboarding-navigation-dot onboarding-navigation-active-dot"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}


class BrevisOnboardingFinish extends Component {
    _onStart() {
        const store = this.context.store;

        store.dispatch({
            type: 'SHOW_SETTINGS_MENU'
        });

        NoticesService.createSuccessNotice('Saved.');

        this.context.router.push('/');

        store.dispatch({type: 'REQUEST_BRIEF_CONTENT'});

        // Todo this: move this to app start?
        UserContentService
            .fetchContent()
            .then((resp) => {
                let contents = resp.contents;
                let user = resp.user;
                let imagesInContent = resp.images;
                let brief = resp.brief;

                //
                // imagesInContent.forEach((images) => {
                //     images.forEach((imageURL) => {
                //         cacheURLOffline(imageURL);
                //     })
                // });

                store.dispatch({
                    type: 'RECEIVED_USER',
                    user: user
                });

                let toSave = [
                    BrevisDB.makeUserCurrentUser(user),
                    contents.map((content) => {
                        BrevisDB.upsertContent(content, user.id)
                    })
                ];

                toSave.push(BrevisDB.insertBrief(brief, contents, user.id));

                return Promise.all(toSave)
                    .then((success) => {
                        return BrevisDB.fetchLatestContentPackage(user.id)
                    })
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
            .catch(() => {
                store.dispatch({
                    type: 'ONBOARDING_SAVE_FAILED'
                });

                NoticesService.createErrorNotice('Failed to save.')
            })
    }


    render() {
        return (
            <div className="brevis-onboarding-finish onboarding-outer-wrapper">
                <div className="brevis-onboarding-finish onboarding-inner-wrapper">
                    <div className="brevis-onboarding-finish onboarding-text-wrapper">
                        <div className="brevis-onboarding-global onboarding-header">You're all set!</div>
                        <div className="brevis-onboarding-global onboarding-body">We'll ping you in the morn and eve when your commute readings are ready. Just hit the notification link to arrive right here.</div>
                        <div className="brevis-onboarding-global onboarding-body">It's not commute time yet, so here's a sample package of readings you can enjoy right now.</div>
                    </div>
                    <div className="brevis-onboarding-finish onboarding-button-wrapper">
                        <button onClick={this._onStart.bind(this)} className="brevis-onboarding-finish onboarding-finish-button">Get Started</button>
                    </div>
                </div>
            </div>
        )
    }
}

class BrevisOnboarding extends Component {
    constructor(props) {
        super(props);

        this.state = {
            screen: 0
        }
    }

    _nextScreen() {
        this.setState({
            screen: this.state.screen + 1
        })
    }

    render() {
        let transformBy = 100 * this.state.screen;

        return (
            <div className="brevis-onboarding onboarding-slides">
                <div className="brevis-onboarding onboarding-slide" style={{transform: `translate3d(-${transformBy}%,0,0)`}}>
                    <ConnectedBrevisOnboardingStart
                        onNextScreen={this._nextScreen.bind(this) }
                    />
                </div>
                <div className="brevis-onboarding onboarding-slide" style={{transform: `translate3d(-${transformBy}%,0,0)`}}>
                    <ConnectedBrevisOnboardingTime
                        onNextScreen={this._nextScreen.bind(this) }
                    />
                </div>
                <div className="brevis-onboarding onboarding-slide" style={{transform: `translate3d(-${transformBy}%,0,0)`}}>
                    <BrevisOnboardingNotifications
                        onNextScreen={this._nextScreen.bind(this) }
                    />
                </div>
                <div className="brevis-onboarding onboarding-slide" style={{transform: `translate3d(-${transformBy}%,0,0)`}}>
                    <BrevisOnboardingLocation
                        onNextScreen={this._nextScreen.bind(this) }
                    />
                </div>
                <div className="brevis-onboarding onboarding-slide" style={{transform: `translate3d(-${transformBy}%,0,0)`}}>
                    <BrevisOnboardingFinish />
                </div>
            </div>
        )
    }
}

export default BrevisOnboarding
