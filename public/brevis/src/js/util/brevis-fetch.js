import 'whatwg-fetch'
import objectAssign from './object-assign';

/** Todo: Promise.race? **/
function brevisFetch(url, opts = {}, timeout = 3000) {
    let timeoutId = null;

    return new Promise((resolve, reject) => {
        fetch(url, objectAssign({
            credentials: 'include'
        }, opts))
            .then((res) => {
                clearTimeout(timeoutId);
                resolve(res)
            })
            .catch((err) => {
                clearTimeout(timeoutId);
                reject(err)
            });

        if (timeout >= 0) {
            timeoutId = setTimeout(() => {
                reject(new Error(`Request timed out after ${timeout}ms for request: ${url}`));
            }, timeout)
        }
    })
}

export { brevisFetch }