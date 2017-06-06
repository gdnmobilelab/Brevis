import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';


class BrevisSidebar extends Component {
    constructor(props) {
        super(props)
    }

    componentDidMount() {
        const store = this.context.store;
    }

    render() {
        return (
            <div className="brevis-sidebar">
                <ul>
                    <li><Link onClick={this.props.onClick} to="/" activeClassName="active">Content</Link></li>
                    <li><Link onClick={this.props.onClick} to="/profile" activeClassName="active">Profile</Link></li>
                    <li><div className="link" onClick={this.props.onLogout}>Logout</div></li>
                </ul>
            </div>
        )
    }
}

export default BrevisSidebar
