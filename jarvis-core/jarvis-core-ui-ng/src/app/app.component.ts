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

import { Component, Input, Inject, OnInit } from '@angular/core';
import { DOCUMENT } from "@angular/platform-browser";

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
  private doc: Document;

  /**
   * constructor
   */
  constructor(
    @Inject(DOCUMENT) doc: any,
    private _jarvisSecurityService: JarvisSecurityService,
    private _jarvisDataStoreService: JarvisDataStoreService
  ) {
    this.doc = doc;
  }

  /**
   * global init of system menu
   */
  ngOnInit() {
    if(!this._jarvisDataStoreService.isConnected()) {
      return;
    }

    /**
     * get profile
     */
    let profile: MeBean;
    this._jarvisSecurityService.Me()
      .subscribe(
      (data: MeBean) => profile = data,
      (error: MeBean) => {
        /**
         * advertise
         */
        this.triggerOnDocument("angular2-app-ready");
      },
      () => {
        this.loadMenu();
        /**
         * advertise
         */
        this.triggerOnDocument("angular2-app-ready");
      });
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
        routerLink: ['/']
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
              { label: 'Property', icon: 'fa-code', routerLink: ['/properties'] }
            ]
          }
        ]
      }
    ];
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
