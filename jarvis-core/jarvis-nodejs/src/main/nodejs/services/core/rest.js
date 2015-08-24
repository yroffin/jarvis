/**
 * Copyright 2015 Yannick Roffin.
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
 * rest api
 */
var rest = require('rest/client/node');
var basicAuth = require('rest/interceptor/basicAuth');
var mime = require('rest/interceptor/mime');
var errorCode = require('rest/interceptor/errorCode');
var timeout = require('rest/interceptor/timeout');

module.exports = {
    /**
     * constructor
     * @param url
     * @constructor
     */
    instance: function(url) {
        this.url = url;
    },
    /**
     * default method
     * @param handle
     * @param path
     * @param body
     * @param method
     * @param callback
     * @param failure
     */
    call: function (path, body, method, callback, failure) {
        /**
         * build a default rest request
         * @type {{entity: *, path: *, method: *, headers: {}}}
         */
        var request = {
            entity: body,
            path: this.url + path,
            method: method,
            headers: {}
        };

        /**
         * todo: verify this code
         * @type {string}
         */
        request.headers['Access-Control-Allow-Origin'] = '*';

        /**
         * initialize client interceptors
         * @type {Client|*}
         */
        var res = rest
            .wrap(basicAuth, {username: handle.user, password: handle.password})
            .wrap(mime, {mime: 'application/json'})
            .wrap(errorCode, {code: 400})
            .wrap(timeout, {timeout: 2000});

        /**
         * make rest call
         */
        res(request).then(callback, failure, function (e) {
            console.error(e);
        });
    }
}

