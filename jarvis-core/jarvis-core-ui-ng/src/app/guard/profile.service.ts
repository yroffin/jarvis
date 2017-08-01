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
import { PlatformLocation } from '@angular/common';
import { DOCUMENT } from "@angular/platform-browser";

import { Subject } from 'rxjs/Rx';
import { Router, ActivatedRoute, Params } from '@angular/router';

import * as _ from 'lodash';

import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";

import { WindowRef } from '../service/jarvis-utils.service';
import { JarvisLoaderService } from '../service/jarvis-loader.service';
import { LoggerService } from '../service/logger.service';
import { JarvisSecurityService } from '../service/jarvis-security.service';
import { JarvisConfigurationService } from '../service/jarvis-configuration.service';

/**
 * data model
 */
import { Oauth2Bean, MeBean } from '../model/security/oauth2-bean';

@Injectable()
export class ProfileGuard implements CanActivate {

  /**
   * profile
   */
  private me: MeBean;
  private myToken: string = '';

  constructor(
    private logger: LoggerService,
    private windowService: WindowRef,
    private router: Router,
    private platformLocation: PlatformLocation,
    private jarvisSecurityService: JarvisSecurityService,
    private jarvisLoaderService: JarvisLoaderService,
    private jarvisConfigurationService: JarvisConfigurationService
  ) {
  }

  /**
   * retrieve me
   */
  public getMe(): MeBean {
    return this.me;
  }

  /**
   * load profile and verify credentials
   * @param destination 
   * @param state 
   */
  public canActivate(destination: ActivatedRouteSnapshot, state: RouterStateSnapshot):  Observable<boolean> {
    /**
     * define an observable to authorize activity in ui
     * after using all connect and me api
     */
    let myObservable: Observable<boolean>;
    myObservable = new Observable<boolean>(observer => {
      if (this.me) {
        this.logger.info('Logged on', this.me);
        this.jarvisConfigurationService.setJarvisAuthToken(this.me.attributes.token);
        observer.next(true);
      } else {
        this.logger.info('Not logged on');
        this.connect(observer, state.url);
      }
    });
    return myObservable;
  }

  /**
   * connect processus
   * @param observer 
   */
  private connect(observer: Subscriber<boolean>, currentRoute: string) {
      let connect: boolean;
      this.jarvisSecurityService.Connect()
        .subscribe(
        (data: boolean) => connect = data,
        (error: any) => {
          this.logger.error("Credential not valid");
          observer.next(false);
        },
        () => {
          this.getMeFromApi(connect, currentRoute)
            .subscribe(
            (data: MeBean) => {
              this.me = data
            },
            (error: any) => {
              this.logger.error("Credential not valid");
              observer.next(false);
            },
            () => {
              /**
               * credential are valid
               */
              observer.next(true);
            });
        });
  }

  /**
   * get de from api
   */
  private getMeFromApi(connect: boolean, currentRoute: string): Observable<MeBean> {
    /**
     * find any token on url
     */
    let accessToken: string = this.windowService.getHash();
    if (accessToken && accessToken.indexOf('#access_token=') > -1) {
      this.myToken = accessToken.match(/^(.*?)&/)[1].replace('#access_token=', '');
    }

    return new Observable<MeBean>(obs => {
      /**
       * when connect is needed, token must be set
       */
      if (this.myToken === '' && connect) {
        this.router.navigate(["/login"]);
        this.jarvisLoaderService.setLoaded();
        obs.next();
        obs.complete();
        return;
      }
  
      /**
       * launch security me acquire
       */
      let profile: MeBean;
      this.jarvisSecurityService.Me(this.myToken)
        .subscribe(
        (data: MeBean) => profile = data,
        (error: any) => {
          this.jarvisLoaderService.setLoaded();
        },
        () => {
          obs.next(profile);
          obs.complete();
          this.router.navigate([currentRoute]);
          this.jarvisLoaderService.setLoaded();
        });
      return;
    });
  }
}