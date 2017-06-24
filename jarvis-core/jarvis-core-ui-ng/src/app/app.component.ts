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

import { MenuItem } from 'primeng/primeng';

import { JarvisSecurityService } from './service/jarvis-security.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';

/**
 * data model
 */
import { ResourceBean } from './model/resource-bean';
import { Oauth2Bean, MeBean } from './model/security/oauth2-bean';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  /**
   * global system menu
   */
  private items: MenuItem[];

  /**
   * constructor
   */
  constructor(
    private _jarvisSecurityService: JarvisSecurityService,
    private _jarvisDataStoreService: JarvisDataStoreService
  ) {
  }

  /**
   * global init of system menu
   */
  ngOnInit() {
    /**
     * get profile from store
     */
    this._jarvisDataStoreService.getMe((data: MeBean) => {
        this.loadMenu();
    });
  }

  /**
   * test if isMobile
   */
  public isMobile(): boolean {
    if (window.matchMedia("(min-width: 400px)").matches) {
      /*
       * the view port is at least 400 pixels wide
       **/
      return false;
    } else {
      /*
       * the view port is less than 400 pixels wide
       **/
      return true;
    }
  }

  /**
   * the given event on the document root.
   */
  private loadMenu(): void {
    /**
     * global item menu
     */
    this.items = [
      {
        label: 'Home',
        icon: 'fa-home',
        routerLink: ['/desktop']
      },
      {
        label: 'Ressource',
        icon: 'fa-sliders',
        items: [
          {
            label: 'Core',
            icon: 'fa-code',
            items: [
              { label: 'View', icon: 'fa-briefcase', routerLink: ['/views'] },
              { label: 'Device', icon: 'fa-server', routerLink: ['/devices'] },
              { label: 'Plugin', icon: 'fa-podcast', routerLink: ['/plugins'] },
              { label: 'Command', icon: 'fa-plug', routerLink: ['/commands'] },
              { label: 'Connector', icon: 'fa-bug', routerLink: ['/connectors'] }
            ]
          },
          {
            label: 'Activation',
            icon: 'fa-sliders',
            items: [
              { label: 'Trigger', icon: 'fa-paper-plane', routerLink: ['/triggers'] },
              { label: 'Cron', icon: 'fa-inbox', routerLink: ['/crons'] },
              { label: 'Notification', icon: 'fa-bolt', routerLink: ['/notifications'] },
            ]
          },
          {
            label: 'Code',
            icon: 'fa-code-fork',
            items: [
              { label: 'Scenario', icon: 'fa-flash', routerLink: ['/scenarios'] },
              { label: 'Block', icon: 'fa-exchange', routerLink: ['/blocks'] }
            ]
          },
          {
            label: 'Misc',
            icon: 'fa-cube',
            items: [
              { label: 'Configuration', icon: 'fa-database', routerLink: ['/configurations'] },
              { label: 'Snapshot', icon: 'fa-clone', routerLink: ['/snapshots'] },
              { label: 'Property', icon: 'fa-code', routerLink: ['/properties'] },
              { label: 'DataSource', icon: 'fa-podcast', routerLink: ['/datasources'] },
              { label: 'Server', icon: 'fa-line-chart', routerLink: ['/resources'] },
              { label: 'Measure', icon: 'fa-line-chart', routerLink: ['/measures'] }
            ]
          }
        ]
      }
    ];
  }
}
