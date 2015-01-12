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
 * logging
 */
var logger = require('blammo').LoggerFactory.getLogger('mongodb');

/**
 * native mongo driver for raw operation
 */
var mongoclient = require('mongodb');
var ___mongoconnector = undefined;

mongoclient.connect("mongodb://localhost:27017/jarvis", function(err, database) {
	if (err) {
		logger.error("Error(s), while connecting to mongodb");
	} else {
		logger.info("Successfull connection to mongodb:", database.databaseName);
		___mongoconnector = database;
	}
});

/**
 * object model oriented mongodb driver
 */
var mongoose = require('mongoose').connect('mongodb://localhost:27017/jarvis');

/**
 * retrieve all collections stored in mongodb
 */
exports.getCollections = function() {
	var collections = [];

	/**
	 * find collections
	 */
	___mongoconnector.collectionNames(function(err, cols) {
		if (cols) {
			collections = cols;
		}
	});

	if (collections.length == 0) {
		/**
		 * default collections are needed
		 */
		___mongoconnector.createCollection("config");
	}
	return {};
};
