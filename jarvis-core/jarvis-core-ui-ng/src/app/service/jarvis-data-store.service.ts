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
import {Subject} from 'rxjs/Rx';

import { WindowRef } from './jarvis-utils.service';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';

@Injectable()
export class JarvisDataStoreService {

  private myToken: string = '';
  private myViews: ViewBean[];
  private myViewsChange: Subject<ViewBean[]> = new Subject<ViewBean[]>();

  constructor(
    private _windowService: WindowRef
    ) {

  }

  public getViews(): ViewBean[] {
    return this.myViews;
  }

  public setViews(toStore: ViewBean[]) {
    this.myViews = toStore;
  }

  /**
   * retrieve token
   */
  public getToken(): string {
    return this.myToken;
  }

  /**
   * set token
   */
  public setToken(token: string) {
    this.myToken = token;
  }

  /**
   * validate credentials
   */
  public isConnected(): boolean {
    /**
     * find any token
     */
    let accessToken: string = this._windowService.getHash();
    if (accessToken && accessToken.indexOf('#access_token=') > -1) {
      let token: string = accessToken.match(/^(.*?)&/)[1].replace('#access_token=', '');
      this.setToken(token);
    }

    /**
     * assert is connected
     */
    var that = this;
    if (this.getToken() === '' && !this._windowService.getHref().endsWith('/login')) {
      this._windowService.setHref('http://' + this._windowService.getHost() +'/login');
      return false;
    }
    return true;
  }
}
