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
import { DOCUMENT } from "@angular/platform-browser";

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Rx';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { WindowRef } from './jarvis-utils.service';
import { JarvisSecurityService } from './jarvis-security.service';

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
  private doc: Document;
  private securityService: Subject<MeBean>;
  private me: MeBean;

  private myToken: string = '';
  private myViews: ViewBean[];
  private myViewsChange: Subject<ViewBean[]> = new Subject<ViewBean[]>();

  constructor(
    @Inject(DOCUMENT) doc: any,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private _windowService: WindowRef,
    private _jarvisSecurityService: JarvisSecurityService
  ) {
    /**
     * implement one subject
     */
    this.securityService = new Subject<MeBean>();
    this.doc = doc;
  }

  /**
   * get profile
   */
  public getMe(meStore: (me: MeBean) => any): void {
    if(this.me) {
      meStore(this.me);
    } else {
      this.getMeFromApi()
        .subscribe(
        (data: MeBean) => this.me = data,
        (error: any) => {
          console.error("Credential not valid");
        },
        () => {
          meStore(this.me);
        });
    }
  }

  /**
   * get de from api
   */
  private getMeFromApi(): Subject<MeBean> {
    /**
     * find any token on url
     */
    let accessToken: string = this._windowService.getHash();
    if (accessToken && accessToken.indexOf('#access_token=') > -1) {
      this.myToken = accessToken.match(/^(.*?)&/)[1].replace('#access_token=', '');
    }

    if (this.myToken === '' && !this._windowService.getHref().endsWith('/login')) {
      console.info('Credential undefined');
      this._windowService.setHref('http://' + this._windowService.getHost() + '/login');
      this.securityService.next();
      return this.securityService;
    }

    /**
     * launch security me acquire
     */
    let profile: MeBean;
    this._jarvisSecurityService.Me(this.myToken)
      .subscribe(
      (data: MeBean) => profile = data,
      (error: any) => {
        console.error('Credential error');
        this.securityService.next();
        this.triggerOnDocument("angular2-app-ready");
      },
      () => {
        console.info('Credential completed');
        this.securityService.next(profile);
        this.securityService.complete();
        this.router.navigateByUrl("/");
        this.triggerOnDocument("angular2-app-ready");
      });

    /**
     * return subject
     */
    return this.securityService;
  }

  public getViews(): ViewBean[] {
    return this.myViews;
  }

  public setViews(toStore: ViewBean[]) {
    this.myViews = toStore;
  }

  /**
   * the given event on the document root.
   */
  private triggerOnDocument(eventType: string): Event {
    return (this.triggerOnElement(this.doc, eventType));
  }

  /**
   * trigger on element
   * Cf. https://github.com/bennadel/JavaScript-Demos/blob/master/demos/pre-bootstrap-evented-loading-screen-angular2
   */
  private triggerOnElement(
    nativeElement: any,
    eventType: string,
    bubbles: boolean = true,
    cancelable: boolean = false
  ): Event {

    var customEvent = this.createEvent(eventType, bubbles, cancelable);

    nativeElement.dispatchEvent(customEvent);

    return (customEvent);
  }

  /**
   * create and return a custom event with the given configuration
   */
  private createEvent(
    eventType: string,
    bubbles: boolean,
    cancelable: boolean
  ): Event {

    // IE (shakes fist) uses some other kind of event initialization. As such, 
    // we'll default to trying the "normal" event generation and then fallback to
    // using the IE version. 
    try {

      var customEvent: any = new CustomEvent(
        eventType,
        {
          bubbles: bubbles,
          cancelable: cancelable
        }
      );
    } catch (error) {
      var customEvent: any = this.doc.createEvent("CustomEvent");
      customEvent.initCustomEvent(eventType, bubbles, cancelable);
    }

    return (customEvent);
  }
}
