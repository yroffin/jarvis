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

import { JarvisPicker } from '../../class/jarvis-pickers';
import { JarvisDefaultResource } from '../../interface/jarvis-default-resource';
import { CompleteCallback, NotifyCallback } from '../../class/jarvis-resource';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerDialogBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-picker',
  templateUrl: './jarvis-picker.component.html',
  styleUrls: ['./jarvis-picker.component.css']
})
export class JarvisPickerComponent implements OnInit {

  @Input() resource: PickerDialogBean;
  @ViewChild('tree') treeNode;

  private picked: any;
  private show: boolean = false;
  private jarvisPickerHelper: JarvisPicker<ResourceBean>;

  private target: NotifyCallback<ResourceBean>;

  /**
   * constructor
   */
  constructor(
    private _notificationService: JarvisDataNotificationService
  ) {
  }

  /**
   * init this component
   */
  ngOnInit() {
    let service: JarvisDefaultResource<ResourceBean>;
    if (this.resource.service === 'notifications') {
      service = this._notificationService;
    }
    /**
     * create helper
     */
    this.jarvisPickerHelper = new JarvisPicker<ResourceBean>(service, this.resource);
  }

  /**
   * open this dialog box
   */
  public open(that: NotifyCallback<ResourceBean>) {
    this.jarvisPickerHelper.loadResource(12);
    this.show = true;
    this.target = that;
  }

  /**
   * validate and close
   */
  public validate(add: boolean) {
    if (add) {
      this.target.notify(this.resource.service, this.picked);
    }
    this.show = false;
  }

  /**
   * handler event
   */
  public onEvent(event: any) {
    if (event.eventName === 'onFocus' && event.node.data.resourceData) {
      if (this.picked != event.node.data.resourceData) {
        /**
         * store picked element
         */
        this.picked = event.node.data.resourceData;
      }
    }
  }
}
