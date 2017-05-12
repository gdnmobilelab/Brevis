import { createStore } from 'redux';
import update from './util/update-ext';


const initialState = {
    loading: false,
    userContent: [],
    user: {
        id: null,
        loading: false,
        failed: false,
        current: {}
    },
    header: {
        showMenu: true
    },
    settings: {
        active: false,
    },
    notices: {
        map: {}
    },
    pages: {
        brief: {},
        content: {
            current: {}
        },
        onboarding: {
            form: {
                commuteLength: 30
            }
        },
        profile: {
            form: {}
        }
    }
};

export default createStore(function(state = initialState, action) {
    switch (action.type) {
        case 'CREATE_NOTICE':
            return update(state, {
                notices: {
                    map: {
                        [action.id] : { $set: {
                            message: action.message,
                            type: action.noticeType
                        } }
                    }
                }
            });
        case 'REMOVE_NOTICE':
            return update(state, {
                notices: {
                    map: {
                        $unset: [action.id]
                    }
                }
            });
        case 'REQUEST_BRIEF_CONTENT':
            return update(state, {
                loading: { $set: true },
                user: {
                    loading: { $set: true }
                }
            });
        case 'RECEIVED_BRIEF_CONTENT':
            return update(state, {
                loading: { $set: false },
                user: {
                    loading: { $set: false }
                },
                userContent: { $set: action.userContent }
            });
        case 'UPDATE_BRIEF_CONTENT':
            return update(state, {
                userContent: { $set: action.userContent }
            });
        case 'BRIEF_CONTENT_TOGGLE_SELECTED':
            return update(state, {
                pages: {
                    brief: {
                        selected: {
                            [action.id]: { $set: !state.pages.brief.selected[action.id] }
                        }
                    }
                }
            });
        case 'BRIEF_CONTENT_TOGGLE_ALL_UNSELECTED':
            return update(state, {
                pages: {
                    brief: {
                        selected: { $set: {} }
                    }
                }
            });
        case 'REQUEST_CURRENT_CONTENT':
            return update(state, {
                pages: {
                    content: {
                        loading: { $set: true },
                        current: { $set: {} }
                    }
                }
            });
        case 'RECEIVE_CURRENT_CONTENT':
            return update(state, {
                pages: {
                    content: {
                        current: { $set: action.content }
                    }
                }
            });
        case 'REQUEST_USER':
        case 'SAVE_USER':
            return update(state, {
                user: {
                    loading: { $set: true }
                }
            });
        case 'RECEIVED_USER':
        case 'SAVED_USER':
            return update(state, {
                user: {
                    id: { $set: action.user.id },
                    loading: { $set: false },
                    failed: { $set: false },
                    current: { $set: action.user }
                }
            });
        case 'REQUEST_USER_FAILED':
        case 'SAVED_USER_FAILED':
            return update(state, {
                user: {
                    loading: { $set: false },
                    failed: { $set: true }
                }
            });
        case 'RECEIVED_USER_ID':
            return update(state, {
                user: {
                    id: { $set: action.userId },
                    loading: { $set: false }
                }
            });
        case 'TOGGLE_SETTINGS':
            return update(state, {
                settings: {
                    active: { $set: !state.settings.active }
                }
            });
        case 'CLOSE_SETTINGS':
            return update(state, {
                settings: {
                    active: { $set: false }
                }
            });
        case 'SHOW_SETTINGS_MENU':
            return update(state, {
                header: {
                    showMenu: { $set: true }
                }
            });
        case 'HIDE_SETTINGS_MENU':
            return update(state, {
                header: {
                    showMenu: { $set: false }
                }
            });
        case 'UPDATE_ONBOARDING_FORM':
            return update(state, {
                pages: {
                    onboarding: {
                        form: { $set: action.form }
                    }
                }
            });
        case 'ONBOARDING_SAVE_REQUEST':
            return update(state, {
                pages: {
                    onboarding: {
                        loading: { $set: true }
                    }
                }
            });
        case 'ONBOARDING_SAVE_RECEIVED':
            return update(state, {
                pages: {
                    onboarding: {
                        loading: { $set: false }
                    }
                }
            });
        case 'ONBOARDING_SAVE_FAILED':
            return update(state, {
                pages: {
                    onboarding: {
                        loading: { $set: false }
                    }
                }
            });
        case 'UPDATE_PROFILE_FORM':
            return update(state, {
                pages: {
                    profile: {
                        form: { $set: action.form }
                    }
                }
            });
        default:
            return state;
    }
});