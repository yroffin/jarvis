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
import { Http, Response, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';
import { JarvisDataLinkedResource } from './jarvis-data-linked-resource';

import { JarvisSecurityService } from './jarvis-security.service';
import { JarvisDataDeviceService } from './jarvis-data-device.service';
import { JarvisDataStoreService } from './jarvis-data-store.service';

import * as _ from 'lodash';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';

@Injectable()
export class JarvisDataViewService extends JarvisDataCoreResource<ViewBean> implements JarvisDefaultResource<ViewBean> {

  public allLinkedDevice: JarvisDefaultLinkResource<DeviceBean>;

  /**
   * constructor
   */
  constructor(
    private _http: Http,
    private _configuration: JarvisConfigurationService,
    private _jarvisSecurityService: JarvisSecurityService,
    private _jarvisDataDeviceService: JarvisDataDeviceService
  ) {
    super(_configuration, _configuration.ServerWithApiUrl + 'views', _http);

    /**
     * map linked elements
     */
    this.allLinkedDevice = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/devices', _http);
  }
}
