import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import classnames from 'classnames';

class BrevisNotices extends Component {
    constructor(props) {
        super(props)
    }

    typeToText(type) {
        switch(type) {
            case 'ERROR':
                return 'ERROR';
            case 'SUCCESS':
                return 'SUCCESS';
            case 'WARN':
                return 'WARN';
        }
    }

    typeToClass(type) {
        switch(type) {
            case 'ERROR':
                return 'notice-error';
            case 'SUCCESS':
                return 'notice-success';
            case 'WARN':
                return 'notice-warn';
        }
    }

    render() {
        let notices = Object.keys(this.props.notices)
            .sort()
            .reverse()
            .map((noticeId) => {
                let noticeTypes = classnames('notice-type', this.typeToClass(this.props.notices[noticeId].type));

                return (
                    <div className="toast">
                        {this.props.notices[noticeId].message}
                        <span className={noticeTypes}>{this.typeToText(this.props.notices[noticeId].type)}</span>
                    </div>
                )
            });

        return (
            <nav id="toast-container">
                {notices}
            </nav>
        )
    }
}

export default connect(state => {
    return {
        notices: state.notices.map
    }
})(BrevisNotices)

