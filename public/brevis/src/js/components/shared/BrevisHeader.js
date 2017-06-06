import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';
import { connect } from 'inferno-redux';
import classnames from 'classnames';

class BrevisHeader extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        let classNames = classnames('clickable', {'hidden': !this.props.header.showMenu});

        return (
            <nav className="header">
                <div className="inner-header">
                    <svg className={classNames} onClick={this.props.onSidebarButtonClick} fill="#9b9b9b" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 0h24v24H0z" fill="none"/>
                        <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
                    </svg>
                    <h1><Link to="/">Your Readings</Link></h1>
                    <div className="settings-text-wrapper">
                        <Link to="/settings">Settings</Link>
                    </div>
                </div>
            </nav>
        )
    }
}

export default connect(state => {
    return {
        header: state.header
    }
})(BrevisHeader)

