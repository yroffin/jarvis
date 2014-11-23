/* 
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

var blammo = require('blammo');
var logger = blammo.LoggerFactory.getLogger('logger1');

var api = require(__dirname + '/../core/api');

exports.init = function () {
  return;
};

/**
 * send services
 */
exports.send = function (req, res) {
	var target = JSON.parse(req.query.target);
	logger.info('send() [%s, %s]', target.id, req.query.message);
	/**
	 * find target client
	 */
	var descriptor = api.findDescriptorById(target.id).descriptor;
	
	/**
	 * Send a nice welcome message and announce
	 */
	api.sendMessage({
		'code' : 'request',
		'request' : {
			'data' : req.query.message
		},
		'session' : {
			'client' : {
				'id' : descriptor.id
			}
		}
	}, descriptor.socket);

	res.json({});
};
