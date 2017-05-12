import { brevisFetch as fetch } from '../util/brevis-fetch';
import CONFIG from '../config';

const API_URL = CONFIG.BREVIS.API_URL;

class APIService {
    post(path, obj) {
        return fetch(API_URL + path, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obj)
        })
        .then((res) => res.json());
    }

    get(path) {
        return fetch(API_URL + path, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
        .then((res) => {
            return res.json();
        });
    }
}

export default new APIService();