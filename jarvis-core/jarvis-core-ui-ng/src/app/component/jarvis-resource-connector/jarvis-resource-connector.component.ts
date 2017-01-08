import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import * as _ from 'lodash';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

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
import { PickerBean } from '../../model/picker-bean';
import { ConnectorBean } from '../../model/connector-bean';

@Component({
  selector: 'app-jarvis-resource-connector',
  templateUrl: './jarvis-resource-connector.component.html',
  styleUrls: ['./jarvis-resource-connector.component.css']
})
export class JarvisResourceConnectorComponent extends JarvisResource<ConnectorBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myConnector: ConnectorBean;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _connectorService: JarvisDataConnectorService) {
    super('/connectors', [], _connectorService, _route, _router);
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
    if(picker.action === 'complete') {
      this.myConnector = <ConnectorBean> resource;
      _.each(this.myConnector.collects.collections, function(item) {
        item.keys = Object.keys(item.entity);
      });
    }
  }
}
