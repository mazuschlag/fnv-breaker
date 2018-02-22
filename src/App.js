// App.js

import React, { Component } from 'react';
import axios from 'axios';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleStart = this.handleStart.bind(this);
    this.state = {
      selectedFile: '',
      ready: false,
      resFilename: '',
      data: ''
    };
  }

  handleChange(e) {
    this.setState({ selectedFile: e.target.files[0] });
  }

  handleSubmit(e) {
    e.preventDefault();

    let formData = new FormData();
    formData.append('selectedFile', this.state.selectedFile);

    axios.post(this.props.url, formData)
      .then(res => {
        console.log(res.data.message);
        this.setState({ resFilename: res.data.message, ready: true });
      });
  }

  handleStart(e) {
    e.preventDefault();
    const fileURL = this.props.url.concat('/', this.state.resFilename);
    console.log(fileURL);
    axios.get(fileURL)
      .then(res => {
        this.setState({ data: res.data.message });
      });
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <h1 className="App-title">BREAK FNV</h1>
        </header>
        <form className="App-form" onSubmit={ this.handleSubmit }>
          <input type="file" name="selectedFile" onChange={ this.handleChange }/>
          <button type="submit">Submit</button>
        </form>
        { this.state.ready && <button className="App-start" onClick={ this.handleStart }>Start</button> }
        <p className="From-java">{ this.state.data }</p>
      </div>
    );
  }
}

export default App;
