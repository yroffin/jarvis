/**
 * Copyright 2015 Yannick Roffin.
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

var microphone = require('./microphone');

/**
 * voice driver based on superagent
 */
var request = require('superagent');

var defaults = {
    client: 'chromium',
    clipSize: 15,
    lang: 'en-US',
    maxRequests: 4,
    maxResults: 1,
    pfilter: 1,
    sampleRate: 44000,
    xjerr: 1
};

var post = function(opts, callback, failure) {
    /**
     * build a default rest request
     */
    request
        .post('https://www.google.com/speech-api/v2/recognize')
        .type('audio/x-flac; rate=' + opts.sampleRate)
        .parse(request.parse.text)
        .query({key: opts.key})
        .query({lang: opts.lang})
        .query({maxResults: opts.maxResults})
        .query({pfilter: opts.pfilter ? 1 : 0})
        .send(data)
        .end(function (err, res) {
            var text = res.text;
            if (err) return done(err);
            if (text) text = text.split('\n')[1];
            if (!text) return done(null, {result: []});
            try {
                done(null, JSON.parse(text));
            } catch (ex) {
                done(ex);
            }
        });
}

module.exports = {
    recognize : function() {
        post(
            defaults,
            function() {
            /**
             * success
             */
        },
        function() {
            /**
             * failure
             */
        })
    }
}

microphone.startCapture();
module.exports.recognize();