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

var logger = require('blammo').LoggerFactory.getLogger('xmppcli');

'use strict';

var xmppcli = require('node-xmpp-client'), Element = require('node-xmpp-core').Stanza.Element, Message = require('node-xmpp-core').Stanza.Message;

/**
 * start xmppcli
 */
exports.start = function(done) {
	// Internal client
	client = new xmppcli.Client({
		jid : 'internal@jarvis.org',
		password : 'alice',
		host : 'localhost',
		port : 5222,
		register : true
	})

	client.on('online', function(e) {
		logger.warn('online/cli', e);
		client.send('<presence/>')
		// Set client's presence
		var status_message = "test";
		client.send(new Element('presence', {
			type : 'available'
		}).c('show').t('chat').up().c('status').t(status_message));
	})

	client.on('error', function(e) {
		logger.warn('error/cli', e);
	})

	/**
	 * stanza handler
	 */
	client.on('stanza', function(stanza) {
		logger.trace('stanza', stanza.attrs.from, stanza.attrs.to,
				stanza.attrs.type);
		var emitType = null;
		if (stanza.getChild('query')) { // Info query get or set
			emitType = 'query:' + stanza.attrs.type + ':'
					+ stanza.getChild('query').attrs.xmlns;
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
	 * presence handler
	 */
	client.on('presence', function(message) {
		logger.warn('presence', message);
	})

	/**
	 * presence handler
	 */
	client.on('message', function(message) {
		logger.warn('message', message);

		/**
		 * ignore everything that isn't a room message
		 */
		if (!message.attrs.type == 'chat') {
			return;
		}

		var body = message.getChild('body');
		/**
		 * message without body is probably a topic change
		 */
		if (!body) {
			return;
		}

		logger.debug('message/text', body.getText());
	})

	client.on('stanzaa', function(stanza) {
		logger.warn('stanza/cli', stanza.type, stanza.from, stanza.to);

		client.send('<presence/>')

		// always log error stanzas
		if (stanza.attrs.type == 'error') {
			logger.error('[error] ' + stanza);
			return;
		}

		// ignore everything that isn't a room message
		if (!stanza.is('message') || !stanza.attrs.type == 'chat') {
			return;
		}

		var body = stanza.getChild('body');
		// message without body is probably a topic change
		if (!body) {
			return;
		}

		return;
		// Extract username
		var from, room, _ref;
		_ref = stanza.attrs.from.split('/'), room = _ref[0], from = _ref[1];
		var message = body.getText();
		logger.warn('stanza/cli', message);
	})

	client.on('end', function() {
		logger.warn('end/cli');
	})

	/**
	 * callback handler
	 */
	done()
}
