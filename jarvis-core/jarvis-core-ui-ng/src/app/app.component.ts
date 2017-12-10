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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';
import { Router } from '@angular/router';
import { State, Store } from '@ngrx/store';
import { Message } from 'primeng/primeng';

import { MatSidenav } from '@angular/material';

import { MenuItem } from 'primeng/primeng';

import { WindowRef } from './service/jarvis-utils.service';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisSecurityService } from './service/jarvis-security.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';
import { ProfileGuard } from './guard/profile.service';

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

  @ViewChild('sidenav') sidenav: MatSidenav;
  myInnerHeight: any;

  /**
   * global system menu
   */
  private items: MenuItem[];
  public dispMe: boolean = false;
  public dispHelp: boolean = false;
  public dispMenu: boolean = false;

  public msgs: Message[] = <Message[]> [];
  public messageObservable: Observable<Message> = new Observable<Message>();

  public me: MeBean;
  public help: string;

  /**
   * constructor
   */
  constructor(
    private profile: ProfileGuard,
    private http: Http,
    private router: Router,
    private windowRef: WindowRef,
    private configuration: JarvisConfigurationService,
    private jarvisSecurityService: JarvisSecurityService,
    private jarvisDataStoreService: JarvisDataStoreService,
    private store: Store<State<Message>>
  ) {
    this.myInnerHeight = windowRef.getWindow();

    /**
      * register to store
      */
    this.messageObservable = this.store.select<Message>('Message');
    /**
     * register to store update
     */
    this.messageObservable
      .filter(item => {
        if (item) {
          if (item.detail && item.severity && item.summary) {
            return true;
          }
          return false
        } else {
          return false
        }
      })
      .subscribe((item) => {
        this.msgs.splice(0,this.msgs.length);
        this.msgs.push(item);
      });
  }

  /**
   * show me
   */
  showMe() {
    this.me = this.profile.getMe();
    this.dispMe = true;
  }

  /**
   * show help
   */
  showMenu() {
    this.dispMenu = true;
    this.dispHelp = false;
    this.sidenav.toggle();
  }

  /**
   * show help
   */
  showHelp() {
    let text;
    let id = 'help' + this.router.routerState.snapshot.url
      .replace(/\//g, "-")
      .replace(/0|1|2|3|4|5|6|7|8|9/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/00/g, "0")
      .replace(/0/g, "by-id");
    this.GetHelp(id + '.markdown').subscribe(
      (data) => {
        text = data;
      },
      (error) => {
        this.help = 'TODO';
      },
      () => {
        this.help = text;
      }
    );
    this.dispMenu = false;
    this.dispHelp = true;
    this.sidenav.toggle();
  }

  /**
   * get single resource
   */
  public GetHelp = (id: string): Observable<string> => {
    let headers = new Headers();
    headers.append('JarvisAuthToken', this.configuration.getJarvisAuthToken());
    return this.http.get(this.configuration.ServerWithApiUrl + 'helps/fr/' + id, { headers: headers })
      .map((response: Response) => <string>response.text())
      .catch(this.handleError);
  }

  /**
   * error handler
   */
  protected handleError(error: Response) {
    return Observable.throw(error || 'Server error');
  }

  /**
   * global init of system menu
   */
  ngOnInit() {
    /**
     * get profile from store
     */
    this.loadMenu();
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
              { label: 'Broker', icon: 'fa-exchange', routerLink: ['/broker'] },
            ]
          },
          {
            label: 'Code',
            icon: 'fa-code-fork',
            items: [
              { label: 'Process', icon: 'fa-flash', routerLink: ['/processes'] },
              { label: 'Model', icon: 'fa-briefcase', routerLink: ['/models'] }
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
