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
 import { Input, ViewChild, ElementRef } from '@angular/core';
 import { Router, ActivatedRoute, Params } from '@angular/router';
 
/**
 * services
 */
import { LoggerService } from '../../service/logger.service';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataModelService } from '../../service/jarvis-data-model.service';
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * models
 */
import { PickerBean } from '../../model/picker-bean';
import { ResourceBean } from '../../model/resource-bean';
import { ModelBean } from '../../model/code/model-bean';
 
@Component({
  selector: 'app-jarvis-resource-model',
  templateUrl: './jarvis-resource-model.component.html',
  styleUrls: ['./jarvis-resource-model.component.css']
})
export class JarvisResourceModelComponent extends JarvisResource<ModelBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myModel: ModelBean;
  
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _modelService: JarvisDataModelService,
    private logger: LoggerService) {
    super('/models', [], _modelService, _route, _router);
  }

  /**
   * load related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if( picker.action === 'complete') {
      this.myModel = <ModelBean> resource;
    }
  }
}
