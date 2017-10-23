import React from 'react';
import {
  NavLink,
} from 'react-router-dom';
import PropTypes from 'prop-types';

import { upcoming, myTalks } from '../../constants/route-url';

const AutorizedUserMenu = ({ length }) => (
  <ul className="tabs-list">
    <li className="tabs-list__item">
      <NavLink
        exact
        className="tabs-list__anchor"
        to={upcoming}
        activeClassName="tabs-list__anchor_active"
      >
        Upcoming
      </NavLink>
    </li>
    {length > 0 &&
      (<li className="tabs-list__item">
        <NavLink
          exact
          className="tabs-list__anchor"
          to={myTalks}
          activeClassName="tabs-list__anchor_active"
        >
        My talks
        </NavLink>
      </li>)
    }
  </ul>
);

AutorizedUserMenu.propTypes = {
  length: PropTypes.number,
};

AutorizedUserMenu.defaultProps = {
  length: 0,
};

export default AutorizedUserMenu;
