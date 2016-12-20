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

import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/primeng';
import * as _ from 'lodash';

import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-toolbar-resource',
  templateUrl: './jarvis-toolbar-resource.component.html',
  styleUrls: ['./jarvis-toolbar-resource.component.css']
})
export class JarvisToolbarResourceComponent implements OnInit {

  @Input() private actions: any[];
  @Input() private notified: any;

  private items: MenuItem[];

  constructor() {

  }

  ngOnInit() {
    /**
     * configure action bar
     */
    let that = this;
    this.items = [];
    _.each(this.actions, function (item) {
      that.items.push(
        {
          label: item.label,
          icon: item.icon,
          command: () => {
            let picker: PickerBean = new PickerBean();
            picker.action = item.action;
            that.pick(item.action);
          }
        }
      )
    });
  }

  /**
   * handle pick behaviour
   */
  public pick(action: string): void {
    /**
     * find picker for this action
     */
    _.find(this.actions, function (item) {
      return item.action === action;
    }).picker.open(this.notified);
  }

}
