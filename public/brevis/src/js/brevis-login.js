import Inferno from 'inferno';
import Component from 'inferno-component';
import createHashHistory from 'history/createHashHistory';
import { Provider } from 'inferno-redux';
import { Router, IndexRoute } from 'inferno-router';

const browserHistory = createHashHistory();

import store from './store';

import BrevisHeader from './components/shared/BrevisHeader';

class BrevisLogin extends Component {
    componentDidMount() {
        store.dispatch({
            type: 'HIDE_SETTINGS_MENU'
        })
    }

    render() {
        return (
            <div>
                <BrevisHeader />
                <div className="brevis-wrapper">
                    <div className="main text-center">
                        <a className="btn" href="/brevis/app/?login=true">Login with Google</a>
                    </div>
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