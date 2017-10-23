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

import { SecurityContext, Sanitizer } from '@angular/core'
import { DomSanitizer } from '@angular/platform-browser';

import { LoggerService } from '../../service/logger.service';
import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
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
import { TriggerBean } from '../../model/trigger-bean';
import { PluginBean, PluginScriptBean } from '../../model/plugin-bean';
import { PickerBean } from '../../model/picker-bean';
import { LinkBean } from '../../model/link-bean';

@Component({
  selector: 'app-jarvis-resource-device',
  templateUrl: './jarvis-resource-device.component.html',
  styleUrls: ['./jarvis-resource-device.component.css']
})
export class JarvisResourceDeviceComponent extends JarvisResource<DeviceBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myDevice: DeviceBean;
  public plugins: PluginScriptBean[];

  @ViewChild('pickDevices') pickDevices: JarvisPickerComponent;
  @ViewChild('pickTriggers') pickTriggers: JarvisPickerComponent;
  @ViewChild('pickPlugins') pickPlugins: JarvisPickerComponent;

  private jarvisDeviceLink: JarvisResourceLink<DeviceBean>;
  private jarvisTriggerLink: JarvisResourceLink<TriggerBean>;
  private jarvisPluginLink: JarvisResourceLink<PluginBean>;

  private myData: any = {};
  private myDetail: string = "";

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private sanitizer:DomSanitizer,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _deviceService: JarvisDataDeviceService,
    private _triggerService: JarvisDataTriggerService,
    private _pluginService: JarvisDataPluginService,
    private logger: LoggerService
  ) {
    super('/devices', ['render'], _deviceService, _route, _router);
    this.jarvisDeviceLink = new JarvisResourceLink<DeviceBean>(this.logger);
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>(this.logger);
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>(this.logger);
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
   * task action
   */
  public render(): void {
    let output: any;
    this._deviceService.Task(this.myDevice.id, 'render', {})
      .subscribe(
      (result: any) => output = result,
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
     * find devices
     */
    if (picker.action === 'devices') {
      this.pickDevices.open(this, 'Device');
    }
    /**
     * find triggers
     */
    if (picker.action === 'triggers') {
      this.pickTriggers.open(this, 'Trigger');
    }
    /**
     * find plugins
     */
    if (picker.action === 'plugins') {
      this.pickPlugins.open(this, 'Plugin');
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'devices') {
      this.jarvisDeviceLink.addLink(this.getResource().id, resource.id, this.getResource().devices, { "order": "1", href: "HREF" }, this._deviceService.allLinkedDevice);
    }
    if (picker.action === 'triggers') {
      this.jarvisTriggerLink.addLink(this.getResource().id, resource.id, this.getResource().triggers, { "order": "1", href: "HREF" }, this._deviceService.allLinkedTrigger);
    }
    if (picker.action === 'plugins') {
      this.jarvisPluginLink.addLink(this.getResource().id, resource.id, this.getResource().plugins, { "order": "1", href: "HREF" }, this._deviceService.allLinkedPlugin);
    }
    if (picker.action === 'complete') {
      this.myDevice = <DeviceBean>resource;
      this.myDevice.devices = [];
      (new JarvisResourceLink<DeviceBean>(this.logger)).loadLinksWithCallback(resource.id, this.myDevice.devices, this._deviceService.allLinkedDevice, (elements) => {
        this.myDevice.devices = elements;
      });
      this.myDevice.triggers = [];
      (new JarvisResourceLink<TriggerBean>(this.logger)).loadLinksWithCallback(resource.id, this.myDevice.triggers, this._deviceService.allLinkedTrigger, (elements) => {
        this.myDevice.triggers = elements;
      });
      this.myDevice.plugins = [];
      (new JarvisResourceLink<PluginBean>(this.logger)).loadLinksWithCallback(resource.id, this.myDevice.plugins, this._deviceService.allLinkedPlugin, (elements) => {
        this.myDevice.plugins = elements;
        this._deviceService.TaskAsXml(this.myDevice.id, 'uml', this.myData)
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
   * drop link
   */
  public dropDeviceLink(linked: DeviceBean): void {
    this.jarvisDeviceLink.dropLink(linked, this.myDevice.id, this.myDevice.devices, this._deviceService.allLinkedDevice);
  }

  /**
   * update link
   */
  public updateDeviceLink(linked: DeviceBean): void {
    this.jarvisDeviceLink.updateLink(linked, this.myDevice.id, this._deviceService.allLinkedDevice);
  }

  /**
   * goto link
   */
  public gotoDeviceLink(linked: DeviceBean): void {
    this._router.navigate(['/devices/' + linked.id]);
  }

  /**
   * drop link
   */
  public dropTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.dropLink(linked, this.myDevice.id, this.myDevice.triggers, this._deviceService.allLinkedTrigger);
  }

  /**
   * update link
   */
  public updateTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.updateLink(linked, this.myDevice.id, this._deviceService.allLinkedTrigger);
  }

  /**
   * goto link
   */
  public gotoTriggerLink(linked: TriggerBean): void {
    this._router.navigate(['/triggers/' + linked.id]);
  }

  /**
   * drop link
   */
  public dropPluginLink(linked: PluginBean): void {
    this.jarvisPluginLink.dropLink(linked, this.myDevice.id, this.myDevice.plugins, this._deviceService.allLinkedPlugin);
  }

  /**
   * update link
   */
  public updatePluginLink(linked: PluginBean): void {
    this.jarvisPluginLink.updateLink(linked, this.myDevice.id, this._deviceService.allLinkedPlugin);
  }

  /**
   * goto link
   */
  public gotoPluginLink(linked: PluginBean): void {
    this._router.navigate(['/plugins/' + linked.id]);
  }
}
