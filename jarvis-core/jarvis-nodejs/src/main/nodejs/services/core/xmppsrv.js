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

exports.start = function(done) {

	/**
	 * set up the server
	 */
	var c2s = new xmpp.C2SServer({
		port : 5222,
		bindAddress : 'localhost'
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
		logger.warn('register: ' + opts.jid + ' -> ' + opts.password);
		cb(true);
	})

	/**
	 * server connect
	 */
	c2s.on('connect', function(client) {
		logger.warn('connect');

		/**
		 * allow all connection
		 * 
		 * @todo implements password validation
		 */
		client.on('authenticate', function(opts, cb) {
			logger.warn('authenticate: ' + opts.jid + ' -> ' + opts.password);
			cb(null, opts)
			/**
			 * store this client
			 */
			clients[opts.jid] = client;
		})

		/**
		 * online handler
		 */
		client.on('online', function() {
			logger.warn('online' + client.jid);
			/**
			 * store this client
			 */
			clients[client.jid] = client;
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
				logger.warn(emitType + '!' + stanza);
				this.emit(emitType, stanza);
			} else {
				logger.trace("stanza not found " + stanza);
			}
		})

		/**
		 * diconnect
		 */
		client.on('disconnect', function() {
			logger.warn('disconnect');
		})

		// handler
		client.on('end', function() {
			logger.warn('end');
		})

		// handler
		client.on('close', function() {
			logger.warn('close');
		})

		// handler
		client.on('error', function() {
			logger.error('error');
		})

		// handler
		client.on('message', function(message) {
			logger.warn('message to : ' + message.attrs.to);
			logger.warn('message from : ' + message.attrs.from);

			if (clients[message.attrs.to]) {
				clients[message.attrs.to].send(message);
			}
		});

		// handler
		client.on('presence', function(message) {
			logger.warn('presence', message);

			if (clients[message.attrs.to])
				clients[message.attrs.to].send(message);
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
				var jid = user.jid.local + '@' + user.jid.domain + '/' + user.jid.resource;
				result.c("item", {
					name : jid,
					jid : jid,
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
