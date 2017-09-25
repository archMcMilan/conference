import React, { PureComponent } from 'react';
import axios from 'axios';
import baseUrl from '../../../constants/backend-url';

class Upcoming extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      data: null,
    };
  }

  componentWillMount() {
    axios.get(`${baseUrl}/api/conference/upcoming`)
      .then((response) => {
        const data = response.data;
        this.setState({ data });
      });
  }

  render() {
    if (!this.state.data) {
      return <div />;
    }

    const data = this.state.data;
    const dataArray = Object.values(data);

    return (
      <div className="tabs-container">
        {dataArray.map(element => (
          <div className="tabs-container conference-card" key={element.id}>
            <div className="conference-card-title">
              <a
                href="#/conference/{element.id}"
                className="conference-card-title__link ng-binding"
              >{element.title}</a>
            </div>
            <button
              className="btn btn-right conference-card-title__btn"
            >
                Submit Talk
            </button>
            <div className="conference-card-dates ng-binding">
              <span className="conference-card-label">Dates:</span> TBD — TBD
            </div>
            <div className="conference-card-dates_cfp ng-binding">
              <span className="conference-card-label">Call For Papers:</span>
              {element.start_date? element.start_date :'TBD — TBD'}
            </div>
            <div className="conference-card-description ng-binding">
              {element.description}
            </div>
            <div className="conference-card-location ng-binding">
              <span
                className="conference-card-label"
              >Location:</span>
              {element.location}
            </div>
          </div>
        ))}
      </div>
    );
  }
}

export default Upcoming;
