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

// Set up our port to either a predetermined port number or 3001
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


// Now configure the API to use bodyParser and look for JSON data in request body
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

router.route('/uploads/:filename')
	.get((req, res) => {
		let directory = '../fnv-breaker/uploads/';
		let filePath = path.resolve(directory.concat(req.params.filename));
		let javaChild = childProcessor.spawn('java', ['-jar', javaPath, filePath]);
		javaChild.stdout.on('data', function(data) {
			let reply = data.toString();
			res.json({ message: reply });
		});
		javaChild.stderr.on('data', function(data) {
			let reply = data.toString();
			console.log(reply);
			res.json({ message: reply });
		});
	});

router.route('/uploads')
	.post(upload.single('selectedFile'), (req, res) => {
		res.json({ message: req.file.filename });
	});

app.use('/api', router);

app.listen(port, function() {
	console.log(`api is running on port ${port}`);
});
