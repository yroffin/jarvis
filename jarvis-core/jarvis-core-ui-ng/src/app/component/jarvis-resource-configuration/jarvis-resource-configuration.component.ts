import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataConfigurationService } from '../../service/jarvis-data-configuration.service';

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
import { ConfigurationBean } from '../../model/configuration-bean';

@Component({
  selector: 'app-jarvis-resource-configuration',
  templateUrl: './jarvis-resource-configuration.component.html',
  styleUrls: ['./jarvis-resource-configuration.component.css']
})
export class JarvisResourceConfigurationComponent extends JarvisResource<ConfigurationBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myConfiguration: ConfigurationBean;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _configurationService: JarvisDataConfigurationService) {
    super('/configurations', [], _configurationService, _route, _router);
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
      this.myConfiguration = resource;
    }
  }
}
