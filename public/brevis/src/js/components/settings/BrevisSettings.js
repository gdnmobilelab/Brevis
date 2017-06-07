import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';
import { connect } from 'inferno-redux';

import BrevisSettingsCategories from './BrevisSettingsCategories';
import BrevisSettingsTime from './BrevisSettingsTime';

import BrevisGenericHeader from '../shared/BrevisGenericHeader';

import classnames from 'classnames';

import UserContentService from '../../services/UserContentService';

class BrevisSettings extends Component {
    constructor(props) {
        super(props);

        this.state = {
            activeMenu: props.params.section ? props.params.section : 'categories'
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            activeMenu: nextProps.params.section ? nextProps.params.section : 'categories'
        })
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

        let toRender = this.state.activeMenu === 'categories' ?
            <BrevisSettingsCategories categories={this.props.categories} />
            : <BrevisSettingsTime />;

        return (
            <div className="brevis-settings brevis-settings-wrapper">
                <BrevisGenericHeader title="Settings" />
                <div className="brevis-settings brevis-settings-main">
                    <div className="brevis-settings brevis-settings-toggle-bar">
                        <div onClick={() => {
                           this.context.router.push('/settings/categories')
                        }} className={classnames('brevis-settings brevis-settings-toggle-item', {'active': this.state.activeMenu === 'categories'})}>Categories</div>
                        <div onClick={() => {
                            this.context.router.push('/settings/time')
                        }} className={classnames('brevis-settings brevis-settings-toggle-item', {'active': this.state.activeMenu === 'time'})}>Time</div>
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