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
exports.start = function () {
	/**
	 * Keep track of the chat clients
	 */
	api.clearClients();

	/**
	 * Start a TCP Server
	 */
	net.createServer(function(socket) {

		/**
		 * Identify this client
		 */
		socket.name = socket.remoteAddress + ":" + socket.remotePort

		/**
		 * store socket in context
		 * put this new client in the list
		 */
		api.addClient(socket);

		/**
		 * Send a nice welcome message and announce
		 */
		socket.write("Welcome " + socket.name + "\n");
		broadcast(socket.name + " joined the chat\n", socket);

		/**
		 * Handle incoming messages from clients.
		 */
		socket.on('data', function(data) {
			socket.write("\r\n" + socket.name + "@jarvis: ");
			handle(data, socket);
			broadcast(socket.name + "> " + data, socket);
		});

		/**
		 * Remove the client from the list when it leaves
		 */
		socket.on('end', function() {
			kernel.getClients().splice(api.getClients().indexOf(socket), 1);
			broadcast(socket.name + " left the chat.\n");
		});

		/**
		 * Send a message to all clients
		 */
		function handle(message, sender) {
			console.info("message[%s]", message);
			/**
			 * handle list command
			 */
			if(message == 'list') {
				console.info("List client(s)");
				api.getClients().forEach(function(client) {
					sender.write("\r\n" + client.name);
				});
			}
		}

		/**
		 * Send a message to all clients
		 * note : exlucde using api level, we must acquire the pysical socket
		 * client stored in list
		 */
		function broadcast(message, sender) {
			kernel.getClients().forEach(function(client) {
				// Don't want to send it to sender
				if (client === sender)
					return;
				if(client != undefined) {
					try {
						client.write(message);
					}catch (excp) {
						console.error(excp);
					}
				}
			});
			// Log it to the server output too
			process.stdout.write(message)
		}

	}).listen(5000);

	// Put a friendly message on the terminal of the server.
	console.log("Chat server running at port 5000\n");
}

