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

import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';

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
import { NotificationBean } from '../../model/notification-bean';

@Component({
  selector: 'app-jarvis-resource-notification',
  templateUrl: './jarvis-resource-notification.component.html',
  styleUrls: ['./jarvis-resource-notification.component.css']
})
export class JarvisResourceNotificationComponent extends JarvisResource<NotificationBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myNotification: NotificationBean;

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
    private _notificationService: JarvisDataNotificationService) {
    super('/notifications', [], _notificationService, _route, _router);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Slack notification', value: 'SLACK' });
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
      this.myNotification = <NotificationBean> resource;
    }
  }
}
