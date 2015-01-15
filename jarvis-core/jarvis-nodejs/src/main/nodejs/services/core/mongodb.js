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
var _db_jarvis = undefined;
var _db_blammo = undefined;

/**
 * object model oriented mongodb driver
 */
var mongooseclient = require('mongoose');

/**
 * retrieve all collections stored in mongodb
 */
exports.init = function() {
	/**
	 * main database
	 */
	mongoclient.connect("mongodb://localhost:27017/jarvis", function(err, database) {
		if (err) {
			logger.error("Error(s), while connecting to mongodb");
		} else {
			logger.info("Successfull connection to mongodb:", database.databaseName);
			_db_jarvis = database;
		}
	});

	/**
	 * log database
	 */
	mongoclient.connect("mongodb://localhost:27017/blammo", function(err, database) {
		if (err) {
			logger.error("Error(s), while connecting to mongodb");
		} else {
			logger.info("Successfull connection to mongodb:", database.databaseName);
			_db_blammo = database;
		}
	});

	/**
	 * object database
	 */
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
function __getSyncCollections(database) {
	var collections = {
		'result' : undefined
	};

	/**
	 * find collections
	 */
	database.collectionNames(function(err, replies) {
		collections.result = replies;
	});
	waitFor(collections);

	return collections.result;
}

/**
 * retrieve all collections stored in mongodb
 */
exports.getSyncCollections = function() {
	var collections_jarvis = __getSyncCollections(_db_jarvis);
	var collections_blammo = __getSyncCollections(_db_blammo);
	var collections = [];

	if (collections_jarvis.length == 0) {
		/**
		 * default collections are needed
		 */
		_db_jarvis.createCollection("config", function() {
			logger.info("Create default mongodb objects:", _db_jarvis.databaseName);
		});

		/**
		 * refresh it
		 */
		collections_jarvis = __getSyncCollections();
	}

	logger.info("Collections/jarvis:", collections_jarvis.length);
	logger.info("Collections/blammo:", collections_blammo.length);

	collections_jarvis.forEach(function(item) {
		item.db = 'jarvis';
		item.name = item.name.replace('jarvis.', '');
		collections.push(item);
	});

	collections_blammo.forEach(function(item) {
		item.db = 'blammo';
		item.name = item.name.replace('blammo.', '');
		collections.push(item);
	});

	logger.info("Collections:", collections);
	return collections;
};

/**
 * sync count collections
 */
function __syncCountCollectionByName(col) {

	var collections = {
		'result' : undefined
	};

	/**
	 * find in collection
	 */
	col.find({}).toArray(function(err, items) {
		if (err == null) {
			collections.result = items.length;
		}
	});

	/**
	 * wait for completion
	 */
	waitFor(collections);

	logger.info('syncCountCollectionByName => %s', collections.result);

	return Number(collections.result);
};

/**
 * sync page collections
 */
function __syncPageCollectionByName(col, offset, page) {

	var collections = {
		'result' : undefined
	};

	var options = {
		"limit" : page,
		"skip" : offset
	}

	/**
	 * find in collection
	 */
	col.find({}, options).toArray(function(err, items) {
		if (err == null) {
			collections.result = items;
		}
	});

	/**
	 * wait for completion
	 */
	waitFor(collections);

	logger.info('__syncPageCollectionByName => %s', collections.result.length);

	return collections.result;
};

/**
 * find
 */
exports.syncCountCollectionByName = function(database, name) {
	var col = undefined;
	if (database == 'jarvis') {
		col = _db_jarvis.collection(name);
	}
	if (database == 'blammo') {
		col = _db_blammo.collection(name);
	}
	logger.info('syncCountCollectionByName(%s)', name);
	return __syncCountCollectionByName(col);
};

/**
 * find
 */
exports.syncPageCollectionByName = function(database, name, offset, page) {
	var col = undefined;
	if (database == 'jarvis') {
		col = _db_jarvis.collection(name);
	}
	if (database == 'blammo') {
		col = _db_blammo.collection(name);
	}
	logger.info('syncCountCollectionByName(%s)', name);
	return __syncPageCollectionByName(col, offset, page);
};
