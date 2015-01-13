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

var logger = require('blammo').LoggerFactory.getLogger('services');

var api = require(__dirname + '/../core/api');

exports.init = function() {
	return;
};

/**
 * info services
 */
exports.info = function(req, res) {
	logger.info('info() key [%s]', req.params.key);
	/**
	 * properties
	 */
	if (req.params.key == 'properties') {
		res.json({
			properties : [ {
				key : 'test',
				value : 'test'
			}, {
				key : 'test',
				value : 'test'
			}, {
				key : 'test',
				value : 'test'
			}, {
				key : 'test',
				value : 'test'
			} ]
		});
		return;
	}
	/**
	 * clients
	 */
	if (req.params.key == 'clients') {
		res.json({
			clients : api.getClients()
		});
		return;
	}
	/**
	 * clients
	 */
	if (req.params.key == 'events') {
		res.json({
			events : api.getEvents()
		});
		return;
	}
	res.json({});
};
