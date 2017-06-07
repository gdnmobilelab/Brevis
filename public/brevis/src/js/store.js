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
    sidebar: {
        active: false,
    },
    notices: {
        map: {}
    },
    onboarding: {
        morningCommuteLength: 30,
        eveningCommuteLength: 30
    },
    settings: {
        loading: false,
        categories: [],
        morningCommuteLength: 0,
        eveningCommuteLength: 0
    },

    //Todo: flatten this
    pages: {
        brief: {},
        content: {
            current: {}
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
                },
                settings: {
                    morningCommuteLength: { $set: action.user.morningCommuteLength },
                    eveningCommuteLength: { $set: action.user.eveningCommuteLength }
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
        case 'TOGGLE_SIDEBAR':
            return update(state, {
                sidebar: {
                    active: { $set: !state.sidebar.active }
                }
            });
        case 'CLOSE_SIDEBAR':
            return update(state, {
                sidebar: {
                    active: { $set: false }
                }
            });
        case 'SHOW_SIDEBAR_MENU':
            return update(state, {
                header: {
                    showMenu: { $set: true }
                }
            });
        case 'HIDE_SIDEBAR_MENU':
            return update(state, {
                header: {
                    showMenu: { $set: false }
                }
            });
        case 'SET_MORNING_COMMUTE_LENGTH':
            return update(state, {
                onboarding: {
                    morningCommuteLength: { $set: action.morningCommuteLength }
                }
            });
        case 'SET_EVENING_COMMUTE_LENGTH':
            return update(state, {
                onboarding: {
                    eveningCommuteLength: { $set: action.eveningCommuteLength }
                }
            });
        case 'SET_SETTINGS_MORNING_COMMUTE_LENGTH':
            return update(state, {
                settings: {
                    morningCommuteLength: { $set: action.morningCommuteLength }
                }
            });
        case 'SET_SETTINGS_EVENING_COMMUTE_LENGTH':
            return update(state, {
                settings: {
                    eveningCommuteLength: { $set: action.eveningCommuteLength }
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
        case 'FETCHING_USER_SETTINGS_CATEGORIES':
            return update(state, {
                settings: {
                    loading: { $set: true }
                }
            });
        case 'FETCHED_USER_SETTINGS_CATEGORIES':
            return update(state, {
                settings: {
                    loading: { $set: false },
                    categories: { $set: action.categories }
                }
            });
        default:
            return state;
    }
});