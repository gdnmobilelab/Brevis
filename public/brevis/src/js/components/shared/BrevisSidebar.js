import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';
import { connect } from 'inferno-redux'


class BrevisSidebar extends Component {
    constructor(props) {
        super(props)
    }

    componentDidMount() {
        const store = this.context.store;
    }

    _formatName(user) {
        let name = '';

        if (user.firstName && !user.lastName) {
            name = user.firstName;
        } else if (user.firstName && user.lastName) {
            name = `${user.firstName} ${user.lastName}`
        } else {
            name = 'Account';
        }

        return name;
    }

    _onLinkClick() {
        this.props.dispatch({
            type: 'CLOSE_SIDEBAR'
        })
    }

    _onClose() {
        this.props.dispatch({
            type: 'CLOSE_SIDEBAR'
        })
    }

    render() {
        return (
            <div className="brevis-sidebar">
                <div className="sidebar-menu">
                    <svg className="clickable" onClick={this._onClose.bind(this)} fill="#FFFFFF" height="32" viewBox="0 0 24 24" width="32" xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 0h24v24H0z" fill="none"/>
                        <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
                    </svg>
                </div>
                <ul>
                    <li className="sidebar-block">
                        <div className="sidebar-block-header-wrapper">
                            <div className="sidebar-profile-icon"></div>
                            <div className="sidebar-profile-name sidebar-header">
                                <Link to="/profile" onClick={this._onLinkClick.bind(this)}>{this._formatName(this.props.user)}</Link>
                            </div>
                        </div>
                        <div className="sidebar-description">View account profile and permissions</div>
                    </li>
                    <li className="sidebar-block">
                        <div className="sidebar-block-header-wrapper">
                            <div className="sidebar-feedback-icon">
                                <svg fill="#FFFFFF" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                                    <path d="M0 0h24v24H0z" fill="none"/>
                                </svg>
                            </div>
                            <div className="sidebar-header">
                                <Link to="/feedback" onClick={this._onLinkClick.bind(this)}>Feedback</Link>
                            </div>
                        </div>
                        <div className="sidebar-description">How is  Brevis? Take a quick survey</div>
                    </li>
                    <li className="sidebar-block sidebar-about-brevis-block">
                        <div className="sidebar-description">
                            <div className="sidebar-about-text-wrapper">
                                <Link onClick={this._onLinkClick.bind(this)} to="/about">About Breivs</Link>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        )
    }
}

export default connect((state) => {
    return {
        user: state.user.current
    }
})(BrevisSidebar)
