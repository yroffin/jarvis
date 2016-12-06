import { Component, OnInit, Input } from '@angular/core';
import * as _ from 'lodash';

import { JarvisDataDeviceService } from '../../../service/jarvis-data-device.service';
import { JarvisDataTriggerService } from '../../../service/jarvis-data-trigger.service';
import { JarvisResourceLink } from '../../../class/jarvis-resource-link';

/**
 * data model
 */
import { DeviceBean } from '../../../model/device-bean';
import { PluginBean } from '../../../model/plugin-bean';

@Component({
  selector: 'app-jarvis-resource-device-plugin',
  templateUrl: './jarvis-resource-device-plugin.component.html',
  styleUrls: ['./jarvis-resource-device-plugin.component.css']
})
export class JarvisResourceDevicePluginComponent implements OnInit {

  @Input() myDevice: DeviceBean;
  private jarvisPluginLink: JarvisResourceLink<PluginBean>;

  constructor(
    private _deviceService: JarvisDataDeviceService
  ) {
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>();
  }

  ngOnInit() {
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
