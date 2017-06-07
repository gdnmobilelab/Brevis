import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';

import BrevisGenericHeader from '../shared/BrevisGenericHeader';

class BrevisAbout extends Component {
    constructor(props) {
        super(props)
    }

    componentDidMount() {
        const store = this.context.store;
    }

    render() {
        return (
            <div className="brevis-about">
                <BrevisGenericHeader title="About Brevis" />
                <div className="about-body">
                    <div className="about-section">
                        <div className="about-header">Why We Built It</div>
                        <div className="about-description">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque porta lacus lectus, at posuere diam sodales in. Donec felis nisl, lacinia non mauris a, ultricies bibendum lectus. Pellentesque ac elit id erat vehicula feugiat at sed augue.</div>
                    </div>
                    <div className="about-section">
                        <div className="about-header">Contact</div>
                        <div className="about-description">Reach out to us by email at <a href="mailto:mobilelab@gdnmobilelab.com">mobilelab@gdnmobilelab.com</a></div>
                    </div>
                    <div className="about-section">
                        <div className="about-header">Data Usage</div>
                        <div className="about-description">Info about much data is downloaded now, regularly, and when, and how.</div>
                    </div>
                    <div className="about-section">
                        <div className="about-header">Why We Built It</div>
                        <div className="about-description">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque porta lacus lectus, at posuere diam sodales in. Donec felis nisl, lacinia non mauris a, ultricies bibendum lectus. Pellentesque ac elit id erat vehicula feugiat at sed augue.</div>
                    </div>
                    <div className="about-section">
                        <div className="about-header">Updates</div>
                        <div className="about-description about-description-light">We keep a log of all our app updates.</div>
                    </div>
                    <div className="about-section">
                        <div className="about-header">Terms</div>
                        <div className="about-description about-description-light">Are there any <a href="#">Terms and Conditions</a></div>
                    </div>
                </div>
            </div>
        )
    }
}

export default BrevisAbout;