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
var logger = require('blammo').LoggerFactory.getLogger('kernel');

function main() {
	// Middleware
	var express = require('express');
	var https = require('https');
	var http = require('http');
	var fs = require('fs');

	// App part
	var routes = require(__dirname + '/services/routes/routes');
	var servicesConfig = require(__dirname + '/services/json/config');
	/**
	 * core services
	 */
	var mongo = require(__dirname + '/services/core/mongodb');
	mongo.init();

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
	app.use(express.logger()).use(express.static(__dirname + '/public')).use(express.favicon(__dirname + '/public/favicon.ico')).use(express.cookieParser()).use(express.session({
		secret : 'secretkey'
	})).use(express.bodyParser());

	/**
	 * start listener
	 */
	listener.start()

	/**
	 * build all routes
	 */
	try {
		routes.init(app);
	} catch (e) {
		logger.warn('routes.init:', e);
	}

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

	try {
		httpServer.listen(80);
	} catch (e) {
		logger.warn('httpServer.listen:', e);
	}

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
	kernel.notify("Create an HTTPS service identical to the HTTP service done");

	/**
	 * plugin execute
	 */
	function plugin(js) {
		var plugin = require(__dirname + '/plugins/' + js.plugin);
		if (js.params != undefined) {
			kernel.notify("Running plugin " + js.plugin + " " + JSON.stringify(js.params));
			plugin.execute(js.params);
			return;
		} else {
			if (js.args != undefined) {
				kernel.notify("Running plugin " + js.plugin + " " + JSON.stringify(js.args));
				plugin.execute(js.args);
				return;
			} else {
				plugin.execute();
				return;
			}
		}
	}

	/**
	 * infinite loop for processing event
	 */
	function process() {
		kernel.process(function(e) {
			try {
				/**
				 * standard event
				 */
				if (e.code == 'event') {
					if (e.event.script != undefined) {
						/**
						 * run this plugin
						 */
						if (e.event.script != '') {
							plugin(JSON.parse(e.event.script));
						}
					}
				}
				/**
				 * standard event, but call evt event is a reserved keyword in
				 * .Net
				 */
				if (e.code == 'evt') {
					if (e.evt.script != undefined) {
						/**
						 * run this plugin
						 */
						if (e.evt.script != '') {
							plugin(JSON.parse(e.evt.script));
						}
					}
				}
				/**
				 * TODO store event in history
				 */
			} catch (e) {
				console.log('Exception: ', e);
				throw e;
			}
		});
		setTimeout(process, 1000);
	}

	process();
}

main()
