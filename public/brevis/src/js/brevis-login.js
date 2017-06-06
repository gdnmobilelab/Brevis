import Inferno from 'inferno';
import Component from 'inferno-component';
import createHashHistory from 'history/createHashHistory';
import { Provider } from 'inferno-redux';
import { Router, IndexRoute } from 'inferno-router';

const browserHistory = createHashHistory();

import store from './store';

class BrevisLogin extends Component {
    componentDidMount() {
        store.dispatch({
            type: 'HIDE_SIDEBAR_MENU'
        })
    }

    render() {
        return (
            <div className="brevis-login login-outer-wrapper">
                <div className="brevis-login login-header-wrapper">
                    <div className="brevis-login login-header">Brevis</div>
                    <div className="brevis-login login-body">By The Guardian Mobile Innovation Lab</div>
                    <div className="brevis-login login-body">Brevis is a smart news app that learns what you like to read and delivers you articles every day for your commute.</div>
                </div>
                <div className="brevis-login login-button-wrapper">
                    <a href="/brevis/app/?login=true" className="brevis-login login-button">Login w/ Google</a>
                </div>
            </div>
        )
    }
}

let app = (
    <Provider store={ store } >
        <Router history={ browserHistory }>
            <IndexRoute component={ BrevisLogin } />
        </Router>
    </Provider>
);

Inferno.render(app, document.getElementById('brevis-login'));