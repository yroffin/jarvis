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
import { PlatformLocation } from '@angular/common';
import { DOCUMENT } from "@angular/platform-browser";
import * as _ from 'lodash';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Rx';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { WindowRef } from './jarvis-utils.service';
import { JarvisSecurityService } from './jarvis-security.service';
import { JarvisDataViewService } from './jarvis-data-view.service';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';
import { Oauth2Bean, MeBean } from './../model/security/oauth2-bean';

@Injectable()
export class JarvisDataStoreService {

  /**
   * internal pipe for security resolve
   */
  private myViews: ViewBean[] = [];
  private myViewsChange: Subject<ViewBean[]> = new Subject<ViewBean[]>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private _jarvisDataViewService: JarvisDataViewService
  ) {
    /**
     * implement one subject
     */
    this.myViews = [];
  }

  /**
   * get profile
   */
  public loadViews(): void {
    /**
     * load views
     */
    this._jarvisDataViewService.Task('*', 'GET', {})
      .subscribe(
      (data: ViewBean[]) => this.setViews(data),
      (error: any) => {
        console.error("Error while loading views");
      },
      () => {
      });
  }

  public getViews(): ViewBean[] {
    return this.myViews;
  }

  public setViews(toStore: ViewBean[]) {
    this.myViews.splice(0, this.myViews.length);
    let that = this;
    _.each(toStore, function (item) {
      that.myViews.push(item);
    });
  }
}
