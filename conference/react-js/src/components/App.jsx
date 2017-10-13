import React from 'react';
import {
  BrowserRouter as Router,
  Route,
} from 'react-router-dom';
import ForgotPassword from './Forgot-password';
import Tabs from './Tabs';
import Header from './Header';
import '../scss/main.scss';
import SignUp from '../containers/SignUpForm';
import {
  baseUrl,
  forgotPassword,
  signUp,
  manageUser,
  talks } from '../constants/route-url';
import ManageUser from './Manage-user/';
import Talks from '../containers/Talks';

const App = () => (
  <Router>
    <div>
      <Header />
      <Route
        path={baseUrl}
        exact
        component={Tabs}
      />
      <Route
        path={forgotPassword}
        exact
        component={ForgotPassword}
      />
      <Route
        path={signUp}
        component={SignUp}
      />
      <Route
        path={talks}
        component={Talks}
      />
      <Route
        path={manageUser}
        component={ManageUser}
      />
    </div>
  </Router>
);

export default App;
