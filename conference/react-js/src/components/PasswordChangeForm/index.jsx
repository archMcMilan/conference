import React from 'react';
import PropTypes from 'prop-types';

import InputBlock from '../InputBlock';

const PasswordChangeForm = ({
  currentPassword, newPassword, confirmNewPassword,
  submit, cancel, change,
}) => (
  <form
    onSubmit={submit}
    onChange={change}
    className="settings__row-content"
  >
    <InputBlock
      value={currentPassword}
      label="Current password"
      name="currentPassword"
      type="password"
    />
    <InputBlock
      value={newPassword}
      label="New password"
      name="newPassword"
      type="password"
    />
    <InputBlock
      value={confirmNewPassword}
      label="Confirm new password"
      name="confirmNewPassword"
      type="password"
    />
    <input
      type="submit"
      className="btn btn__inline"
    />
    <input
      type="button"
      className="btn btn__inline"
      value="Cancel"
      onClick={cancel}
    />
  </form>
);

PasswordChangeForm.propTypes = {
  cancel: PropTypes.func.isRequired,
  submit: PropTypes.func.isRequired,
  change: PropTypes.func.isRequired,
  currentPassword: PropTypes.string.isRequired,
  newPassword: PropTypes.string.isRequired,
  confirmNewPassword: PropTypes.string.isRequired,
};

export default PasswordChangeForm;
