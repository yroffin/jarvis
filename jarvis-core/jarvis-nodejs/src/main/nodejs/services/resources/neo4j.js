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

var logger = require('blammo').LoggerFactory.getLogger('services');

var neo4jdb = require(__dirname + '/../core/neo4jdb');

var _neo4jLabels = {
    /**
     * get all labels
     * @param callback
     */
    get : function(callback) {
        neo4jdb.label.get(callback);
    },
    /**
     * get by id
     * @param id
     * @param callback
     */
    getById: function (id, callback) {
        var result = {};
        neo4jdb.label.all(id, function(rows) {
            callback(rows);
        });
    }
}

/**
 * jobs restfull api
 * @type {
 *  {job: {get: Function, head: Function, put: Function, patch: Function, delete: Function},
 *  jobs: {get: Function, head: Function, post: Function, delete: Function}}
 * }
 */
module.exports = {
    get: function(req, res) {
        /**
         * simple call back to handle result
         * @param _result
         * @returns {*}
         */
        function callback(_result) {
            if(!_result) {
                return res.status(404).json({});
            } else return res.json(_result);
        }
    },
    labels:{
        /**
         * get all labels
         * @param req
         * @param res
         */
        get : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * labels extraction
             */
            if(req.params.id) {
                _neo4jLabels.getById(req.params.id, callback, callback);
            } else {
                _neo4jLabels.get(callback);
            }
        }
    }
}
