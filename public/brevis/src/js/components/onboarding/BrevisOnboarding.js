import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import update from 'immutability-helper';
import UserService from '../../services/UserService';

import BrevisLoading from '../shared/BrevisLoading';

import BrevisDB from '../../db/BrevisDB';

import cacheURLOffline from '../../util/cache-url-offline';

import NoticesService from '../../services/NoticesService';
import UserContentService from '../../services/UserContentService';

// Todo: fix routing so we're not mounting <App> before onboarding is finished
class BrevisOnboarding extends Component {
    constructor(props) {
        super(props)
    }

    componentDidMount() {
        const store = this.context.store;

        store.dispatch({
            type: 'HIDE_SETTINGS_MENU'
        })
    }

    updateForm(prop, val) {
        const store = this.context.store;

        store.dispatch({
            type: 'UPDATE_ONBOARDING_FORM',
            form: update(this.props.form, {
                [prop]: { $set: val }
            })
        })
    }

    onSubmit() {
        const store = this.context.store;

        store.dispatch({
            type: 'ONBOARDING_SAVE_REQUEST'
        });

        UserService
            .updateCurrentUser(update(this.props.user, {
                morningCommuteLength: { $set: parseInt(this.props.form.morningCommuteLength, 10) },
                eveningCommuteLength: { $set: parseInt(this.props.form.eveningCommuteLength, 10) }
            }))
            .then(() => {
                store.dispatch({
                    type: 'ONBOARDING_SAVE_RECEIVED'
                });

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

                        imagesInContent.forEach((images) => {
                            images.forEach((imageURL) => {
                                cacheURLOffline(imageURL);
                            })
                        });

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
            })
            .catch(() => {
                store.dispatch({
                    type: 'ONBOARDING_SAVE_FAILED'
                });

                NoticesService.createErrorNotice('Failed to save.')
            })

    }

    render() {
        let onboardingForm = (
            <div>
                <h2 className="onboarding-title title">Welcome!</h2>
                <div className="form">
                    <p>What's the average length of your commute in the morning (in minutes)?</p>
                    <div className="form-group text">
                        <input id="commute-length"
                               type="number"
                               onChange={(e) => this.updateForm('morningCommuteLength', e.target.value)}
                               value={this.props.form.morningCommuteLength} />
                        <label htmlFor="commute-length" className="active">Morning Commute Length (in minutes)</label>
                    </div>
                    <p>What about in the evening (in minutes)?</p>
                    <div className="form-group text">
                        <input id="commute-length"
                               type="number"
                               onChange={(e) => this.updateForm('eveningCommuteLength', e.target.value)}
                               value={this.props.form.eveningCommuteLength} />
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

        let toShow =  (this.props.user.id && !this.props.loading) ? onboardingForm : <BrevisLoading />;

        return (
            <div className="onboarding">
                {toShow}
            </div>
        )
    }
}

export default connect(state => {
    return {
        loading: state.pages.onboarding.loading,
        user: state.user.current,
        form: state.pages.onboarding.form
    }
})(BrevisOnboarding)
