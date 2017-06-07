import Inferno from 'inferno';
import Component from 'inferno-component';

class BrevisGenericHeader extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <nav className="header">
                <div className="inner-header">
                    <svg onClick={() => {
                        this.context.router.push('/');
                    }} className="clickable" fill="#9b9b9b" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M15.41 16.09l-4.58-4.59 4.58-4.59L14 5.5l-6 6 6 6z"/>
                        <path d="M0-.5h24v24H0z" fill="none"/>
                    </svg>
                    <h1>{this.props.title}</h1>
                </div>
            </nav>
        )
    }
}

export default BrevisGenericHeader;