import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataPropertyService } from '../../service/jarvis-data-property.service';

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
import { PropertyBean } from '../../model/property-bean';

@Component({
  selector: 'app-jarvis-resource-property',
  templateUrl: './jarvis-resource-property.component.html',
  styleUrls: ['./jarvis-resource-property.component.css']
})
export class JarvisResourcePropertyComponent extends JarvisResource<PropertyBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myProperty: PropertyBean;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _propertyService: JarvisDataPropertyService) {
    super('/properties', [], _propertyService, _route, _router);
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
      this.myProperty = resource;
    }
  }
}
