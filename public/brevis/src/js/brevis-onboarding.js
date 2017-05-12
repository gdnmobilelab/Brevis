import UserService from './services/UserService';
import update from 'immutability-helper';

const ON_SUCCESS_REDIRECT_TO = '/brevis/app/';

let submitButton = document.querySelector('.submit');
let loader = document.querySelector('.loader');
let commuteLengthInput = document.querySelector('#commute-length');

submitButton.addEventListener('click', (e) => {
    submitButton.addClass('hidden');
    submitButton.disabled = "disabled";
    loader.removeClass('hidden');

    let userUpdate = update(ACTIVE_USER, {
        commuteLength: { $set: parseInt(commuteLengthInput.value, 10) }
    });

    UserService.updateCurrentUser(userUpdate)
        .then((success) => {
            window.location = ON_SUCCESS_REDIRECT_TO;
        })
        .catch((err) => {
            console.log(err);
            submitButton.removeClass('hidden');
            submitButton.removeAttribute('disabled');
            loader.addClass('hidden');
        })
});

/** Cycle through all the form groups to attach focus/blur event listeners **/
let formGroupInputs = document.querySelectorAll('.form-group input');

for(let i = 0; i < formGroupInputs.length; i++) {
    let input = formGroupInputs[i];
    let label = input.nextElementSibling;

    input.addEventListener('focus', (e) => {
        if (!label) { return }

        label.addClass('active');
    });

    input.addEventListener('blur', (e) => {
        if (!label || e.target.value) { return }

        label.removeClass('active');
    })
}