import React, { Component } from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import changeUserInfo from '../../actions/change-user-info';
import { uploadUserPhoto } from '../../constants/backend-url';

import InputBlock from '../InputBlock/InputBlock';
import TextareaBlock from '../TextareaBlock/TextareaBlock';
import PopUpSaved from './PopUps/PopUpSaved';
import PopUpPreventUnsavedExit from './PopUps/PopUpPreventUnsavedExit';
import PopUpChangePhoto from './PopUps/PopUpChangePhoto';

class MyInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showPreventUnsavedExitModal: false,
      showInfoSavedModal: false,
      showChangePhotoModal: false,
      user: {},
      file: '',
      userPhotoSrc: '',
    };
  }

  componentDidMount() {
    this.setUserInfo(this.props);
    this.props.updateInfo();
  }

  componentWillReceiveProps(nextProps) {
    this.setUserInfo(nextProps);
  }

  setUserInfo = ({ user }) => {
    this.setState({ user });
  };

  handleOpenModal = () => {
    this.setState({ showInfoSavedModal: true });
  };

  handleCloseModal = () => {
    this.setState({ showInfoSavedModal: false });
  };

  handleCloseModal1 = () => {
    this.setState({ showChangePhotoModal: false });
  };

  handleInput = (e) => {
    const upUser = {
      ...this.state.user,
      [e.target.name]: e.target.value,
    };
    this.setState(() => ({ user: upUser }));
  };

  handleSaveInfo = (e) => {
    e.preventDefault();
    changeUserInfo(this.state.user);
    this.handleOpenModal();
  };

  handleChangePhoto = () => {
    this.setState({ showChangePhotoModal: true });
  };

  changeProfilePhoto = (e) => {
    e.preventDefault();

    const file = e.target.files[0];
    const photoURL = window.URL.createObjectURL(file);

    this.setState({ userPhotoSrc: photoURL });
  }

  uploadPhotoToDB = (e) => {
    e.preventDefault();

    const choosePhotoBtn = document.querySelector('#choose-photo__btn');
    const userPhoto = choosePhotoBtn.files[0];

    const data = new FormData();
    data.append('file', userPhoto);

    axios.post(uploadUserPhoto, data)
      .then((res) => {
        console.log(res);
      });
  }

  render() {
    const { user: {
      bio = '',
      job = '',
      company = '',
      past = '',
      info = '' } } = this.state;

    return (
      <div>
        <div className="my-info__photo-block">
          <img
            className="my-info__photo"
            src={this.state.userPhotoSrc}
            alt=""
          />
          <button
            className="my-info__remove"
          />
          <span
            className="change-photo"
            onClick={this.handleChangePhoto}
            role="button"
            tabIndex="-1"
            data-type="changePhoto"
          >Change photo</span>
        </div>
        <form className="my-info" name="" noValidate>
          <TextareaBlock
            id="my-info-bio"
            labelClassName="form-label_required my-info__label
              my-info__label_bio"
            className="textarea textarea-focus my-info__bio"
            label="Short Bio"
            name="bio"
            maxLen={2000}
            value={bio}
            onChange={this.handleInput}
          />
          <InputBlock
            id="my-job-title"
            labelClass="form-label_required my-info__label"
            label="Job Title"
            name="job"
            inputClass="field_border my-info__field_job"
            maxLength={256}
            value={job}
            onChange={this.handleInput}
          />
          <InputBlock
            id="my-info-company"
            labelClass="form-label_required my-info__label"
            label="Company"
            name="company"
            inputClass="field_border my-info__field_company"
            maxLength={256}
            value={company}
            onChange={this.handleInput}
          />
          <TextareaBlock
            id="my-past-conferences"
            labelClassName="my-info__label"
            className="textarea textarea-focus"
            label="Past Conferences"
            name="past"
            rows={5}
            maxLen={1000}
            value={past}
            onChange={this.handleInput}
          />
          <TextareaBlock
            id="my-additional-info"
            labelClassName="form-label"
            className="textarea textarea-focus"
            label="Additional Info"
            name="info"
            rows={5}
            maxLen={1000}
            value={info}
            onChange={this.handleInput}
          />
          <input
            type="submit"
            value="save"
            className="btn my-info__button"
            data-type="saveInfo"
            onClick={this.handleSaveInfo}
          />
        </form>
        {this.state.showInfoSavedModal &&
          <PopUpSaved
            showModal={this.state.showInfoSavedModal}
            closeModal={this.handleCloseModal}
          />}
        {this.state.showPreventUnsavedExitModal &&
        <PopUpPreventUnsavedExit
          showModal={this.state.showPreventUnsavedExitModal}
          closeModal={this.handleCloseModal}
        />}
        {this.state.showChangePhotoModal &&
        <PopUpChangePhoto
          showModal={this.state.showChangePhotoModal}
          closeModal={this.handleCloseModal1}
          changeProfilePhoto={this.changeProfilePhoto}
          uploadPhotoToDB={this.uploadPhotoToDB}
        />}
      </div>
    );
  }
}

MyInfo.propTypes = {
  updateInfo: PropTypes.func.isRequired,
};

export default MyInfo;
