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

import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';

import { WindowRef } from './jarvis-utils.service';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';

/**
 * data model
 */
import { ResourceBean } from './../model/resource-bean';
import { Oauth2Bean, MeBean } from './../model/security/oauth2-bean';

@Injectable()
export class JarvisSecurityService extends JarvisDataCoreResource<ResourceBean> implements JarvisDefaultResource<ResourceBean> {

  /**
   * constructor
   */
  constructor(
    private _http: Http,
    private window: WindowRef,
    private _router: Router,
    private _windowService: WindowRef,
    private _jarvisConfigurationService:JarvisConfigurationService) {
    super(_jarvisConfigurationService.ServerWithUrl, _http);
  }

  /**
   * get me resource
   */
  public Me = (token: string): Observable<MeBean> => {
    this.headers.set('JarvisAuthToken', token);
    return this.http.get(this.actionUrl + 'api/profile/me', { headers: this.headers })
      .map((response: Response) => <MeBean> response.json())
      .catch(this.handleError);
  }

  /**
   * get oauth2 resource
   */
  public Oauth2 = (client: string): Observable<Oauth2Bean> => {
    return this.http.get(this.actionUrl + 'oauth2?client='+client+'&oauth2_redirect_uri=http://'+this._windowService.getHost())
      .map((response: Response) => <Oauth2Bean> response.json())
      .catch(this.handleError);
  }
}
