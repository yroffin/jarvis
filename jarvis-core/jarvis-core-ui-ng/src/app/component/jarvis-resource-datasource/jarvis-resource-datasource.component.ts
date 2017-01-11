/* 
 * Copyright 2017 Yannick Roffin.
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

import { Component, OnInit, Input } from '@angular/core';

import * as _ from 'lodash';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataConnectorService } from '../../service/jarvis-data-connector.service';
import { JarvisDataDatasourceService } from '../../service/jarvis-data-datasource.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { DataSourceBean } from '../../model/connector/datasource-bean';
import { ConnectorBean } from '../../model/connector-bean';

@Component({
  selector: 'app-jarvis-resource-datasource',
  templateUrl: './jarvis-resource-datasource.component.html',
  styleUrls: ['./jarvis-resource-datasource.component.css']
})
export class JarvisResourceDatasourceComponent extends JarvisResource<DataSourceBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myDataSource: DataSourceBean;

  /**
   * internal
   */
  private types: SelectItem[];

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _datasourceService: JarvisDataDatasourceService,
    private _connectorService: JarvisDataConnectorService) {
    super('/datasources', [], _datasourceService, _route, _router);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    let connectors: ConnectorBean[];
    let that = this;
    _connectorService.GetAll()
      .subscribe(
      (data: ConnectorBean[]) => connectors = data,
      error => console.log(error),
      () => {
        /**
         * complete resource
         */
        _.each(connectors, function (connector) {
          that.types.push({ label: connector.name, value: connector.adress});
        });
      }
      );
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * task action
   */
  public task(action: string): void {
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'complete') {
      this.myDataSource = <DataSourceBean>resource;
    }
  }
}
