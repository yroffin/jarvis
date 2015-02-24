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

var logger = require('blammo').LoggerFactory.getLogger('xmppsrv');

'use strict';

var xmpp = require('node-xmpp-server');
var Message = require('node-xmpp-core').Stanza.Message;
var Element = require('node-xmpp-core').Stanza.Element;

var clients = {}
var resources = {}

exports.start = function(host, port, done) {

	/**
	 * set up the server
	 */
	var c2s = new xmpp.C2SServer({
		bindAddress : host,
		port : port
	})

	/**
	 * error handler
	 */
	c2s.on('error', function(err) {
		logger.warn('c2s error: ' + err.message);
	})

	/**
	 * register handler
	 */
	c2s.on('register', function(opts, cb) {
		logger.info('register: ' + opts.jid + ' -> ' + opts.password);
		cb(true);
	})

	/**
	 * server connect
	 */
	c2s.on('connect', function(client) {
		logger.info('connect');

		/**
		 * boradcast message
		 */
		client.broadcast = function(message) {
			for (cl in clients) {
				if (client != client[cl]) {
					clients[cl].send(message);
				}
			}
		}

		/**
		 * allow all connection
		 * 
		 * @todo implements password validation
		 */
		client.on('authenticate', function(opts, cb) {
			logger.info('authenticate: ' + opts.jid + '::' + opts.password);
			cb(null, opts)
			/**
			 * store this client
			 */
			resources[client.jid] = client;
		})

		/**
		 * online handler
		 */
		client.on('online', function() {
			logger.info('online:', client.jid);
			/**
			 * store this client
			 */
			clients[client.jid] = client;
			resources[client.jid] = client;

			/**
			 * send reconfigure, for inject logs
			 */
			client.sendAlt = client.send;
			client.send = function(message) {
				logger.debug('jid:' + client.jid);
				logger.debug('to:' + message.attrs.to);
				logger.debug('from:' + message.attrs.from);
				logger.debug("send:", message.toString());
				this.sendAlt(message);
			}
		})

		/**
		 * stanza handler
		 */
		client.on('stanza', function(stanza) {
			logger.trace('stanza', stanza.attrs.from, stanza.attrs.to, stanza.attrs.type);
			var emitType = null;
			if (stanza.getChild('query')) { // Info query get or set
				emitType = 'query:' + stanza.attrs.type + ':' + stanza.getChild('query').attrs.xmlns;
			} else if (stanza.getName() == "presence") { // Presence
				emitType = 'presence';
			} else if (stanza.getName() == "message") { // Message
				emitType = 'message';
			} else if (stanza.getChild('ping') != null) {
				emitType = 'ping';
			}
			if (emitType) {
				logger.debug(emitType + '!' + stanza);
				this.emit(emitType, stanza);
			} else {
				logger.trace("stanza not found " + stanza);
			}
		})

		/**
		 * diconnect
		 */
		client.on('disconnect', function() {
			logger.debug('disconnect', client.jid);
			delete clients[client.jid];
			delete resources[client.jid];
		})

		/**
		 * end handler
		 */
		client.on('end', function() {
			logger.debug('end');
			delete clients[client.jid];
			delete resources[client.jid];
		})

		/**
		 * close handler
		 */
		client.on('close', function() {
			logger.debug('close');
		})

		/**
		 * error handler
		 */
		client.on('error', function() {
			logger.error('error');
		})

		/**
		 * Cf. http://xmpp.org/extensions/xep-0199.html
		 */
		client.on('ping', function(ping) {
			logger.debug('ping', ping);
			/**
			 * answer to this ping
			 */
			var answer = new Element('iq', {
				type : 'result',
				to : ping.attrs.from,
				id : ping.attrs.id
			});
			clients[ping.attrs.from].send(answer);
		})

		/**
		 * message handler
		 */
		client.on('message', function(message) {
			logger.debug('message', client.jid);
			if (resources[message.attrs.to]) {
				resources[message.attrs.to].send(message);
			}
		});

		/**
		 * presence handler
		 */
		client.on('presence', function(message) {
			logger.debug('presence', client.jid);
			client.broadcast(message);
		});

		/**
		 * IQ result builder
		 */
		var _buildIqResult = function(query, xmlns) {
			return new Element('iq', {
				type : 'result',
				from : query.attrs.to,
				to : query.attrs.from,
				id : query.attrs.id
			}).c("query", {
				xmlns : xmlns
			});
		}

		/**
		 * Cf. http://xmpp.org/extensions/xep-0016.html
		 */
		client.on('query:get:jabber:iq:privacy', function(query) {
			var result = new Element('iq', {
				type : 'result',
				to : query.attrs.from,
				id : query.attrs.id
			}).c("query", {
				xmlns : 'jabber:iq:privacy'
			}).c("active", {
				name : 'jarvis'
			}).up().c("default", {
				name : 'jarvis'
			}).up().c("list", {
				name : 'jarvis'
			}).up();
			client.send(result);
		});

		/**
		 * Cf. http://xmpp.org/extensions/xep-0049.html
		 */
		client.on('query:get:jabber:iq:private', function(query) {
			var result = new Element('iq', {
				type : 'result',
				from : query.attrs.to,
				to : query.attrs.from,
				id : query.attrs.id
			}).c("query", {
				xmlns : 'jabber:iq:private'
			});

			/**
			 * @todo identify roster get behaviour
			 */
			client.send(result);
		});

		/**
		 * Cf. http://xmpp.org/extensions/xep-0083.html
		 */
		client.on('query:get:jabber:iq:roster', function(query) {
			var result = _buildIqResult(query, 'jabber:iq:roster');
			/**
			 * add local jid
			 */
			for ( var cli in clients) {
				var user = clients[cli];
				result.c("item", {
					name : user.jid.local + '@' + user.jid.domain,
					jid : user.jid.local + '@' + user.jid.domain + '/' + user.jid.resource,
					subscription : 'both'
				}).c("group", {}).t('jarvis').up();
			}
			client.send(result);
		});
	})

	/**
	 * callback handler
	 */
	done()
}
