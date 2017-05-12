import Inferno from 'inferno';
import Component from 'inferno-component';

class ProgressBar extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        let progress = this.props.succeeded + this.props.failed;
        let progressPercentage = Math.round((progress / (this.props.total - 1)) * 100);

        return (
            <div className="content-loading">
                <div className="progress">
                    <div className="determinate" style={{width: `${progressPercentage}%`}}></div>
                </div>
            </div>
        )
    }
}

export default ProgressBar
