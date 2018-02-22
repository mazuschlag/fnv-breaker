import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';

let serverURL = 'http://localhost:3001/api/uploads';
ReactDOM.render(<App url={ serverURL }/>, document.getElementById('root'));
