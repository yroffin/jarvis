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

import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { CronBean } from '../../model/cron-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-resource-cron',
  templateUrl: './jarvis-resource-cron.component.html',
  styleUrls: ['./jarvis-resource-cron.component.css']
})
export class JarvisResourceCronComponent extends JarvisResource<CronBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myCron: CronBean;
  private types: SelectItem[];

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _cronService: JarvisDataCronService) {
    super('/crons', ['toggle', 'test'], _cronService, _route, _router);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Couché du soleil', value: 'SUNSET' });
    this.types.push({ label: 'Levée du soleil', value: 'SUNRISE' });
    this.types.push({ label: 'Crontab', value: 'CRONTAB' });
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
  public toggle(): void {
    /**
     * execute this plugin
     */
    let myOutputData;
    this._cronService.Task(this.myCron.id, 'toggle', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
    return;
  }

  /**
   * task action
   */
  public test(): void {
    let myOutputData;
    this._cronService.Task(this.myCron.id, 'test', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
    return;
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'complete') {
      this.myCron = resource;
    }
  }
}
