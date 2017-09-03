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

import { MenuItem, Message } from 'primeng/primeng';

import * as _ from 'lodash';

import { NotifyCallback } from '../../class/jarvis-resource';
import { JarvisToolbarAction } from '../../interface/jarvis-toolbar-action';
import { JarvisMessageService } from '../../service/jarvis-message.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { TaskBean, PickerTaskBean } from '../../model/action-bean';

@Component({
  selector: 'app-jarvis-toolbar-resource',
  templateUrl: './jarvis-toolbar-resource.component.html',
  styleUrls: ['./jarvis-toolbar-resource.component.css']
})
export class JarvisToolbarResourceComponent implements OnInit {

  /**
   * members
   */
  @Input() public tasks: TaskBean[] = [];
  @Input() public pickers: PickerTaskBean[] = [];

  @Input() private actions: any[];
  @Input() private notified: JarvisToolbarAction;
  @Input() public crud: boolean = false;

  public display: boolean = false;
  public toDelete: ResourceBean;

  private items: MenuItem[] = [];

  constructor(
    private jarvisMessageService: JarvisMessageService
  ) {
  }

  /**
   * protect dropping
   */
  dropResource() {
    this.display = true;
  }

  /**
   * confirmation
   */
  confirmedDropResource() {
    this.display = false;
    this.remove();
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
   * callback behaviour
   * little tricky code, may be to refactor
   */
  private taskCallback(task: TaskBean): void {
    eval("this.notified."+task.task).apply(this.notified, task.args);
    this.jarvisMessageService.push({severity:'info', summary:'Tâche', detail:task.label});
  }

  /**
   * callback behaviour
   * little tricky code, may be to refactor
   */
  private pickerCallback(picker: PickerTaskBean): void {
    let data: PickerBean = new PickerBean();
    data.action = picker.action;
    this.pick(picker.action);
  }

  /**
   * handle pick behaviour
   */
  public pick(action: string): void {
    /**
     * find picker for this action
     */
    _.find(this.pickers, function (item) {
      return item.action === action;
    }).picker.open(this.notified, action);
  }

  /**
   * default crud method
   */
  public close(): void {
    this.notified.close();
  }

  /**
   * default crud method
   */
  public save(): void {
    this.notified.save();
    this.jarvisMessageService.push({severity:'info', summary:'Action', detail:"sauvegarde"});
  }

  /**
   * default crud method
   */
  public remove(): void {
    this.notified.remove();
    this.jarvisMessageService.push({severity:'info', summary:'Action', detail:"suppression"});
  }

  /**
   * default crud method
   */
  public duplicate(): void {
    this.notified.duplicate();
    this.jarvisMessageService.push({severity:'info', summary:'Action', detail:"duplication"});
  }
}
