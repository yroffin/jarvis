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

import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Message } from 'primeng/primeng';
import * as _ from 'lodash';

import { LoggerService } from '../../service/logger.service';
import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisMessageService } from '../../service/jarvis-message.service';
import { JarvisDataMeasureService } from '../../service/jarvis-data-measure.service';
import { JarvisDataConnectorService } from '../../service/jarvis-data-connector.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { MeasureBean } from '../../model/connector/measure-bean';
import { ConnectorBean } from '../../model/connector/connector-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-measure',
  templateUrl: './jarvis-measure.component.html',
  styleUrls: ['./jarvis-measure.component.css']
})
export class JarvisMeasureComponent extends JarvisResource<MeasureBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myMeasure: MeasureBean;
  @ViewChild('pickConnectors') pickConnectors: JarvisPickerComponent;

  private checkData: any = {};

  private checkName: string = "";
  private checkDatetime: string = "";
  private checkValue: string = "";
  
  /**
   * internal
   */
  private jarvisConnectorLink: JarvisResourceLink<ConnectorBean>;

  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _measureService: JarvisDataMeasureService,
    private _connectorService: JarvisDataConnectorService,
    private logger: LoggerService,    
    private jarvisMessageService: JarvisMessageService
  ) {
    super('/measures', [], _measureService, _route, _router);
    this.jarvisConnectorLink = new JarvisResourceLink<ConnectorBean>(this.logger);
  }

  /**
   * load resource and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * change action
   */
  public check(): void {
    /**
     * assert that measure data are correct
     */
    if(this.myMeasure.connectors.length > 1) {
      this.jarvisMessageService.push({severity:'warn', summary:'Trop de connecteur', detail:'Une mesure ne doit avoir qu\'un seul connecteur'});
    } else {
      this.jarvisMessageService.push({severity:'info', summary:'Contrôle', detail:'Contrôle du connecteur ' + this.myMeasure.connectors[0].id});
      let loaded;
      this._connectorService.GetSingle(this.myMeasure.connectors[0].id).subscribe(
        (data) => {
          loaded = data;
        },
        (error) => {
        },
        () => {
          if(loaded.collects.collections.length == 1) {
            // one collect ... but now check the field
            _.each(loaded.collects.collections, (collect: any) => {
              this.checkData = collect;
              this.checkName = collect.name;
              this.checkDatetime = collect.entity[this.myMeasure.datetime];
              this.checkValue = collect.entity[this.myMeasure.value];
            });
          } else {
            this.jarvisMessageService.push({severity:'warn', summary:'Contrôle', detail:'Pas de collecte active ' + loaded.collects.collections.length});
          }
        }
      )
    }
  }

  /**
   * drop link
   */
  public dropConnectorLink(linked: ConnectorBean): void {
    this.jarvisConnectorLink.dropLink(linked, this.myMeasure.id, this.myMeasure.connectors, this._measureService.allLinkedConnectors);
  }

  /**
   * goto link
   */
  public gotoConnectorLink(linked: MeasureBean): void {
    this._router.navigate(['/connectors/' + linked.id]);
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if( picker.action === 'connectors') {
      this.jarvisConnectorLink.addLink(this.getResource().id, resource.id, this.getResource().connectors, {"order": "1", href: "HREF"}, this._measureService.allLinkedConnectors);
    }
    if(picker.action === 'complete') {
      this.myMeasure = <MeasureBean> resource;
      this.myMeasure.connectors = [];
      (new JarvisResourceLink<ConnectorBean>(this.logger)).loadLinksWithCallback(resource.id, this.myMeasure.connectors, this._measureService.allLinkedConnectors, (connectors) => {
        this.check();
      });
    }
  }

  /**
   * pick datasources
   */
  public pick(picker: PickerBean): void {
    /**
     * find measures
     */
    if (picker.action === 'connectors') {
      this.pickConnectors.open(this, 'Connector');
    }
  }
}
