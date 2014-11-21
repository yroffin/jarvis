/** 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * logging
 */
var blammo = require('blammo');
var root = blammo.LoggerFactory.getLogger(blammo.Logger.ROOT_LOGGER_NAME);
var logger = blammo.LoggerFactory.getLogger('logger1');

function main() {
	// Middleware
	var express = require('express');
	var https = require('https');
	var http = require('http');
	var fs = require('fs');

	// App part
	var routes = require(__dirname + '/routes/routes');
	var servicesConfig = require(__dirname + '/services/json/config');
	/**
	 * core services
	 */
	var listener = require(__dirname + '/services/core/listener');
	var kernel = require(__dirname + '/services/core/kernel');

	// Default options for this htts server
	var options = {
		key : fs.readFileSync('keys/agent2-key.pem'),
		cert : fs.readFileSync('keys/agent2-cert.pem')
	};

	// Create a service (the app object is just a callback).
	var app = express();

	// Activate cookies, sessions and forms
	app.use(express.logger()).use(express.static(__dirname + '/public')).use(
			express.favicon(__dirname + '/public/favicon.ico')).use(
			express.cookieParser()).use(express.session({
		secret : 'secretkey'
	})).use(express.bodyParser());

	/**
	 * start listener
	 */
	listener.start()

	/**
	 * build all routes
	 */
	routes.init(app);

	// Create an HTTP service.
	logger.info('Create an HTTP service');
	var httpServer = http.createServer(app);

	httpServer.on('error', function(e) {
		if (e.code == 'EADDRINUSE') {
			console.log('Address 80 in use, retrying...');
			setTimeout(function() {
				httpServer.listen(80);
			}, 5000);
		}
	});

	httpServer.listen(80);
	logger.info('Create an HTTP service done');
	// Create an HTTPS service identical to the HTTP service.
	logger.info("Create an HTTPS service identical to the HTTP service");
	var httpsServer = https.createServer(options, app);

	httpsServer.on('error', function(e) {
		if (e.code == 'EADDRINUSE') {
			console.log('Address 443 in use, retrying...');
			setTimeout(function() {
				httpServer.listen(443);
			}, 5000);
		}
	});

	httpsServer.listen(443);
	logger.info("Create an HTTPS service identical to the HTTP service done");
}

main()
