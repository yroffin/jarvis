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

import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from '../../service/jarvis-data-core-resource';

/**
 * core resource
 */
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';

@Component({
  selector: 'app-jarvis-resources',
  templateUrl: './jarvis-resources.component.html',
  styleUrls: ['./jarvis-resources.component.css']
})
export class JarvisResourcesComponent implements OnInit {

  myResourceName: string = "default";
  myResources: ResourceBean[];

  /**
   * constructor
   */
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataPluginService: JarvisDataPluginService,
    private _jarvisDataCommandService: JarvisDataCommandService,
    private _jarvisDataTriggerService: JarvisDataTriggerService,
    private _jarvisDataCronService: JarvisDataCronService
  ) {
  }

  ngOnInit() {
    /**
     * listen on route change
     */
    this.router.events
      .filter(event => event instanceof NavigationEnd)
      .subscribe((navigationEnd: NavigationEnd) => {
        // You only receive NavigationEnd events
        if (navigationEnd.url === '/devices') {
          this.load('devices', this._jarvisDataDeviceService);
        }
        if (navigationEnd.url === '/plugins') {
          this.load('plugins', this._jarvisDataPluginService);
        }
        if (navigationEnd.url === '/commands') {
          this.load('commands', this._jarvisDataCommandService);
        }
        if (navigationEnd.url === '/triggers') {
          this.load('triggers', this._jarvisDataTriggerService);
        }
        if (navigationEnd.url === '/crons') {
          this.load('crons', this._jarvisDataCronService);
        }
      });
  }

  /**
   * load this component with a new resource
   */
  public load<T extends ResourceBean>(res: string, jarvisDataService: JarvisDefaultResource<T>): void {
    /**
     * check if already loaded
     */
    if (this.myResourceName === res) {
      return;
    } else {
      this.myResourceName = res;
    }

    jarvisDataService.GetAll()
      .subscribe(
      (data: ResourceBean[]) => this.myResources = data,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * view this resource
   */
  public view(resource: ResourceBean) {
    this.router.navigate(['/'+this.myResourceName+'/' + resource.id]);
  }
}
