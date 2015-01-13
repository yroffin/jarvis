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
var deasync = require('deasync');

/**
 * native mongo driver for raw operation
 */
var mongoclient = require('mongodb');
var _db = undefined;

/**
 * object model oriented mongodb driver
 */
var mongooseclient = require('mongoose');

/**
 * retrieve all collections stored in mongodb
 */
exports.init = function() {
	mongoclient.connect("mongodb://localhost:27017/jarvis", function(err, database) {
		if (err) {
			logger.error("Error(s), while connecting to mongodb");
		} else {
			logger.info("Successfull connection to mongodb:", database.databaseName);
			_db = database;
		}
	});

	mongooseclient.connect('mongodb://localhost:27017/jarvis');
}

/**
 * wait for initialization
 * 
 * @param variable
 */
function waitFor(data) {
	logger.info("Waiting for ", data);
	while (data.result === undefined) {
		logger.info("Waiting for ", data);
		deasync.sleep(10);
	}
}

/**
 * private function
 * 
 * @returns
 */
function __getSyncCollections() {
	var collections = {
		'result' : undefined
	};

	/**
	 * find collections
	 */
	_db.collectionNames(function(err, replies) {
		collections.result = replies;
	});
	waitFor(collections);

	return collections.result;
}

/**
 * retrieve all collections stored in mongodb
 */
exports.getSyncCollections = function() {
	var collections = __getSyncCollections();

	if (collections.length == 0) {
		/**
		 * default collections are needed
		 */
		_db.createCollection("config", function() {
			logger.info("Create default mongodb objects:", _db.databaseName);
		});

		/**
		 * refresh it
		 */
		collections = __getSyncCollections();
	}

	logger.info("Collections:", collections);
	return collections;
};
