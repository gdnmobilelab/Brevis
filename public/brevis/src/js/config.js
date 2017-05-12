var BREVIS_CONFIG;
import stage from './config/stage.js';
import prod from './config/production.js';
import dev from './config/dev.js';

if (process.env.NODE_ENV === 'stage') {
    BREVIS_CONFIG = stage;
} else if (process.env.NODE_ENV === 'production') {
    BREVIS_CONFIG = prod;
} else {
    BREVIS_CONFIG = dev;
}

export default BREVIS_CONFIG