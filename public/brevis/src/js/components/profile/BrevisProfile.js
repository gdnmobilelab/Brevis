import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import update from 'immutability-helper';

import BrevisDB from '../../db/BrevisDB';

import BrevisHeader from '../shared/BrevisHeader';
import BrevisLoading from '../shared/BrevisLoading';

import UserService from '../../services/UserService';
import NoticesService from '../../services/NoticesService';

class BrevisProfile extends Component {
    constructor(props) {
        super(props)
    }

    _showSettings() {
        const store = this.context.store;

        store.dispatch({
            type: 'TOGGLE_SETTINGS'
        })
    }

    loadUser() {
        const store = this.context.store;

        store.dispatch({
            type: 'REQUEST_USER'
        });

        (navigator && navigator.onLine ? UserService.getCurrentUser() : Promise.reject(new Error('Currently offline')))
            .then((user) => {
                BrevisDB.makeUserCurrentUser(user);

                store.dispatch({
                    type: 'RECEIVED_USER',
                    user: user
                })
            })
            .catch((err) => {
                console.error(err);

                return BrevisDB.findCurrentUser()
                    .then((user) => {
                        store.dispatch({
                            type: 'RECEIVED_USER',
                            user: user
                        })
                    })
            })
            .catch((err) => {
                console.error(err);

                store.dispatch({
                    type: 'REQUEST_USER_FAILED'
                });

                NoticesService.createErrorNotice('Failed to retrieve user.');
            })
    }

    componentDidMount() {
        if (this.props.user.id || this.props.loading) {
            return;
        } else {
            this.loadUser();
        }
    }

    updateForm(prop, val) {
        const store = this.context.store;

        store.dispatch({
            type: 'UPDATE_PROFILE_FORM',
            form: update(this.props.form, {
                [prop]: { $set: val }
            })
        })
    }

    onSubmit() {
        const store = this.context.store;

        store.dispatch({
            type: 'SAVE_USER'
        });

        let updatedUser = update(this.props.user, {
            morningCommuteLength: { $set: parseInt(this.props.form.morningCommuteLength || this.props.user.morningCommuteLength, 10) },
            eveningCommuteLength: { $set: parseInt(this.props.form.eveningCommuteLength || this.props.user.eveningCommuteLength, 10) }
        });

        UserService
            .updateCurrentUser(updatedUser)
            .then(() => {

                store.dispatch({
                    type: 'SAVED_USER',
                    user: updatedUser
                });

                NoticesService.createSuccessNotice('Profile saved.');
            })
            .catch((err) => {
                return BrevisDB.upsertUserToSync(updatedUser)
                    .then(() => {
                        store.dispatch({
                            type: 'SAVED_USER',
                            user: updatedUser
                        });

                        NoticesService.createSuccessNotice('Profile saved.');
                    });
            })
            .catch((err) => {
                store.dispatch({
                    type: 'SAVED_USER_FAILED'
                });

                NoticesService.createErrorNotice('Failed to save.')
            })

    }

    render() {
        let profileForm = (
            <div>
                <h2 className="profile-title title">Profile</h2>
                <div className="form">
                    <p>Morning commute length (in minutes)?</p>
                    <div className="form-group text">
                        <input id="commute-length"
                               type="number"
                               onChange={(e) => this.updateForm('morningCommuteLength', e.target.value)}
                               value={this.props.form.morningCommuteLength || this.props.user.morningCommuteLength || ''} />
                        <label htmlFor="commute-length" className="active">Morning Commute Length (in minutes)</label>
                    </div>
                    <p>Evening commute length (in minutes)?</p>
                    <div className="form-group text">
                        <input id="commute-length"
                               type="number"
                               onChange={(e) => this.updateForm('eveningCommuteLength', e.target.value)}
                               value={this.props.form.eveningCommuteLength || this.props.user.eveningCommuteLength || ''} />
                        <label htmlFor="commute-length" className="active">Evening Commute Length (in minutes)</label>
                    </div>
                    <div className="pull-right">
                        <div className="loader hidden">
                            <svg className="circular" viewBox="25 25 50 50">
                                <circle className="path" cx="50" cy="50" r="20" fill="none" strokeWidth="2" strokeMiterlimit="10"/>
                            </svg>
                        </div>
                        <button onClick={this.onSubmit.bind(this)} className="btn submit" name="action">Let's Go!</button>
                    </div>
                </div>
            </div>
        );

        let toShow = <div></div>;

        if (this.props.user.failed && !this.props.loading) {
            toShow = (
                <div>
                    <h2>Hm.</h2>
                    <p>We're not able to retrieve your user profile information at the moment. Please try again in a bit.</p>
                </div>
            )
        } else if (!this.props.user.id || this.props.loading) {
            toShow = <BrevisLoading/>
        } else {
            toShow = profileForm;
        }

        return (
            <div className="brevis-profile">
                <BrevisHeader onSettingsButtonClick={this._showSettings.bind(this)} />
                {toShow}
            </div>
        )
    }
}

export default connect(state => {
    return {
        loading: state.user.loading,
        user: state.user.current,
        form: state.pages.profile.form
    }
})(BrevisProfile)
