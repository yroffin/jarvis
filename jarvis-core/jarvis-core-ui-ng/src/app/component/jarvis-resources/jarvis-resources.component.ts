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
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Message } from 'primeng/primeng';

import * as _ from 'lodash';

import { NavigationGuard } from '../../guard/navigation.service';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from '../../service/jarvis-data-core-resource';

/**
 * core resource
 */
import { JarvisMessageService } from '../../service/jarvis-message.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataViewService } from '../../service/jarvis-data-view.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataProcessService } from '../../service/jarvis-data-process.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';
import { JarvisDataConfigurationService } from '../../service/jarvis-data-configuration.service';
import { JarvisDataPropertyService } from '../../service/jarvis-data-property.service';
import { JarvisDataConnectorService } from '../../service/jarvis-data-connector.service';
import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';
import { JarvisDataSnapshotService } from '../../service/jarvis-data-snapshot.service';
import { JarvisDataDatasourceService } from '../../service/jarvis-data-datasource.service';
import { JarvisDataMeasureService } from '../../service/jarvis-data-measure.service';
import { JarvisDataModelService } from '../../service/jarvis-data-model.service';

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

  /**
   * members
   */
  public myResourceName: string = "default";
  public myResources: ResourceBean[] = <ResourceBean[]>[];
  public toDelete: ResourceBean;
  public display: boolean = false;

  /**
   * internal service
   */
  protected myService: JarvisDefaultResource<ResourceBean>;

  /**
   * constructor
   */
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private navigationGuard: NavigationGuard,
    private jarvisMessageService: JarvisMessageService,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataPluginService: JarvisDataPluginService,
    private _jarvisDataCommandService: JarvisDataCommandService,
    private _jarvisDataTriggerService: JarvisDataTriggerService,
    private _jarvisDataProcessService: JarvisDataProcessService,
    private _jarvisDataConfigurationService: JarvisDataConfigurationService,
    private _jarvisDataPropertyService: JarvisDataPropertyService,
    private _jarvisDataConnectorService: JarvisDataConnectorService,
    private _jarvisDataNotificationService: JarvisDataNotificationService,
    private _jarvisDataCronService: JarvisDataCronService,
    private _jarvisDataSnapshotService: JarvisDataSnapshotService,
    private _jarvisDataViewService: JarvisDataViewService,
    private _jarvisDataMeasureService: JarvisDataMeasureService,
    private _jarvisDataModelService: JarvisDataModelService,
    private _jarvisDataDatasourceService: JarvisDataDatasourceService
  ) {
  }

  ngOnInit() {
      let navigationEnd = {
        url: '/' + this.navigationGuard.getUrl()
      }

      if (navigationEnd.url === '/measures') {
        this.load('measures', this._jarvisDataMeasureService);
      }
      if (navigationEnd.url === '/models') {
        this.load('models', this._jarvisDataModelService);
      }
      if (navigationEnd.url === '/notifications') {
        this.load('notifications', this._jarvisDataNotificationService);
      }
      if (navigationEnd.url === '/datasources') {
        this.load('datasources', this._jarvisDataDatasourceService);
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
      if (navigationEnd.url === '/processes') {
        this.load('processes', this._jarvisDataProcessService);
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
      if (navigationEnd.url === '/snapshots') {
        this.load('snapshots', this._jarvisDataSnapshotService);
      }
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
    this.myService = jarvisDataService;
    jarvisDataService.GetAll()
      .subscribe(
      (data: ResourceBean[]) => this.myResources = data,
      error => {
         console.log(error)
      },
      () => {
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
        this.myResources = [...this.myResources];
        this.jarvisMessageService.push({severity:'info', summary:'Création de la ressource', detail:created.name});
      }
      );
  }

  /**
   * protect dropping
   */
  dropResource(resource: ResourceBean) {
    this.display = true;
    this.toDelete = resource;
  }

  /**
   * delete resource
   */
  public confirmedDropResource(resource: ResourceBean) {
    this.display = false;
    /**
     * delete a single resource
     */
    this.myService.Delete(resource.id)
      .subscribe(
      (data: ResourceBean) => resource = data,
      error => console.log(error),
      () => {
        let that = this;
        _.remove(this.myResources, function(item) {
          return (item.id === resource.id);
        })
        this.myResources = [...this.myResources];
        that.jarvisMessageService.push({severity:'info', summary:'Supression de la ressource', detail:resource.name});
      }
      );
  }

  /**
   * view this resource
   */
  public view(resource: ResourceBean) {
    this.router.navigate([this.myResourceName + '/' + resource.id]);
  }
}
