import Inferno from 'inferno';
import Component from 'inferno-component';

class BrevisContentCardLoading extends Component {
    constructor(props) {
        super(props);
    }


    render() {
        return (
            <div className="content-wrapper">
                <div className="content">
                    <div className="progress" style={{height: '22px'}}>
                        <div className="indeterminate"></div>
                    </div>
                    <div className="progress" style={{height: '32px'}}>
                        <div className="indeterminate"></div>
                    </div>
                </div>
            </div>
        )
    }
}

export default BrevisContentCardLoading