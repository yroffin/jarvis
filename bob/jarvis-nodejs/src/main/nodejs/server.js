var express = require('express');
var https = require('https');
var http = require('http');
var fs = require('fs');
var blammo = require('blammo');

function main() {
	var root = blammo.LoggerFactory.getLogger(blammo.Logger.ROOT_LOGGER_NAME);
	var logger = blammo.LoggerFactory.getLogger('logger1');

	// This line is from the Node.js HTTPS documentation.
	var options = {
		key : fs.readFileSync('keys/agent2-key.pem'),
		cert : fs.readFileSync('keys/agent2-cert.pem')
	};

	// Create a service (the app object is just a callback).
	var app = express();

	// Create an HTTP service.
	logger.info('Create an HTTP service');
	http.createServer(app).listen(80);
	logger.info('Create an HTTP service done');
	// Create an HTTPS service identical to the HTTP service.
	logger.info("Create an HTTPS service identical to the HTTP service");
	var httpsServer = https.createServer(options, app);
	httpsServer.listen(443);
	logger.info("Create an HTTPS service identical to the HTTP service done");
}

main()
