import CONFIG from '../config';

let firebase = require('firebase/app');
require('firebase/messaging');

firebase.initializeApp(CONFIG.FIREBASE.CONFIG);

export default firebase.messaging();