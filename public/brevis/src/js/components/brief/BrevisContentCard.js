import Inferno from 'inferno';
import Component from 'inferno-component';
import { Link } from 'inferno-router';
import classnames from 'classnames';
import ZingTouch from 'zingtouch';

class BrevisContentCard extends Component {
    constructor(props) {
        super(props);

        this.state = {
            markRead: false
        }
    }

    addSwipeActions(el) {
        let region = this.context.touchRegion;

        let detect = new ZingTouch.Swipe({
            // escapeVelocity: 0.05,
            // maxRestTime: 150
        });

        region.bind(el, detect, (e) => {
            if (this.props.meta.read) { return }

            let detail = e.detail,
                currentDirection = detail.data[0].currentDirection;


            if ((currentDirection > 120 && currentDirection < 240)
                && !this.state.markRead) {
                this.setState({
                    markRead: true
                });

                setTimeout(() => {
                    this.props.onMarkContentRead(this.props.content.id);
                    this.setState({
                        markRead: false
                    })
                }, 400);

            }
        });
    }

    render() {
        let content = this.props.content;
        let meta = this.props.meta;

        let classNames = classnames('content-wrapper', this.props.className, {'slide-left': this.state.markRead});
        let contentClassNames = classnames('content');

        let showSwipeInfoText = this.props.meta.read ? '' : (
            <div className="content-sink-left">
                Swipe left to dismiss
            </div>
        );

        return (
            <div ref={(el) => {
                if(el) {
                    this.addSwipeActions(el)
                }
            }} className={classNames}>
                <div className={contentClassNames}>
                    <Link onClick={(e) => this.props.onContentClick(content.id)} className="content-url" to={`/content/${content.id}`}>{content.headline}</Link>
                    <p className="content-standfirst" dangerouslySetInnerHTML={{__html: content.standfirst}}></p>
                    <div className="content-sink">
                        {showSwipeInfoText}
                        <div className="content-sink-right">
                            SCORE: {meta.score.toFixed(2)}
                        </div>
                    </div>
                </div>
                <div className="mark-read-box fill-content-wrapper">
                    <svg fill="#FFFFFF" height="36" viewBox="0 0 24 24" width="36" xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                    </svg>
                </div>
            </div>
        )
    }
}

export default BrevisContentCard