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

var express = require('express')
var app = express()

app.put('/api/job', function (req, res) {
	res.send('Hello World')
})

app.listen(8081)

/**
 * logging
 */
var logger = require('blammo').LoggerFactory.getLogger('kernel');

var __options;
var __neo4j;

/**
 * options
 *
 * @returns {*}
 */
function options() {
	var path = require('path');
	var properties = require("java-properties");
	var fs = require('fs');

	if(__options) {
		return __options;
	}
	/**
	 * default properties from jarvis.properties stored in ${SCRIPT_WORKSPACE}
	 */
	var jarvis_properties = properties.of(path.resolve(process.env.SCRIPT_WORKSPACE + '/jarvis.properties'));
	console.error(jarvis_properties);
	// Default options for this htts server
	__options = {
		key : fs.readFileSync(jarvis_properties.get('jarvis.srv.https.key')),
		cert : fs.readFileSync(jarvis_properties.get('jarvis.srv.https.cert')),
		properties : jarvis_properties,
		kernel : require(__dirname + '/services/core/kernel')
	};

	return __options;
}

/**
 * neo4j layer
 *
 * @param options
 * @returns {*}
 */
function neo4j(options) {
	var properties = options.properties;
	var kernel = options.kernel;

	if(__neo4j) {
		return __neo4j;
	}
	/**
	 * init neo4j
	 */
	__neo4j = require(__dirname + '/services/core/neo4jdb');
	__neo4j.init(properties.get('jarvis.neo4jdb.jarvis'));
	kernel.notify("Neo4jDb connexions ok");

	return __neo4j;
}

/**
 * web module
 *
 * @param options
 */
function webModule(options) {
	var properties = options.properties;
	var kernel = options.kernel;

	// Middleware
	var express = require('express');
	var bodyParser = require('body-parser');
	var https = require('https');
	var http = require('http');

	http.globalAgent.maxSockets = Infinity

	// App part
	var routes = require(__dirname + '/services/routes/routes');

	// Create a service (the app object is just a callback).
	var app = express();

	// Activate cookies, sessions and forms
	app.use(express.static(__dirname + '/public'));
	app.use(bodyParser.json()); // for parsing application/json

	// build all routes
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
			console.log('Address ' + properties.get('jarvis.srv.http.port') + ' in use, retrying...');
			setTimeout(function() {
				httpServer.listen(properties.get('jarvis.srv.http.port'));
			}, 5000);
		}
	});

	try {
		httpServer.listen(properties.get('jarvis.srv.http.port'));
	} catch (e) {
		logger.warn('httpServer.listen:', e);
	}

	logger.info('Create an HTTP service done');

	// Create an HTTPS service identical to the HTTP service.
	logger.info("Create an HTTPS service identical to the HTTP service");
	var httpsServer = https.createServer(options, app);

	httpsServer.on('error', function (e) {
		if (e.code == 'EADDRINUSE') {
			logger.error('Address ' + properties.get('jarvis.srv.https.port') + ' in use, retrying...');
			setTimeout(function () {
				httpServer.listen(properties.get('jarvis.srv.https.port'));
			}, 5000);
		}
	});

	httpsServer.listen(properties.get('jarvis.srv.https.port'));
	kernel.notify("Create an HTTPS service identical to the HTTP service done");
}

/**
 * xmpp module
 *
 * @param options
 */
function xmppModule(options) {
	var xmppsrv = require(__dirname + '/services/core/xmppsrv');
	var xmppcli = require(__dirname + '/services/core/xmppcli');

	var properties = options.properties;
	var kernel = options.kernel;

	/**
	 * start xmpp server and xmppcli
	 */
	xmppsrv.start(properties.get('jarvis.xmpp.srv.host'), properties.get('jarvis.xmpp.srv.port'), function () {
		kernel.notify("xmpp server done");
		/**
		 * internal xmppcli
		 */
		kernel.xmppcli(properties.get('jarvis.xmpp.srv.host'), properties.get('jarvis.xmpp.srv.port'), {
			fn: kernel.xmppcliEcho,
			jid: 'internal@jarvis.org/local'
		});
		/**
		 * internal xmppcli
		 */
		kernel.xmppcli(properties.get('jarvis.xmpp.srv.host'), properties.get('jarvis.xmpp.srv.port'), {
			fn: kernel.xmppcliScript,
			jid: 'script@jarvis.org/local'
		});
	});
}

/**
 * listenerModule
 * @param options
 */
function listenerModule(options) {
	var listener = require(__dirname + '/services/core/listener');

	var properties = options.properties;
	var kernel = options.kernel;

	/**
	 * start listener
	 */
	listener.start(properties);
}

/**
 * crontab module
 *
 * @param options
 */
function crontabModule(options) {
	var jobs = require(__dirname + '/services/resources/jobs');

	var properties = options.properties;
	var kernel = options.kernel;

	/**
	 * crontab
	 */
	kernel.notify("Clear crontab (revert from initial state)");
	jobs.services.clear();
	kernel.notify("Start all jobs");
	jobs.services.start(function (job) {
		kernel.xmppcliForkScript(job);
	});
	kernel.notify("Start all jobs done");
}

/**
 * event module
 *
 * @param options
 */
function eventModule(options) {
	var neo4jdb = require(__dirname + '/services/core/neo4jdb');

	var properties = options.properties;
	var kernel = options.kernel;

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
			kernel.notify("Running plugin " + js.plugin + " with params " + JSON.stringify(js.params));
			plugin.execute(js.params);
			return;
		} else {
			if (js.args != undefined) {
				kernel.notify("Running plugin " + js.plugin + " with args " + JSON.stringify(js.args));
				plugin.execute(js.args);
				return;
			} else {
				kernel.notify("Running plugin " + js.plugin);
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
				neo4jdb.label.store('events', e);
			} catch (e) {
				logger.error('Exception: ', e);
				console.trace(e);
			}
		});
		setTimeout(processIt, 1000);
	}

	processIt();
}

/**
 * main entry
 */
function main() {
	//var config = require(__dirname + '/services/json/config');

	// core services
	//var mailer = require(__dirname + '/services/core/mailer');

	setImmediate(options);
	var kernel = options().kernel;

	neo4j(options());
	webModule(options());
	xmppModule(options());
	listenerModule(options());
	crontabModule(options());
	eventModule(options());
}

/**
 * main entry
 */
main()
