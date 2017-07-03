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
import { Http } from '@angular/http';

import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';
import { JarvisDataLinkedResource } from './jarvis-data-linked-resource';

/**
 * data model
 */
import { DataSourceBean } from './../model/connector/datasource-bean';
import { MeasureBean } from './../model/connector/measure-bean';

@Injectable()
export class JarvisDataDatasourceService extends JarvisDataCoreResource<DataSourceBean> implements JarvisDefaultResource<DataSourceBean> {

    public allLinkedMeasures: JarvisDefaultLinkResource<MeasureBean>;

    constructor(
        private _http: Http,
        private _configuration: JarvisConfigurationService
    ) {
        super(_configuration, _configuration.ServerWithApiUrl + 'datasources', _http);
        /**
         * map linked elements
         */
        this.allLinkedMeasures = new JarvisDataLinkedResource<MeasureBean>(this.actionUrl, '/measures', _http);
    }

}
