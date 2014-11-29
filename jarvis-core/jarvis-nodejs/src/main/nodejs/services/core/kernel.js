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

var logger = require('blammo').LoggerFactory.getLogger('kernel');

var Dequeue = require('./dequeue')

/**
 * events queue store all event acquire by this
 * system before any treatment
 */
var context = {'sequence':0,'events':new Dequeue()};

/**
 * register a new event, only for system internal use
 */
exports.notify = function (message) {
	var copy = {'data':message};
	/**
	 * update event context
	 */
	copy.sender = {'id':-1, 'name':'internal'};
	copy.target = {'id':-1, 'name':'internal'};
	copy.sequence = context.sequence++;
	copy.timestamp = new Date();
	/**
	 * register it
	 */
	context.events.push(copy);
	logger.info("Register new event", copy);
	return copy;
}

/**
 * register a new event
 */
exports.register = function (sender, target, event) {
	var copy = event;
	/**
	 * update event context
	 */
	copy.sender = {'id':sender.id, 'name':sender.name};
	copy.target = {'id':target.id, 'name':target.name};
	copy.sequence = context.sequence++;
	copy.timestamp = new Date();
	/**
	 * register it
	 */
	context.events.push(copy);
	logger.info("Register new event", copy);
	return event;
}

/**
 * process event queue
 */
exports.process = function (callback) {
	while(context.events.length > 0) {
		callback(context.events.pop());
	}
}

/**
 * retrieve events
 */
exports.getEvents = function () {
  return context.events.all();
};

/**
 * retrieve current kernel context
 */
exports.getContext = function () {
  return context;
};

/**
 * retrieve current kernel client
 */
exports.setSession = function () {
  return context.session;
};

/**
 * retrieve current kernel client
 */
exports.getClients = function () {
  return context.clients;
};
