import Inferno from 'inferno';
import Component from 'inferno-component';
import { connect } from 'inferno-redux';
import update from 'immutability-helper';
import classnames from 'classnames';

import BrevisDB from '../../db/BrevisDB';

import BrevisHeader from '../shared/BrevisHeader';
import BrevisContentCard from './BrevisContentCard';
import BrevisContentCardLoading from './BrevisContentCardLoading';

import UserContentService from '../../services/UserContentService';
import GeoLocationService from '../../services/GeoLocationService';
import ObservationService from '../../services/ObservationService';

import '../../polyfills/find';

class BrevisBrief extends Component {
    constructor(props) {
        super(props);
    }

    _showSidebar() {
        const store = this.context.store;

        store.dispatch({
            type: 'TOGGLE_SIDEBAR'
        })
    }

    onContentClick(id) {
        window.scroll(0, 0);

        this.markContentRead(id);

        GeoLocationService.getLatLng()
            .catch((err) => {
                return false
            })
            .then((latlong) => {
                return ObservationService.read(id, latlong)
                    .catch((err) => {
                        console.error(err);

                        BrevisDB.findCurrentUserId()
                            .then((userId) => {
                                return BrevisDB.upsertContentClickObservationToSync(id, userId, latlong);
                            })
                    })
            })
            .catch((err) => {
                console.log(err);
            })
    }

    markContentRead(contentId) {
        const store = this.context.store;

        let updatedContent = this.props.content.map((content) => {
            if (content.content.id === contentId) {
                return update(content, {
                    meta: {
                        read: { $set: true }
                    }
                })
            } else {
                return content;
            }
        });

        store.dispatch({
            type: 'UPDATE_BRIEF_CONTENT',
            userContent: updatedContent
        });

        let readContent = updatedContent.find((content) => content.content.id === contentId);

        BrevisDB.upsertContent(readContent, this.props.userId);

        return UserContentService
            .updateContentMeta({
                contentId: readContent.content.id,
                meta: readContent.meta
            })
            .catch((err) => {
                BrevisDB.upsertContentToSync(readContent, this.props.userId)
                    .then((success) => {
                        console.log(success)
                    })
                    .catch((err) => {
                        console.error(err);
                    })
        })
    }

    render() {
        let content = this.props.content
            .sort((contentA, contentB) => {
                if (typeof contentA.meta.score !== 'undefined' && typeof contentB.meta.score !== 'undefined') {
                    if (contentA.meta.score > contentB.meta.score) {
                        return -1;
                    } else if (contentA.meta.score < contentB.meta.score) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    if (contentA.content.webPublicationDateTimestamp > contentB.content.webPublicationDateTimestamp) {
                        return -1;
                    } else if (contentA.content.webPublicationDateTimestamp < contentB.content.webPublicationDateTimestamp) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            })
            .reduce((acc, content) => {
                if (content.meta.read) {
                    acc['read'].push(content)
                } else {
                    acc['unread'].push(content);
                }

                return acc;
            }, {
                'read': [],
                'unread': []
            });

        content = (content.unread.concat(content.read.reverse())).map((content) => {
                let contentCardClasses = classnames({'content-read': content.meta.read});

                return (
                    <BrevisContentCard
                        onMarkContentRead={this.markContentRead.bind(this)}
                        className={contentCardClasses}
                        key={content.content.id}
                        content={content.content}
                        meta={content.meta}
                        onContentClick={this.onContentClick.bind(this)}
                    />
                )
        });

        let toShow = (
            <div className="more-stories">
                <h3>We're running low on new content at the moment.</h3>
                <p>Hang tight, we'll have some content for you soon.</p>
            </div>
        );

        if (this.props.loading) {
            toShow = (
                <div>
                    <BrevisContentCardLoading />
                    <BrevisContentCardLoading />
                    <BrevisContentCardLoading />
                </div>
            );
        } else if (content.length > 0) {
            toShow = content;
        }

        return (
            <div className="brevis-brief">
                <BrevisHeader onSidebarButtonClick={this._showSidebar.bind(this)} />
                <div className="content-list">
                    {toShow}
                </div>
            </div>
        )
    }
}

export default connect(state => {
    return {
        userId: state.user.id,
        loading: state.loading,
        content: state.userContent,
        selected: state.pages.brief.selected
    }
})(BrevisBrief)