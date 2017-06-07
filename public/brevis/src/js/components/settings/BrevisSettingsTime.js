import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import update from 'immutability-helper';

import BrevisDB from '../../db/BrevisDB';

import UserService from '../../services/UserService';
import NoticesService from '../../services/NoticesService';

class BrevisSettingsTime extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {

    }

    _setMorningCommuteLength(commuteLength) {
        this.props.dispatch({
            type: 'SET_SETTINGS_MORNING_COMMUTE_LENGTH',
            morningCommuteLength: commuteLength
        })
    }

    _setEveningCommuteLength(commuteLength) {
        this.props.dispatch({
            type: 'SET_SETTINGS_EVENING_COMMUTE_LENGTH',
            eveningCommuteLength: commuteLength
        })
    }

    _saveSettings() {
        this.props.dispatch({
            type: 'SAVE_USER'
        });

        let updatedUser = update(this.props.user, {
            morningCommuteLength: { $set: parseInt(this.props.morningCommuteLength || this.props.user.morningCommuteLength, 10) },
            eveningCommuteLength: { $set: parseInt(this.props.eveningCommuteLength || this.props.user.eveningCommuteLength, 10) }
        });

        UserService
            .updateCurrentUser(updatedUser)
            .then(() => {

                this.props.dispatch({
                    type: 'SAVED_USER',
                    user: updatedUser
                });

                NoticesService.createSuccessNotice('Saved.');
            })
            .catch((err) => {
                return BrevisDB.upsertUserToSync(updatedUser)
                    .then(() => {
                        store.dispatch({
                            type: 'SAVED_USER',
                            user: updatedUser
                        });

                        NoticesService.createSuccessNotice('Saved.');
                    });
            })
            .catch((err) => {
                this.props.dispatch({
                    type: 'SAVED_USER_FAILED'
                });

                NoticesService.createErrorNotice('Failed to save.')
            })
    }

    render() {

        return (
            <div className="brevis-settings-time">
                <div className="brevis-settings-time brevis-settings-time-description">Brevis recommends an number of articles based on how long your commute time is.</div>
                <div className="brevis-settings-time brevis-settings-time-timebox-wrapper">
                    <div className="brevis-settings-time brevis-settings-time-timebox">
                        <div className="brevis-settings-time brevis-settings-time-timebox-row">
                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-descriptor">Morning</div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-button-wrapper">
                                <div onClick={this._setMorningCommuteLength.bind(this, this.props.morningCommuteLength - 1)} className="brevis-settings-time brevis-settings-time-timebox-button">
                                    <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M19 13H5v-2h14v2z"/>
                                        <path d="M0 0h24v24H0z" fill="none"/>
                                    </svg>
                                </div>
                            </div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-text">{this.props.morningCommuteLength} MIN</div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-button-wrapper">
                                <div onClick={this._setMorningCommuteLength.bind(this, this.props.morningCommuteLength + 1)}  className="brevis-settings-time brevis-settings-time-timebox-button">
                                    <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                                        <path d="M0 0h24v24H0z" fill="none"/>
                                    </svg>
                                </div>
                            </div>
                        </div>
                        <div className="brevis-settings-time brevis-settings-time-timebox-row">
                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-descriptor">Evening</div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-button-wrapper">
                                <div onClick={this._setEveningCommuteLength.bind(this, this.props.eveningCommuteLength - 1)} className="brevis-settings-time brevis-settings-time-timebox-button">
                                    <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M19 13H5v-2h14v2z"/>
                                        <path d="M0 0h24v24H0z" fill="none"/>
                                    </svg>
                                </div>
                            </div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-text">{this.props.eveningCommuteLength} MIN</div>

                            <div className="brevis-settings-time brevis-settings-time-timebox-row-item brevis-settings-time-timebox-button-wrapper">
                                <div onClick={this._setEveningCommuteLength.bind(this, this.props.eveningCommuteLength + 1)} className="brevis-settings-time brevis-settings-time-timebox-button">
                                    <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                                        <path d="M0 0h24v24H0z" fill="none"/>
                                    </svg>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="brevis-settings-time brevis-settings-time-save-wrapper">
                    <a href="javascript:void(0);" onClick={this._saveSettings.bind(this)}>Save</a>
                </div>
            </div>
        )
    }
}

export default connect(state => {
    return {
        user: state.user.current,
        eveningCommuteLength: state.settings.eveningCommuteLength,
        morningCommuteLength: state.settings.morningCommuteLength
    }
})(BrevisSettingsTime)