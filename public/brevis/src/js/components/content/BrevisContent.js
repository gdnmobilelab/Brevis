import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';

import { makeImagesLazy, fetchImages } from '../../util/make-images-lazy';

import BrevisHeader from '../shared/BrevisHeader';
import BrevisLoading from '../shared/BrevisLoading';

import BrevisDB from '../../db/BrevisDB';

class BrevisContent extends Component {
    _showSettings() {
        const store = this.context.store;

        store.dispatch({
            type: 'TOGGLE_SETTINGS'
        })
    }

    componentDidMount() {
        const store = this.context.store;

        store.dispatch({
            type: 'REQUEST_CURRENT_CONTENT'
        });

        BrevisDB.findCurrentUserId()
            .then((userId) => {
                BrevisDB.getContent(this.props.params.contentId, userId)
                    .then((content) => {
                        store.dispatch({
                            type: 'RECEIVE_CURRENT_CONTENT',
                            content: content
                        });
                    });
            })
    }

    render() {
        let content = this.props.userContent.content;

        let toShow = <BrevisLoading />;

        if (content) {
            // todo: move this elsewhere
            // let main = makeImagesLazy(content.main);
            // let body = makeImagesLazy(content.bodyHtml);

            /*
             <div className="content-item">
             <h2 className="title">{content.headline}</h2>
             <div className="content-main" ref={(main) => {
             if (!main) { return; }

             fetchImages(main);
             }} dangerouslySetInnerHTML={{'__html': main}}></div>
             <div className="byline">
             {content.byline}
             </div>
             <div className="body" ref={(body) => {
             if (!body) { return; }

             fetchImages(body);
             }} dangerouslySetInnerHTML={{'__html': body}}></div>
             </div>
             */

            toShow = (
                <div dangerouslySetInnerHTML={{'__html': content.templatedHTML}}></div>
            )
        }

        return (
            <div className="brevis-content">
                <BrevisHeader onSettingsButtonClick={this._showSettings.bind(this)} />
                {toShow}
            </div>
        );
    }
}

export default connect(state => {
    return {
        userContent: state.pages.content.current
    }
})(BrevisContent)