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
import * as _ from 'lodash';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from '../../service/jarvis-data-core-resource';

/**
 * core resource
 */
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataViewService } from '../../service/jarvis-data-view.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';
import { JarvisDataScenarioService } from '../../service/jarvis-data-scenario.service';
import { JarvisDataBlockService } from '../../service/jarvis-data-block.service';
import { JarvisDataConfigurationService } from '../../service/jarvis-data-configuration.service';
import { JarvisDataPropertyService } from '../../service/jarvis-data-property.service';
import { JarvisDataConnectorService } from '../../service/jarvis-data-connector.service';
import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';

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
  myService: JarvisDefaultResource<ResourceBean>;

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
    private _jarvisDataScenarioService: JarvisDataScenarioService,
    private _jarvisDataBlockService: JarvisDataBlockService,
    private _jarvisDataConfigurationService: JarvisDataConfigurationService,
    private _jarvisDataPropertyService: JarvisDataPropertyService,
    private _jarvisDataConnectorService: JarvisDataConnectorService,
    private _jarvisDataNotificationService: JarvisDataNotificationService,
    private _jarvisDataCronService: JarvisDataCronService,
    private _jarvisDataViewService: JarvisDataViewService
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
        if (navigationEnd.url === '/notifications') {
          this.load('notifications', this._jarvisDataNotificationService);
        }
        if (navigationEnd.url === '/views') {
          this.load('views', this._jarvisDataViewService);
        }
        if (navigationEnd.url === '/configurations') {
          this.load('configurations', this._jarvisDataConfigurationService);
        }
        if (navigationEnd.url === '/properties') {
          this.load('properties', this._jarvisDataPropertyService);
        }
        if (navigationEnd.url === '/connectors') {
          this.load('connectors', this._jarvisDataConnectorService);
        }
        if (navigationEnd.url === '/blocks') {
          this.load('blocks', this._jarvisDataBlockService);
        }
        if (navigationEnd.url === '/scenarios') {
          this.load('scenarios', this._jarvisDataScenarioService);
        }
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

    /**
     * get all resource
     */
    jarvisDataService.GetAll()
      .subscribe(
      (data: ResourceBean[]) => this.myResources = data,
      error => console.log(error),
      () => {
        this.myService = jarvisDataService;
      }
      );
  }

  /**
   * add a new resource
   */
  public addNewResource(resource: ResourceBean) {
    /**
     * create a single resource
     */
    let created: ResourceBean = <ResourceBean> {};
    created.name = "default";
    created.icon = "save";
    this.myService.Add(created)
      .subscribe(
      (data: ResourceBean) => created = data,
      error => console.log(error),
      () => {
        this.myResources.push(created);
      }
      );
  }

  /**
   * delete resource
   */
  public dropResource(resource: ResourceBean) {
    /**
     * delete a single resource
     */
    this.myService.Delete(resource.id)
      .subscribe(
      (data: ResourceBean) => resource = data,
      error => console.log(error),
      () => {
        _.remove(this.myResources, function(item) {
          return item.id === resource.id;
        })
      }
      );
  }

  /**
   * view this resource
   */
  public view(resource: ResourceBean) {
    this.router.navigate(['/' + this.myResourceName + '/' + resource.id]);
  }
}
