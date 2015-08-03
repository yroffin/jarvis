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
 * clear clients
 * 
 * @param none
 * @return nothing
 */
var clearClients = function() {
	/**
	 * clear current client context
	 */
	getContext().clients = [];
	logger.info("clearClients()");
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
 * retrieve current clients connected (without internal information like socket)
 */
var getConnectors = function() {
	/**
	 * api must not expose internal structure for security reason
	 */
	var result = [];
	getContext().clients.forEach(function(descriptor) {
		var descriptor = {
			'id' : descriptor.id,
			'name' : descriptor.name,
			'isRenderer' : descriptor.isRenderer,
			'canAnswer' : descriptor.canAnswer,
			'isSensor' : descriptor.isSensor
		};
		result.push(descriptor);
		logger.debug("getClients(%s)", JSON.stringify(descriptor));
	});
	return result;
};

/**
 * retrieve current kernel client
 */
var getClients = function() {
	return context.clients;
};

/**
 * retrieve current clients connected
 */
var removeClient = function(socket) {
	logger.info("removeClient(%s)", socket);
	getClients().splice(findDescriptorBySocket(socket).index, 1);
}

/**
 * retrieve current clients connected
 */
var findDescriptorBySocket = function(socket) {
	/**
	 * api must not expose internal structure for security reason
	 */
	var index = 0;
	var length = getContext().clients.length;
	var result = {
		index : -1
	};
	for (; index < length; index++) {
		var descriptor = getContext().clients[index];
		if (descriptor.socket == socket) {
			result.index = index;
			result.descriptor = descriptor;
			break;
		}
	}
	logger.debug("getClientIndexOf(%s) => %s", socket.remoteAddress + ":" + socket.remotePort, result.index);
	return result;
};

/**
 * retrieve current clients connected
 */
var findDescriptorById = function(id) {
	/**
	 * api must not expose internal structure for security reason
	 */
	var index = 0;
	var length = getContext().clients.length;
	var result = {
		index : -1
	};
	for (; index < length; index++) {
		var descriptor = getContext().clients[index];
		if (descriptor.id == id) {
			result.index = index;
			result.descriptor = descriptor;
			break;
		}
	}
	logger.debug("findDescriptorById(%s) => %s", descriptor.id, result.index);
	return result;
};

/**
 * retrieve current clients connected
 */
var findDescriptorByName = function(name) {
	/**
	 * api must not expose internal structure for security reason
	 */
	var index = 0;
	var length = getContext().clients.length;
	var result = {
		index : -1
	};
	for (; index < length; index++) {
		var descriptor = getContext().clients[index];
		if (descriptor.name == name) {
			result.index = index;
			result.descriptor = descriptor;
			break;
		}
	}
	if(descriptor) {
		logger.debug("findDescriptorByName(%s) => %s", descriptor.name, result.index);
	}
	return result;
};

/**
 * retrieve current clients connected
 */
var findAnswerDescriptor = function() {
	/**
	 * api must not expose internal structure for security reason
	 */
	var index = 0;
	var length = getContext().clients.length;
	var result = {
		index : -1
	};
	for (; index < length; index++) {
		var descriptor = getContext().clients[index];
		if (descriptor.canAnswer == true) {
			result.index = index;
			result.descriptor = descriptor;
			break;
		}
	}
	if(descriptor) {
		logger.debug("findAnswerDescriptor() %s => %s", descriptor.id, result.index);
	}
	return result;
};

/**
 * add a new descriptor for this client
 * 
 * @param Object
 *            client descriptor
 */
var addClient = function(descriptor) {
	logger.info("addClient(%s)", descriptor.id);
	/**
	 * add this client to current context note : client is a pure socket nodejs
	 * object
	 */
	getContext().clients.push(descriptor)
	return descriptor;
};

/**
 * write on socket
 * 
 * @param Object
 *            message to send
 * @param Object
 *            target descriptor to send to
 */
var sendMessage = function write(message, target, socket) {
	logger.info("[SENDV]", JSON.stringify(message));
	/**
	 * send message on network
	 */
	socket.write(JSON.stringify(message));
	socket.write("\r\n");
	/**
	 * each message are event and must be notified / traced to the system event
	 * queue
	 */
	register({
		'id' : -1,
		'name' : 'kernel'
	}, {
		'id' : target.id,
		'name' : target.name
	}, message);
}

/**
 * api aiml send stream to aiml renderer
 * 
 * @target object {id: target id, message: message to send}
 */
var remoteModuleRender = function(target) {
	/**
	 * find target client
	 */
	var descriptor;
	if (target.id != undefined) {
		/**
		 * find descriptor by id
		 */
		descriptor = findDescriptorById(target.id).descriptor;
	} else {
		if (target.name != undefined) {
			/**
			 * find descriptor by id
			 */
			descriptor = findDescriptorByName(target.name).descriptor;
		}
	}
	/**
	 * last chance
	 */
	if (!descriptor) {
		/**
		 * find descriptor by attribute
		 */
		logger.error('Impossible de trouver le descripteur pour ', target);
		descriptor = findAnswerDescriptor().descriptor;
	}
	/**
	 * no descriptor no message
	 */
	if (!descriptor) {
		return;
	}
	/**
	 * Send a request message
	 */
	sendMessage({
		'code' : 'request',
		'request' : {
			'to' : target.to,
			'from' : target.from,
			'data' : target.message
		},
		'session' : {
			'client' : {
				'id' : descriptor.id
			}
		}
	}, descriptor, descriptor.socket);
}

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
	 * client management
	 */
	getClients : getClients,
	addClient : addClient,
	removeClient : removeClient,
	clearClients : clearClients,
	getConnectors : getConnectors,
	findDescriptorBySocket : findDescriptorBySocket,
	findDescriptorById : findDescriptorById,
	findAnswerDescriptor : findAnswerDescriptor,
	sendMessage : sendMessage,
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
	 * aiml api
	 */
	remoteModuleRender : remoteModuleRender,
	/**
	 * internal api
	 */
	process : process,
	getEvents : getEvents,
	getContext : getContext,
	setSession : setSession
}
