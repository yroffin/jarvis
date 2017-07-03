/* 
 * Copyright 2016 Yannick Roffin.
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

import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';

/**
 * data model
 */
import { ResourceBean } from './../model/resource-bean';

@Injectable()
export class JarvisDataRawService extends JarvisDataCoreResource<ResourceBean> implements JarvisDefaultResource<ResourceBean> {

    /**
     * constructor
     */
    constructor(
        private _http: Http,
        private _configuration: JarvisConfigurationService
    ) {
        super(_configuration, _configuration.ServerWithApiUrl, _http);
    }

    /**
     * get all resources
     */
    public RawGet = (urn: string): Observable<string> => {
        return this.http.get(this.actionUrl + urn)
            .map((response: Response) => <string>response.text())
            .catch(this.handleError);
    }
}
