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

import { SecurityContext, Sanitizer } from '@angular/core'
import { DomSanitizer } from '@angular/platform-browser';

declare var Prism: any;

import { LoggerService } from '../../service/logger.service';
import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { DeviceBean } from '../../model/device-bean';
import { PluginBean } from '../../model/plugin-bean';
import { PickerBean } from '../../model/picker-bean';
import { LinkBean } from '../../model/link-bean';
import { CommandBean } from '../../model/command-bean';

@Component({
  selector: 'app-jarvis-resource-plugin',
  templateUrl: './jarvis-resource-plugin.component.html',
  styleUrls: ['./jarvis-resource-plugin.component.css']
})
export class JarvisResourcePluginComponent extends JarvisResource<PluginBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myPlugin: PluginBean;
  @Input() myJsonData: string = "{}";
  @Input() myRawData: string = "{}";

  private myData: any = {};
  private myOutputData: any = {};
  private myDetail: string = "";
  private types: SelectItem[];

  @ViewChild('pickCommands') pickCommands;

  /**
   * internal vars
   */
  display: boolean = false;
  myCommand: CommandBean;

  private jarvisCommandLink: JarvisResourceLink<CommandBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private sanitizer:DomSanitizer,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _pluginService: JarvisDataPluginService,
    private logger: LoggerService,
    private _commandService: JarvisDataCommandService) {
    super('/plugins', ['execute', 'render', 'clear'], _pluginService, _route, _router);
    this.jarvisCommandLink = new JarvisResourceLink<CommandBean>(this.logger);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Plugin Script', value: 'script' });
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
  private sanitize(html: string): any {
    return this.sanitizer.bypassSecurityTrustHtml(html);
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
  public clear(): void {
  }

  /**
   * task action
   */
  public execute(): void {
    /**
     * execute this plugin
     */
    this.myData = JSON.parse(this.myJsonData);
    this.myRawData = JSON.stringify(this.myData);
    this._pluginService.Task(this.myPlugin.id, 'execute', this.myData)
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
    /**
     * render this plugin
     */
    this.myData = JSON.parse(this.myJsonData);
    this.myRawData = JSON.stringify(this.myData);
    this._pluginService.Task(this.myPlugin.id, 'render', this.myData)
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
    if (picker.action === 'commands') {
      this.pickCommands.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'commands') {
      this.jarvisCommandLink.addLink(this.getResource().id, resource.id, this.getResource().commands, { "order": "1", href: "HREF" }, this._pluginService.allLinkedCommand);
    }
    if (picker.action === 'complete') {
      this.myPlugin = <PluginBean>resource;
      this.myPlugin.commands = [];
      (new JarvisResourceLink<CommandBean>(this.logger)).loadLinksWithCallback(resource.id, this.myPlugin.commands, this._pluginService.allLinkedCommand, (elements) => {
        this.myPlugin.commands = elements;
        this._pluginService.TaskAsXml(this.myPlugin.id, 'uml', this.myData)
          .subscribe(
          (result: any) => this.myDetail = result,
          error => console.log(error),
          () => {
            console.log(this.myDetail);
          }
          );
      });
    }
  }

  /**
   * drop command link
   */
  public dropCommandLink(linked: CommandBean): void {
    this.jarvisCommandLink.dropLink(linked, this.myPlugin.id, this.myPlugin.commands, this._pluginService.allLinkedCommand);
  }

  /**
   * drop command link
   */
  public updateCommandLink(linked: CommandBean): void {
    this.jarvisCommandLink.updateLink(linked, this.myPlugin.id, this._pluginService.allLinkedCommand);
  }

  /**
   * drop command link
   */
  public viewCommandLink(linked: CommandBean): void {
    this.myCommand = linked;
    this.display = true;
  }

  /**
   * highlight source
   */
  public hightlight(body: string): void {
    return Prism.highlight(body, Prism.languages.javascript);
  }

  /**
   * goto command link
   */
  public gotoCommandLink(linked: CommandBean): void {
    this._router.navigate(['/commands/' + linked.id]);
  }
}
