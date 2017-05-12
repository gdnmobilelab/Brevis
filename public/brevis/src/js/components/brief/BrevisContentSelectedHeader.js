import Inferno from 'inferno';
import Component from 'inferno-component';

class BrevisContentSelectedHeader extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <nav className="header-content-selected">
                <div className="inner-header">
                    <div className="arrow-back">
                        <svg onClick={this.props.onBackClick} fill="#FFFFFF" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                            <path d="M0 0h24v24H0z" fill="none"/>
                            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
                        </svg>
                    </div>
                    <div className="header-content-selected-action-bar">
                        <svg onClick={this.props.onMarkReadContentClick} fill="#FFFFFF" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.54 5.23l-1.39-1.68C18.88 3.21 18.47 3 18 3H6c-.47 0-.88.21-1.16.55L3.46 5.23C3.17 5.57 3 6.02 3 6.5V19c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6.5c0-.48-.17-.93-.46-1.27zM12 17.5L6.5 12H10v-2h4v2h3.5L12 17.5zM5.12 5l.81-1h12l.94 1H5.12z"/>
                            <path d="M0 0h24v24H0z" fill="none"/>
                        </svg>
                    </div>
                </div>
            </nav>
        )
    }
}

export default BrevisContentSelectedHeader
