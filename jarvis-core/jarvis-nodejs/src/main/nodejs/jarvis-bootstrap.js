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
var deasync = require('deasync');

function main() {
	// Middleware
	var path = require('path');
	var properties = require("java-properties");
	var express = require('express');
	var https = require('https');
	var http = require('http');
	var fs = require('fs');

	// App part
	var routes = require(__dirname + '/services/routes/routes');
	var config = require(__dirname + '/services/json/config');

	// core services
	var mongo = require(__dirname + '/services/core/mongodb');
	var neo4j = require(__dirname + '/services/core/neo4jdb');

	var listener = require(__dirname + '/services/core/listener');
	var kernel = require(__dirname + '/services/core/kernel');
	var xmppsrv = require(__dirname + '/services/core/xmppsrv');
	var xmppcli = require(__dirname + '/services/core/xmppcli');
	var crontab = require(__dirname + '/services/core/crontab');

	/**
	 * default properties from jarvis.properties stored in ${SCRIPT_WORKSPACE}
	 */
	var jarvis_properties = properties.of(path.resolve(process.env.SCRIPT_WORKSPACE + '/jarvis.properties'));
	console.error(jarvis_properties);
	// Default options for this htts server
	var options = {
		key : fs.readFileSync(jarvis_properties.get('jarvis.srv.https.key')),
		cert : fs.readFileSync(jarvis_properties.get('jarvis.srv.https.cert'))
	};

	/**
	 * init mongodb
	 */
	mongo.init(jarvis_properties.get('jarvis.mongodb.jarvis'), jarvis_properties.get('jarvis.mongodb.blammo'));
	kernel.notify("Mongodb connexions ok");

	/**
	 * init neo4j
	 */
	neo4j.init(jarvis_properties.get('jarvis.neo4jdb.jarvis'));
	kernel.notify("Neo4jDb connexions ok");

	// Create a service (the app object is just a callback).
	var app = express();

	// Activate cookies, sessions and forms
	app.use(express.static(__dirname + '/public'));

	/**
	 * start xmpp server and xmppcli
	 */
	xmppsrv.start(jarvis_properties.get('jarvis.xmpp.srv.host'), jarvis_properties.get('jarvis.xmpp.srv.port'), function() {
		kernel.notify("xmpp server done");
		/**
		 * internal xmppcli
		 */
		kernel.xmppcli(jarvis_properties.get('jarvis.xmpp.srv.host'), jarvis_properties.get('jarvis.xmpp.srv.port'), {
			fn : kernel.xmppcliEcho,
			jid : 'internal@jarvis.org/local'
		});
		/**
		 * internal xmppcli
		 */
		kernel.xmppcli(jarvis_properties.get('jarvis.xmpp.srv.host'), jarvis_properties.get('jarvis.xmpp.srv.port'), {
			fn : kernel.xmppcliScript,
			jid : 'script@jarvis.org/local'
		});
	});

	/**
	 * start listener
	 */
	listener.start(jarvis_properties);

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
			console.log('Address ' + jarvis_properties.get('jarvis.srv.http.port') + ' in use, retrying...');
			setTimeout(function() {
				httpServer.listen(jarvis_properties.get('jarvis.srv.http.port'));
			}, 5000);
		}
	});

	try {
		httpServer.listen(jarvis_properties.get('jarvis.srv.http.port'));
	} catch (e) {
		logger.warn('httpServer.listen:', e);
	}

	logger.info('Create an HTTP service done');

	// Create an HTTPS service identical to the HTTP service.
	logger.info("Create an HTTPS service identical to the HTTP service");
	var httpsServer = https.createServer(options, app);

	httpsServer.on('error', function(e) {
		if (e.code == 'EADDRINUSE') {
			logger.error('Address ' + jarvis_properties.get('jarvis.srv.https.port') + ' in use, retrying...');
			setTimeout(function() {
				httpServer.listen(jarvis_properties.get('jarvis.srv.https.port'));
			}, 5000);
		}
	});

	httpsServer.listen(jarvis_properties.get('jarvis.srv.https.port'));
	kernel.notify("Create an HTTPS service identical to the HTTP service done");

	/**
	 * crontab
	 */
	kernel.notify("Clear crontab (revert from initial state)");
	crontab.clear();
	kernel.notify("Start all jobs");
	crontab.start(function(job) {
		kernel.xmppcliForkScript(job);
	});
	kernel.notify("Start all jobs done");

	/**
	 * plugin execute
	 */
	function plugin(js) {
		/**
		 * if plugin is not defined, return
		 */
		if (!js.plugin)
			return;
		try {
			var plugin = require(__dirname + '/plugins/' + js.plugin);
		} catch (e) {
			logger.warn('' + e);
			return;
		}
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
	function processIt() {
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
				 * Store event in history
				 */
				mongo.syncStoreInCollectionByName('jarvis', 'events', e);
			} catch (e) {
				logger.error('Exception: ', e);
				console.trace(e);
				throw e;
			}
		});
		setTimeout(processIt, 1000);
	}

	processIt();
}

main()
