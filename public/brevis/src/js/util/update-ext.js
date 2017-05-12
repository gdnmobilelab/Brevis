import update from 'immutability-helper';

//see: https://github.com/kolodny/immutability-helper/issues/18
update.extend('$unset', (keysToRemove, original) => {
    let copy = Object.assign({}, original);
    for (const key of keysToRemove) delete copy[key]
    return copy
});

export default update