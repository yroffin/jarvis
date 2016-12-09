import { Component, OnInit, Input } from '@angular/core';
import * as _ from 'lodash';

import { JarvisDataDeviceService } from '../../../service/jarvis-data-device.service';
import { JarvisDataTriggerService } from '../../../service/jarvis-data-trigger.service';
import { JarvisResourceLink } from '../../../class/jarvis-resource-link';

/**
 * data model
 */
import { DeviceBean } from '../../../model/device-bean';
import { TriggerBean } from '../../../model/trigger-bean';
import { PluginBean } from '../../../model/plugin-bean';

@Component({
  selector: 'app-jarvis-resource-device-general',
  templateUrl: './jarvis-resource-device-general.component.html',
  styleUrls: ['./jarvis-resource-device-general.component.css']
})
export class JarvisResourceDeviceGeneralComponent implements OnInit {

  @Input() myDevice: DeviceBean;
  private jarvisDeviceLink: JarvisResourceLink<DeviceBean>;
  private jarvisTriggerLink: JarvisResourceLink<TriggerBean>;
  private jarvisPluginLink: JarvisResourceLink<PluginBean>;

  /**
   * default constructor
   */
  constructor(
    private _deviceService: JarvisDataDeviceService
  ) {
    this.jarvisDeviceLink = new JarvisResourceLink<DeviceBean>();
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>();
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>();
  }

  ngOnInit() {
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
}
