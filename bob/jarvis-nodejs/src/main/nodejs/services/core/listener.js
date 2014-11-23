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
 * Load the TCP Library
 */
net = require('net');

var kernel = require(__dirname + '/kernel');
var api = require(__dirname + '/api');

/**
 * main services
 */
exports.start = function() {
	/**
	 * Keep track of the chat clients
	 */
	api.clearClients();

	/**
	 * Start a TCP Server
	 */
	var chatServer = net.createServer(exports.handler);
	/**
	 * error handler
	 */
	chatServer.on('error', function(e) {
		if (e.code == 'EADDRINUSE') {
			console.log('Address 5000 in use, retrying...');
			setTimeout(function() {
				chatServer.listen(5000);
			}, 5000);
		}
	});
	chatServer.listen(5000);

	// Put a friendly message on the terminal of the server.
	console.log("Chat server running at port 5000\n");
}

/**
 * handler services
 */
exports.handler = function(socket) {
	/**
	 * Error handler
	 */
	socket.on('error', function(e) {
		/**
		 * unable to build exchange protocol with this client
		 */
		console.error(e);
	});

	/**
	 * Identify this client (create) during socket
	 * connect phase
	 */
	var descriptor = {
		'id' : socket.remoteAddress + ":" + socket.remotePort,
		'socket' : socket
	};

	/**
	 * Send a nice welcome message and announce
	 */
	api.sendMessage({
		'code' : 'welcome',
		'welcome' : {
			'data' : descriptor.id + ' connexion'
		},
		'session' : {
			'client' : {
				'id' : descriptor.id
			}
		}
	}, socket);

	/**
	 * store descriptor/socket in context put this new client in the list
	 */
	api.addClient(descriptor);

	/**
	 * Handle incoming messages from clients.
	 */
	socket.on('data', function(data) {
		/**
		 * handle this new message
		 */
		console.info("message to parse[%s]", data);
		handle(socket, JSON.parse(data));
		broadcast({
			'code' : 'ack',
			'ack' : {
				'data' : 'jarvis@' + descriptor.id
			}
		}, socket);
	});

	/**
	 * Remove the client from the list when it leaves
	 */
	socket.on('end', function() {
		kernel.getClients().splice(api.getClientIndexOf(socket).index, 1);
		broadcast({
			'code' : 'bye',
			'bye' : {
				'data' : descriptor.id + ' exit'
			}
		});
	});

	/**
	 * Send a message to all clients
	 */
	function handle(sender, message) {
		/**
		 * handle welcome reply
		 */
		if (message.code == 'welcome') {
			var descriptor = api.getClientIndexOf(sender).descriptor;
			/**
			 * store element from client in session
			 * onto descriptor element
			 */
			descriptor.name = message.session.client.name;
			descriptor.canAnswer = message.session.client.canAnswer;
			descriptor.isRenderer = message.session.client.isRenderer;
			descriptor.isSensor = message.session.client.isSensor;
			return;
		}
		/**
		 * handle welcome reply
		 */
		if (message.code == 'event') {
			console.info(message.event.data);
			return;
		}
		/**
		 * handle list command
		 */
		if (message.code == 'list') {
			console.info("List client(s)");
			api.sendMessage({
				'code' : 'list',
				'list' : {
					'client' : api.getClients()
				}
			}, sender);
			return;
		}
	}

	/**
	 * Send a message to all clients note : exlucde using api level, we must
	 * acquire the pysical socket client stored in list
	 */
	function broadcast(message, sender) {
		kernel.getClients().forEach(function(descriptor) {
			/**
			 * Don't want to send it to sender
			 */
			if (descriptor.socket === sender)
				return;
			if (descriptor != undefined) {
				api.sendMessage(message, descriptor.socket);
			}
		});
	}
}
