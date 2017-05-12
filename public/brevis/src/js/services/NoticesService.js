import store from '../store';

function createNotice(msg, type, timeout) {
    let noticeId = Date.now();

    store.dispatch({
        type: 'CREATE_NOTICE',
        id: noticeId,
        message: msg,
        noticeType: type
    });

    setTimeout(() => {
        store.dispatch({
            type: 'REMOVE_NOTICE',
            id: noticeId
        });
    }, timeout)
}

class NoticesService {
    createSuccessNotice(msg, timeout = 5000) {
        createNotice(msg, 'SUCCESS', timeout);
    }

    createErrorNotice(msg, timeout = 5000) {
        createNotice(msg, 'ERROR', timeout);
    }

    createWarningNotice(msg, timeout = 5000) {
        createNotice(msg, 'WARN', timeout);
    }
}

export default new NoticesService();