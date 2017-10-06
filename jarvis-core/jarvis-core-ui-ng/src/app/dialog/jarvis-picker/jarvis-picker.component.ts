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

import { Component, Input, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { TreeNode, TREE_ACTIONS, KEYS, IActionMapping } from 'angular2-tree-component';
import * as _ from 'lodash';

import { LoggerService } from '../../service/logger.service';
import { JarvisPicker } from '../../class/jarvis-pickers';
import { JarvisDefaultResource } from '../../interface/jarvis-default-resource';
import { NotifyCallback } from '../../class/jarvis-resource';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';
import { JarvisDataDatasourceService } from '../../service/jarvis-data-datasource.service';
import { JarvisDataConnectorService } from '../../service/jarvis-data-connector.service';
import { JarvisDataMeasureService } from '../../service/jarvis-data-measure.service';
import { JarvisDataProcessService } from '../../service/jarvis-data-process.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-picker',
  templateUrl: './jarvis-picker.component.html',
  styleUrls: ['./jarvis-picker.component.css']
})
export class JarvisPickerComponent implements OnInit {

  @Input() resource: PickerBean;

  public picked: any;
  public show: boolean = false;
  public jarvisPickerHelper: JarvisPicker<ResourceBean>;

  private target: NotifyCallback<ResourceBean>;

  /**
   * constructor
   */
  constructor(
    private _deviceService: JarvisDataDeviceService,
    private _triggerService: JarvisDataTriggerService,
    private _pluginService: JarvisDataPluginService,
    private _commandService: JarvisDataCommandService,
    private _processService: JarvisDataProcessService,
    private _cronService: JarvisDataCronService,
    private _datasourceService: JarvisDataDatasourceService,
    private _connectorService: JarvisDataConnectorService,
    private _measureService: JarvisDataMeasureService,
    private _notificationService: JarvisDataNotificationService,
    private logger: LoggerService
  ) {
  }

  /**
   * init this component
   */
  ngOnInit() {
    if(this.resource.action === undefined) {
      this.resource.action = this.resource.service;
    }
    let service: JarvisDefaultResource<ResourceBean>;
    if (this.resource.service === 'measures') {
      service = this._measureService;
    }
    if (this.resource.service === 'connectors') {
      service = this._connectorService;
    }
    if (this.resource.service === 'processes') {
      service = this._processService;
    }
    if (this.resource.service === 'crons') {
      service = this._cronService;
    }
    if (this.resource.service === 'devices') {
      service = this._deviceService;
    }
    if (this.resource.service === 'triggers') {
      service = this._triggerService;
    }
    if (this.resource.service === 'plugins') {
      service = this._pluginService;
    }
    if (this.resource.service === 'notifications') {
      service = this._notificationService;
    }
    if (this.resource.service === 'commands') {
      service = this._commandService;
    }
    if (this.resource.service === 'datasources') {
      service = this._datasourceService;
    }
    /**
     * create helper
     */
    this.jarvisPickerHelper = new JarvisPicker<ResourceBean>(service, this.logger, this.resource);
  }

  /**
   * open this dialog box
   */
  public open(that: NotifyCallback<ResourceBean>, objectType: string) {
    this.jarvisPickerHelper.loadResource(12, objectType);
    this.show = true;
    this.target = that;
  }

  /**
   * validate and close
   */
  public validate(picked: any) {
    if (picked) {
      this.target.notify(this.resource, picked);
    }
    this.show = false;
  }
}
