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

import { Component, Input, OnInit } from '@angular/core';
import { DOCUMENT } from "@angular/platform-browser";
import { Router, ActivatedRoute, Params } from '@angular/router';

import { WindowRef } from '../../service/jarvis-utils.service';
import { JarvisSecurityService } from '../../service/jarvis-security.service';
import { JarvisDataStoreService } from '../../service/jarvis-data-store.service';
import { JarvisLoaderService } from '../../service/jarvis-loader.service';

/**
 * model
 */
import { Oauth2Bean } from '../../model/security/oauth2-bean';

@Component({
  selector: 'app-jarvis-login',
  templateUrl: './jarvis-login.component.html',
  styleUrls: ['./jarvis-login.component.css']
})
export class JarvisLoginComponent implements OnInit {

  public display: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private windowService: WindowRef,
    private jarvisSecurityService: JarvisSecurityService,
    private jarvisDataStoreService: JarvisDataStoreService,
    private jarvisLoaderService: JarvisLoaderService) {

  }

  ngOnInit() {
    this.display = true;
    this.jarvisLoaderService.setLoaded();
  }

  public google(): void {
    this.display = false;

    /**
     * get profile
     */
    let profile: Oauth2Bean;
    this.jarvisSecurityService.Oauth2('google')
      .subscribe(
      (data: Oauth2Bean) => profile = data,
      (error: any) => {
        console.log('Oauth2 error', error);
      },
      () => {
        this.windowService.setHref(profile.url);
      });
  }

  public facebook(): void {
    this.display = false;
  }
}
