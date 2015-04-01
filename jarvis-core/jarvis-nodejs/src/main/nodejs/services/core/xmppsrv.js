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

var internal = {
	/**
	 * map client put and remove
	 */
	clients : {
		put : function(key, value) {
			logger.debug('clients::put(' + key + ')');
			clients[key] = value;
		},
		exist : function(key) {
			logger.debug('clients::exist(' + key + ') = ' + (clients[key] != undefined));
			return clients[key] != undefined;
		},
		get : function(key) {
			logger.debug('clients::get(' + key + ')');
			return clients[key];
		},
		remove : function(key) {
			logger.debug('clients::remove(' + key + ')');
			delete clients[key];
			clients[key] = undefined;
		}
	},
	/**
	 * map client put and remove
	 */
	resources : {
		put : function(key, value) {
			logger.debug('resources::put(' + key + ')');
			resources[key] = value;
		},
		exist : function(key) {
			logger.debug('resources::exist(' + key + ') = ' + (resources[key] != undefined));
			return resources[key] != undefined;
		},
		get : function(key) {
			logger.debug('resources::get(' + key + ')');
			return resources[key];
		},
		remove : function(key) {
			logger.debug('resources::remove(' + key + ')');
			delete resources[key];
			resources[key] = undefined;
		}
	}
}

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
		 * broadcast message on other
		 * 
		 * @param message
		 *            the message to broadcast
		 */
		client.broadcast = function(message) {
			for (cl in clients) {
				if (client != internal.clients.get(cl)) {
					if (internal.clients.exist(cl)) {
						internal.clients.get(cl).send(message);
					}
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
			internal.resources.put(client.jid, client);
		})

		/**
		 * online handler
		 */
		client.on('online', function() {
			logger.info('online:', client.jid);
			/**
			 * store this client
			 */
			internal.clients.put(client.jid, client);
			internal.resources.put(client.jid, client);

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
		 * disconnect handler
		 */
		client.on('disconnect', function() {
			logger.warn('disconnect', client.jid);
			internal.clients.remove(client.jid);
			internal.resources.remove(client.jid);
		})

		/**
		 * end handler
		 */
		client.on('end', function() {
			logger.debug('end', client.jid);
			internal.clients.remove(client.jid);
			internal.resources.remove(client.jid);
		})

		/**
		 * close handler
		 */
		client.on('close', function() {
			logger.debug('close', client.jid);
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
			internal.clients.get(ping.attrs.from).send(answer);
		})

		/**
		 * message handler
		 */
		client.on('message', function(message) {
			logger.debug('message', client.jid);
			if (internal.resources.exist(message.attrs.to)) {
				internal.resources.get(message.attrs.to).send(message);
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
				var user = internal.clients.get(cli);
				result.c("item", {
					name : user.jid.local + '@' + user.jid.domain,
					jid : user.jid.local + '@' + user.jid.domain + '/' + user.jid.resource,
					subscription : 'both'
				}).c("group", {}).t('jarvis').up();
			}
			client.send(result);
		});

		/**
		 * Cf. http://xmpp.org/extensions/xep-0144.html
		 */
		client.on('query:set:jabber:iq:roster', function(query) {
			var item = query.getChild('query').getChild('item');
			/**
			 * read query
			 */
			if (internal.clients.exist(item.attrs.jid)) {
				logger.warn("query:set:jabber:iq:roster", item.attrs);
			}
		});
	})

	/**
	 * callback handler
	 */
	done()
}
