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

import { Component, OnInit, Input, ViewChild } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';

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
import { ConnectorBean } from '../../model/connector/connector-bean';

@Component({
  selector: 'app-jarvis-resource-datasource',
  templateUrl: './jarvis-resource-datasource.component.html',
  styleUrls: ['./jarvis-resource-datasource.component.css']
})
export class JarvisResourceDatasourceComponent extends JarvisResource<DataSourceBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myDataSource: DataSourceBean;
  @ViewChild('pickConnectors') pickConnectors: JarvisPickerComponent;

  /**
   * internal
   */
  private jarvisConnectorLink: JarvisResourceLink<ConnectorBean>;

  /**
   * constructor
   */
  constructor(
    private _http: Http,
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _datasourceService: JarvisDataDatasourceService,
    private _connectorService: JarvisDataConnectorService) {
    super('/datasources', [], _datasourceService, _route, _router);
    this.jarvisConnectorLink = new JarvisResourceLink<ConnectorBean>();
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * drop link
   */
  public dropConnectorLink(linked: ConnectorBean): void {
    this.jarvisConnectorLink.dropLink(linked, this.myDataSource.id, this.myDataSource.connectors, this._datasourceService.allLinkedConnector);
  }

  /**
   * goto link
   */
  public gotoConnectorLink(linked: ConnectorBean): void {
    this._router.navigate(['/connectors/' + linked.id]);
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if( picker.action === 'connectors') {
      this.jarvisConnectorLink.addLink(this.getResource().id, resource.id, this.getResource().connectors, {"order": "1", href: "HREF"}, this._datasourceService.allLinkedConnector);
    }
    if (picker.action === 'complete') {
      this.myDataSource = <DataSourceBean>resource;
      this.myDataSource.connectors = [];
      (new JarvisResourceLink<ConnectorBean>()).loadLinks(resource.id, this.myDataSource.connectors, this._datasourceService.allLinkedConnector);
    }
  }

  /**
   * pick datasources
   */
  public pick(picker: PickerBean): void {
    /**
     * find connectors
     */
    if (picker.action === 'connectors') {
      this.pickConnectors.open(this, 'Connector');
    }
  }
}
