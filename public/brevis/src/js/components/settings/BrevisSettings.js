import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';
import { connect } from 'inferno-redux';

import BrevisSettingsCategories from './BrevisSettingsCategories';
import BrevisSettingsTime from './BrevisSettingsTime';

import classnames from 'classnames';

import UserContentService from '../../services/UserContentService';

class BrevisSettings extends Component {
    constructor(props) {
        super(props);

        this.state = {
            activeMenu: 'CATEGORIES'
        }
    }

    componentDidMount() {
        UserContentService.findClickedContent()
            .then((resp) => {
                let categories = resp.content.reduce((acc, content) => {
                    content.tags.forEach((tag) => {
                        if(acc[tag.webTitle]) {
                            acc[tag.webTitle] += 1;
                        } else {
                            acc[tag.webTitle] = 1;
                        }
                    });

                    return acc;
                }, {});

                let categoriesWithCount = Object.keys(categories).map((cat) => {
                    return {
                        name: cat,
                        count: categories[cat]
                    }
                });

                this.props.dispatch({
                    type: 'FETCHED_USER_SETTINGS_CATEGORIES',
                    categories: categoriesWithCount
                })
            })
    }

    render() {

        let toRender = this.state.activeMenu === 'CATEGORIES' ? <BrevisSettingsCategories categories={this.props.categories} /> : <BrevisSettingsTime/>;

        return (
            <div className="brevis-settings brevis-settings-wrapper">
                <nav className="header">
                    <div className="inner-header">
                        <svg onClick={() => {
                            this.context.router.push('/');
                        }} className="clickable" fill="#9b9b9b" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                            <path d="M15.41 16.09l-4.58-4.59 4.58-4.59L14 5.5l-6 6 6 6z"/>
                            <path d="M0-.5h24v24H0z" fill="none"/>
                        </svg>
                        <h1><Link to="/settings">Settings</Link></h1>
                    </div>
                </nav>
                <div className="brevis-settings brevis-settings-main">
                    <div className="brevis-settings brevis-settings-toggle-bar">
                        <div className={classnames('brevis-settings brevis-settings-toggle-item', {'active': this.state.activeMenu === 'CATEGORIES'})}>Categories</div>
                        <div className={classnames('brevis-settings brevis-settings-toggle-item', {'active': this.state.activeMenu === 'TIME'})}>Time</div>
                    </div>
                </div>
                {toRender}
            </div>
        )
    }
}

export default connect(state => {
    return {
        activeMenu: state.settings.activeMenu,
        categories: state.settings.categories,
        loading: state.settings.loading
    }
})(BrevisSettings)