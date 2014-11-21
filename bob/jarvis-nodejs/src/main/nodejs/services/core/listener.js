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
	 * Identify this client
	 */
	socket.name = socket.remoteAddress + ":" + socket.remotePort;

	/**
	 * write on socket
	 */
	function write(message, target) {
		target.write(JSON.stringify(message));
		target.write("\r\n");
	}

	/**
	 * Send a nice welcome message and announce
	 */
	write({
		'code' : 'welcome',
		'welcome' : {
			'data' : socket.name + ' joined the chat.'
		}
	}, socket);

	/**
	 * store socket in context put this new client in the list
	 */
	api.addClient(socket);

	/**
	 * Handle incoming messages from clients.
	 */
	socket.on('data', function(data) {
		write({
			'code' : 'ack',
			'ack' : {
				'data' : socket.name + ' @jarvis'
			}
		}, socket);
		handle(data, socket);
		broadcast({
			'code' : 'ack',
			'ack' : {
				'data' : socket.name + ' @jarvis'
			}
		}, socket);
	});

	/**
	 * Remove the client from the list when it leaves
	 */
	socket.on('end', function() {
		kernel.getClients().splice(api.getClients().indexOf(socket), 1);
		broadcast({
			'code' : 'bye',
			'bye' : {
				'data' : socket.name + ' left the chat.'
			}
		});
	});

	/**
	 * Send a message to all clients
	 */
	function handle(message, sender) {
		console.info("message[%s]", message);
		/**
		 * handle list command
		 */
		if (message == 'list') {
			console.info("List client(s)");
			broadcast({
				'code' : 'list',
				'list' : {
					'client' : api.getClients()
				}
			});
		}
	}

	/**
	 * Send a message to all clients note : exlucde using api level, we must
	 * acquire the pysical socket client stored in list
	 */
	function broadcast(message, sender) {
		kernel.getClients().forEach(function(client) {
			/**
			 * Don't want to send it to sender
			 */
			if (client === sender)
				return;
			if (client != undefined) {
				write(message, client);
			}
		});
		/**
		 * Log it to the server output too
		 */
		process.stdout.write(JSON.stringify(message))
	}
}
