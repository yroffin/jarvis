import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

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
import { PluginBean } from '../../model/plugin-bean';
import { PickerBean } from '../../model/picker-bean';
import { LinkBean } from '../../model/link-bean';

@Component({
  selector: 'app-jarvis-resource-device',
  templateUrl: './jarvis-resource-device.component.html',
  styleUrls: ['./jarvis-resource-device.component.css']
})
export class JarvisResourceDeviceComponent extends JarvisResource<DeviceBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myDevice: DeviceBean;

  @ViewChild('pickDevices') pickDevices: JarvisPickerComponent;
  @ViewChild('pickTriggers') pickTriggers: JarvisPickerComponent;
  @ViewChild('pickPlugins') pickPlugins: JarvisPickerComponent;

  private jarvisDeviceLink: JarvisResourceLink<DeviceBean>;
  private jarvisTriggerLink: JarvisResourceLink<TriggerBean>;
  private jarvisPluginLink: JarvisResourceLink<PluginBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _deviceService: JarvisDataDeviceService,
    private _triggerService: JarvisDataTriggerService,
    private _pluginService: JarvisDataPluginService) {
    super('/devices', ['render'], _deviceService, _route, _router);
    this.jarvisDeviceLink = new JarvisResourceLink<DeviceBean>();
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>();
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>();
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * pick action
   */
  public pick(picker: PickerBean): void {
    /**
     * find notifications
     */
    if (picker.action === 'devices') {
      this.pickDevices.open(this);
    }
    /**
     * find notifications
     */
    if (picker.action === 'triggers') {
      this.pickTriggers.open(this);
    }
    /**
     * find notifications
     */
    if (picker.action === 'plugins') {
      this.pickPlugins.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(action: string, resource: ResourceBean): void {
    if (action === 'devices') {
      this.jarvisDeviceLink.addLink(this.getResource().id, resource.id, this.getResource().devices, { "order": "1", href: "HREF" }, this._deviceService.allLinkedDevice);
    }
    if (action === 'triggers') {
      this.jarvisTriggerLink.addLink(this.getResource().id, resource.id, this.getResource().triggers, { "order": "1", href: "HREF" }, this._deviceService.allLinkedTrigger);
    }
    if (action === 'plugins') {
      this.jarvisPluginLink.addLink(this.getResource().id, resource.id, this.getResource().plugins, { "order": "1", href: "HREF" }, this._deviceService.allLinkedPlugin);
    }
  }

  /**
   * complete resource
   */
  public complete(that: any, resource: DeviceBean): void {
    that.myDevice = resource;
    resource.devices = [];
    (new JarvisResourceLink<DeviceBean>()).loadLinks(resource.id, resource.devices, that._deviceService.allLinkedDevice);
    resource.triggers = [];
    (new JarvisResourceLink<TriggerBean>()).loadLinks(resource.id, resource.triggers, that._deviceService.allLinkedTrigger);
    resource.plugins = [];
    (new JarvisResourceLink<PluginBean>()).loadLinks(resource.id, resource.plugins, that._deviceService.allLinkedPlugin);
  }

  /**
   * drop device link
   */
  public dropDeviceLink(linked: DeviceBean): void {
    this.jarvisDeviceLink.dropLink(linked, this.myDevice.id, this.myDevice.devices, this._deviceService.allLinkedDevice);
  }

  /**
   * drop trigger link
   */
  public dropTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.dropLink(linked, this.myDevice.id, this.myDevice.triggers, this._deviceService.allLinkedTrigger);
  }

  /**
   * drop plugin link
   */
  public dropPluginLink(linked: PluginBean): void {
    this.jarvisPluginLink.dropLink(linked, this.myDevice.id, this.myDevice.plugins, this._deviceService.allLinkedPlugin);
  }

  /**
   * drop plugin link
   */
  public updatePluginLink(linked: PluginBean): void {
    this.jarvisPluginLink.updateLink(linked, this.myDevice.id, this._deviceService.allLinkedPlugin);
  }

  /**
   * goto plugin link
   */
  public gotoPluginLink(linked: PluginBean): void {
    this._router.navigate(['/plugins/' + linked.id]);
  }
}
