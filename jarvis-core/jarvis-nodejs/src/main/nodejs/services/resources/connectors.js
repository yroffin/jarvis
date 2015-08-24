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
var kernel = require(__dirname + '/../core/kernel');

var _registry = {};

var _connectors = {
    /**
     * register connector
     * @param callback
     */
    register : function(body, callback) {
        var body = body.session.client;
        if(body.id != undefined) {
            if(_registry[body.id] === undefined) {
                /**
                 * register new connector
                 */
                kernel.notify("Register " + body.id);
                _registry[body.id] = body;
            }
            callback(body);
        } else {
            callback();
        }
    },
    /**
     * get all connector
     * @param callback
     */
    get : function(callback) {
        var filtered = [];
        for(var key in _registry) {
            filtered.push({
                id : _registry[key].id,
                href : _registry[key].href,
                name : _registry[key].name,
                isRenderer : _registry[key].isRenderer,
                isSensor : _registry[key].isSensor,
                canAnswer : _registry[key].canAnswer
            });
        }
        callback(filtered);
    },
    /**
     * get by id
     * @param id
     * @param callback
     */
    getById: function (key, callback) {
        if (_registry[key] !== undefined) {
            var result = {
                id : _registry[key].id,
                href : _registry[key].href,
                name : _registry[key].name,
                isRenderer : _registry[key].isRenderer,
                isSensor : _registry[key].isSensor,
                canAnswer : _registry[key].canAnswer
            };
            callback(result);
        } else {
            callback();
        }
    }
}

/**
 * connectors restfull api
 * @type {
 *  {job: {get: Function, head: Function, put: Function, patch: Function, delete: Function},
 *  jobs: {get: Function, head: Function, post: Function, delete: Function}}
 * }
 */
module.exports = {
    services: {
        /**
         * task handler
         * - register
         * @param req
         * @param res
         * @returns {*|ServerResponse}
         */
        task : function(req, res) {
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
             * core get functions
             */
            var _result;
            if(req.body && req.query.method) {
                if(req.query.method == 'register') {
                    _result = _connectors.register(req.body, callback);
                    return;
                }
                return res.status(400).json({});
            } else {
                return res.status(400).json({});
            }
        },
        /**
         * push a single body on connector by id
         * @param id
         * @param body
         * @param callback
         * @param failure
         */
        push : function(id, body, callback, failure) {
            new Client(_registry[id].href).call('/' + _registry[id], body, 'post', callback, failure);
        }
    },
    resources : {
        /**
         * get all internal connectors
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
             * core get functions
             * if any id or name then it's a single resource request
             * else recover all exiting resources
             */
            if(req.params.id) {
                _connectors.getById(req.params.id, callback);
            } else {
                _connectors.get(callback);
            }
        },
    }
}
