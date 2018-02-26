// App.js

import React, { Component } from 'react';
import axios from 'axios';
import fileDownload from 'react-file-download';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleStart = this.handleStart.bind(this);
    this.downloadFile = this.downloadFile.bind(this);
    this.goBack = this.goBack.bind(this);
    this.state = {
      selectedFile: '',
      resFilename: '',
      before: false,
      going: false,
      after: false,
    };
  }

  handleChange(e) {
    this.setState({ selectedFile: e.target.files[0] });
  }

  handleSubmit(e) {
    e.preventDefault();

    let formData = new FormData();
    const submitURL = this.props.url.concat('/uploads/')
    formData.append('selectedFile', this.state.selectedFile);

    axios.post(submitURL, formData)
      .then(res => {
        console.log(res.data.message);
        this.setState({ resFilename: res.data.message, before: true });
      });
  }

  handleStart(e) {
    e.preventDefault();
    this.setState({ before: false, going: true });
    const fileURL = this.props.url.concat('/uploads/', this.state.resFilename);
    axios.get(fileURL)
      .then(res => {
        this.setState({ going: false, after: true});
      });
  }

  downloadFile(e) {
    e.preventDefault();
    const fileURL = this.props.url.concat('/results/results.txt');
    console.log(fileURL);
    axios.get(fileURL)
      .then(res => {
        fileDownload(res.data, 'results.txt');
      });
  }

  goBack(e) {
    this.setState({ before: false, going: false, after: false, resFilename: '', selectedFile: ''});
  }

  render() {
    if (this.state.after) {
      return(
        <div className="app-3">
          <header className="app-header">
            <h1 className="app-title">Ready!</h1>
          </header>
          <div className="done-buttons">
            <button type="button" className="btn btn-success" onClick={ this.downloadFile }>Download</button>
            <button type="button" className="btn btn-default" onClick={ this.goBack }>Return</button>
          </div>
        </div>
      );
    } else if (this.state.going) {
      return(
        <div className="app-2">
          <div className="spinner">
            <div className="rect1"></div>
            <div className="rect2"></div>
            <div className="rect3"></div>
            <div className="rect4"></div>
            <div className="rect5"></div>
          </div>
        </div>
      );
    } else {
      return (
        <div className="app-1">
          <header className="app-header">
            <h1 className="app-title">BREAK FNV</h1>
          </header>
          <form className="form-inline" onSubmit={ this.handleSubmit }>
            <div className="form-group">
              <input className="form-control" type="file" name="selectedFile" onChange={ this.handleChange }/>
            </div>
            <button type="submit" class="btn btn-default">Submit</button>
          </form>
          { this.state.before && <div className="start-button"><button type="button" className="btn btn-primary btn-lg" id="go-button" onClick={ this.handleStart }>Start</button></div> }
        </div>
      );
    }
  }
}

export default App;
