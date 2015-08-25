/**
 * Copyright 2014 Yannick Roffin.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var logger = require('blammo').LoggerFactory.getLogger('kernel');

var Dequeue = require(__dirname + '/dequeue');
var Xmppcli = require(__dirname + '/xmppcli');
var xmppsrv = require(__dirname + '/xmppsrv');
var mailer = require(__dirname + '/mailer');

/**
 * events queue store all event acquire by this system before any treatment
 */
var context = {
	'sequence' : 0,
	'events' : new Dequeue()
};

/**
 * notify a new event, only for system internal use
 */
var notify = function(message) {
	var copy = {
		'data' : message
	};
	/**
	 * update event context
	 */
	copy.sender = {
		'id' : -1,
		'name' : 'internal'
	};
	copy.target = {
		'id' : -1,
		'name' : 'internal'
	};
	copy.sequence = context.sequence++;
	copy.timestamp = new Date();
	/**
	 * register it
	 */
	context.events.push(copy);
	logger.warn("notification:", copy.data);
	return copy;
}

/**
 * register a new event
 */
var register = function(sender, target, event) {
	var copy = event;
	/**
	 * update event context
	 */
	copy.sender = {
		'id' : sender.id,
		'name' : sender.name
	};
	copy.target = {
		'id' : target.id,
		'name' : target.name
	};
	copy.sequence = context.sequence++;
	copy.timestamp = new Date();
	/**
	 * register it
	 */
	context.events.push(copy);
	return event;
}

/**
 * process event queue
 */
var process = function(callback) {
	while (context.events.length > 0) {
		callback(context.events.pop());
	}
}

/**
 * retrieve events
 */
var getEvents = function() {
	return context.events.all();
};

/**
 * retrieve current kernel context
 */
var getContext = function() {
	return context;
};

/**
 * retrieve current kernel client
 */
var setSession = function() {
	return context.session;
};

/**
 * xmppcli client for remoteModuleRender exchange
 * 
 * @param args
 * @return nothing
 */
var xmppcliAiml = function(args) {
	/**
	 * send this message to render only if it's not a reply
	 */
	remoteModuleRender({
		id : args.descriptorId,
		to : args.to,
		from : args.from,
		message : args.message
	}, args);
}

/**
 * xmppcli client for simple echo
 * 
 * @param args
 * @return nothing
 */
var xmppcliScript = function(args) {
	try {
		var evt = JSON.parse(args.message);
	} catch (e) {
		/**
		 * emit internal answer to script@jarvis.org
		 */
		xmppsrv.emit({
			to : args.from,
			from : args.to,
			data : '' + e
		});
		return;
	}
	/**
	 * register this plugin for execute
	 */
	register({
		'id' : -1,
		'name' : 'xmppcli'
	}, {
		'id' : -1,
		'name' : 'xmppcli'
	}, {
		code : 'event',
		event : {
			script : '{"plugin":"' + evt.plugin + '", "params":' + JSON.stringify(evt.params) + '}'
		}
	});
	return JSON.stringify(evt) + ' ok ...';
}

/**
 * xmppcli client for simple echo
 * 
 * @param args
 * @return nothing
 */
var xmppcliEcho = function(args) {
	return args.message;
}

/**
 * xmppcli client
 * 
 * @param none
 * @return nothing
 */
var xmppcli = function(host, port, fn, args) {
	Xmppcli.start(host, port, fn, args);
}

/**
 * xmppcli client
 * 
 * @param host
 * @param port
 * @param fn
 * @param args
 * @return nothing
 */
var xmppcliExit = function(host, port, fn, args) {
	Xmppcli.end(host, port, fn, args);
}

/**
 * exports
 */
module.exports = {
	/**
	 * event management
	 */
	register : register,
	notify : notify,
	/**
	 * xmppcli
	 */
	xmppcli : xmppcli,
	xmppcliExit : xmppcliExit,
	xmppcliScript : xmppcliScript,
	xmppcliAiml : xmppcliAiml,
	xmppcliEcho : xmppcliEcho,
	/**
	 * execute plugin
	 * @param job
	 * @returns {string}
	 */
	xmppcliForkScript : function(job) {
		/**
		 * send mail for each message
		 */
		mailer.send('[Jarvis]', 'Job activation', job);
		/**
		 * register it
		 */
		register({
			'id' : -1,
			'name' : 'xmppcli'
		}, {
			'id' : -1,
			'name' : 'xmppcli'
		}, {
			code : 'event',
			event : {
				script : '{"plugin":"' + job.plugin + '", "params":' + JSON.stringify(job.params) + '}'
			}
		});
		return JSON.stringify(job) + ' ok ...';
	},
	/**
	 * internal api
	 */
	process : process,
	getEvents : getEvents,
	getContext : getContext,
	setSession : setSession
}
