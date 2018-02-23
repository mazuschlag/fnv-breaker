// server.js

'use strict';

// Import dependencies
const express = require('express');
const bodyParser = require('body-parser');
const childProcessor = require('child_process');
const multer = require('multer');
const uuidv4 = require('uuid/v4');
const path = require('path');

var javaPath = path.resolve('../fnv-breaker/java/Breaker.jar');

// Change Windows path to Unix for child_process.spawn()
let javaPathArr = javaPath.split('');
javaPathArr.map((element, i) => { if (element === '\\') javaPathArr[i] = '/'; });

javaPath = javaPathArr.join('');

const app = express();
const router = express.Router();

// Set up port to either a predetermined port number or 3001
const port = process.env.API_PORT || 3001;


const storage = multer.diskStorage({
	// Save files in uploads directory
	destination: (req, file, cb) => {
		cb(null, './uploads');
	},
	// Generate random id for new filename
	filename: (req, file, cb) => {
		const newFilename = `${uuidv4()}${path.extname(file.originalname)}`;
		cb(null, newFilename);
	},
});
// Create multer instance that will be used to upload/save file
const upload = multer({ storage });

// Configure the API to use bodyParser and look for JSON data in request body
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// To prevent errors from Cross Origin Resource Sharing, 
// set headers to allow CORS with middleware
app.use((req, res, next) => {
	res.setHeader('Access-Control-Allow-Origin', '*');
	res.setHeader('Access-Control-Allow-Credentials', 'true');
	res.setHeader('Access-Control-Allow-Methods', 'GET,HEAD,OPTIONS,POST,PUT,DELETE');
	res.setHeader('Access-Control-Allow-Headers', 'Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers');
	next();
});

// Send the decoded password file
app.get('/api/results/results.txt', (req, res) => {
		let results = '../fnv-breaker/results/results.txt';
		let resultsPath = path.resolve(results);
		res.sendFile(resultsPath);
	});
// Starts Java process to process passwords
app.get('/api/uploads/:filename', (req, res) => {
		let uploads = '../fnv-breaker/uploads/';
		let passwords = '../fnv-breaker/passwords/rockyou75.txt';
		let results = '../fnv-breaker/results/results.txt'
		let filePath = path.resolve(uploads.concat(req.params.filename)); // Create file path for the user's file
		let passwordPath = path.resolve(passwords); // Create file path for the common password file
		let resultsPath = path.resolve(results); // Create file path for results
		let javaChild = childProcessor.spawn('java', ['-jar', javaPath, filePath, passwordPath, resultsPath]); // Spawn child Java process to do heavy lifting
		javaChild.stdin.on('data', function(data) {
			console.log('stdin: ' + data);
		})
		javaChild.stdout.on('data', function(data) {
			let reply = data.toString();
			console.log(data);
			res.send(reply);
			return;
		});
		javaChild.stderr.on('data', function(data) {
			let reply = data.toString();
			console.log(reply);
		});
		javaChild.on('close', function(code) {
			console.log('closing code: ' + code);
		})
	});

// Route for handling user uploaded file
app.post('/api/uploads', upload.single('selectedFile'), (req, res) => {
		res.json({ message: req.file.filename });
	});


app.listen(port, function() {
	console.log(`api is running on port ${port}`);
});
