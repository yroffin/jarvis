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

var kernel = require(__dirname + '/kernel');
var cycle = require(__dirname + '/cycle');

/**
 * clear clients
 */
exports.clearClients = function () {
	/**
	 * clear current client context
	 */
 	kernel.getContext().clients = [];
	logger.info("clearClients()");
};

/**
 * retrieve current clients connected (without internal information like socket)
 */
exports.getClients = function () {
	/**
	 * api must not expose internal structure for security
	 * reason
	 */
	var result = [];
	kernel.getContext().clients.forEach(function(descriptor) {
		result.push({'id':descriptor.id,'name':descriptor.name,'isReferer':descriptor.isReferer,'isSensor':descriptor.isSensor});
		logger.info("getClients(%s)", JSON.stringify({'id':descriptor.id,'name':descriptor.name,'isReferer':descriptor.isReferer,'isSensor':descriptor.isSensor}));
	});
 	return result;
};

/**
 * retrieve current clients connected
 */
exports.getClientIndexOf = function (socket) {
	/**
	 * api must not expose internal structure for security
	 * reason
	 */
	var index = 0;
	var length = kernel.getContext().clients.length;
	var result = {index:-1};
	for(;index < length;index++) {
		descriptor = kernel.getContext().clients[index];
		if(descriptor.socket == socket) {
			result.index = index;
			result.descriptor = descriptor;
		}
	}
	logger.debug("getClientIndexOf(%s) => %s", socket.remoteAddress + ":" + socket.remotePort, result.index);
 	return result;
};

/**
 * retrieve current clients connected
 */
exports.addClient = function (descriptor) {
	logger.info("addClient(%s)", descriptor.id);
	/**
	 * add this client to current context
	 * note : client is a pure socket nodejs object
	 */
	kernel.getContext().clients.push(descriptor)
 	return descriptor;
};
