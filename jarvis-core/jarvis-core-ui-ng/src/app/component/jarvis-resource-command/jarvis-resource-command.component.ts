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
import { ConfirmationService } from 'primeng/primeng';

declare var Prism: any;

import { LoggerService } from '../../service/logger.service';
import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataNotificationService } from '../../service/jarvis-data-notification.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

/**
 * class
 */
import { JarvisPicker } from '../../class/jarvis-pickers';
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { LinkBean } from '../../model/link-bean';
import { CommandBean } from '../../model/command-bean';
import { NotificationBean } from '../../model/notification-bean';

@Component({
  selector: 'app-jarvis-resource-command',
  templateUrl: './jarvis-resource-command.component.html',
  styleUrls: ['./jarvis-resource-command.component.css']
})
export class JarvisResourceCommandComponent extends JarvisResource<CommandBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myCommand: CommandBean;
  @Input() myJsonData: string = "{}";
  @Input() myRawData: string = "{}";

  @ViewChild('pickNotifications') pickNotifications: JarvisPickerComponent;

  private myData: any = {};
  private myOutputData: any = {};
  private types: SelectItem[];

  /**
   * internal vars
   */
  private jarvisNotificationLink: JarvisResourceLink<NotificationBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _commandService: JarvisDataCommandService,
    private logger: LoggerService,
    private _notificationService: JarvisDataNotificationService
  ) {
    super('/commands', ['execute', 'test', 'clear'], _commandService, _route, _router);
    this.jarvisNotificationLink = new JarvisResourceLink<NotificationBean>(this.logger);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Shell script', value: 'SHELL' });
    this.types.push({ label: 'Command script', value: 'COMMAND' });
    this.types.push({ label: 'Groovy script', value: 'GROOVY' });
    this.types.push({ label: 'Zway command', value: 'ZWAY' });
    this.types.push({ label: 'Chacon command', value: 'CHACON' });
    this.types.push({ label: 'Slack notification', value: 'SLACK' });
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * pretty
   */
  private pretty(val) {
    let body = JSON.stringify(val, null, 2);
    return Prism.highlight(body, Prism.languages.javascript);
  }

  /**
   * task action
   */
  public execute(): void {
    this.myData = JSON.parse(this.myJsonData);
    this.myRawData = JSON.stringify(this.myData);
    this._commandService.Task(this.myCommand.id, 'execute', this.myData)
      .subscribe(
      (result: any) => this.myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * task action
   */
  public render(): void {
    this.myData = JSON.parse(this.myJsonData);
    this.myRawData = JSON.stringify(this.myData);
    this._commandService.Task(this.myCommand.id, 'render', this.myData)
      .subscribe(
      (result: any) => this.myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * pick action
   */
  public pick(picker: PickerBean): void {
    /**
     * find notifications
     */
    if (picker.action === 'notifications') {
      this.pickNotifications.open(this, 'Notification');
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'notifications') {
      this.jarvisNotificationLink.addLink(this.getResource().id, resource.id, this.getResource().notifications, { "order": "1", href: "HREF" }, this._commandService.allLinkedNotification);
    }
    if (picker.action === 'complete') {
      this.myCommand = <CommandBean>resource;
      this.myCommand.notifications = [];
      (new JarvisResourceLink<NotificationBean>(this.logger)).loadLinksWithCallback(resource.id, this.myCommand.notifications, this._commandService.allLinkedNotification, (elements) => {
        this.myCommand.notifications = elements;
      });
    }
  }

  /**
   * drop notification link
   */
  public dropNotificationLink(linked: NotificationBean): void {
    this.jarvisNotificationLink.dropLink(linked, this.myCommand.id, this.myCommand.notifications, this._commandService.allLinkedNotification);
  }

  /**
   * drop notification link
   */
  public updateNotificationLink(linked: NotificationBean): void {
    this.jarvisNotificationLink.updateLink(linked, this.myCommand.id, this._commandService.allLinkedNotification);
  }

  /**
   * highlight source
   */
  public hightlight(body: string): void {
    if(body) {
      return Prism.highlight(body, Prism.languages.javascript);
    }
  }

  /**
   * goto command link
   */
  public gotoNotificationLink(linked: CommandBean): void {
    this._router.navigate(['/notifications/' + linked.id]);
  }
}
