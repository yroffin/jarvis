import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { CompleteCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';
import { TriggerBean } from '../../model/trigger-bean';
import { ScriptBean } from '../../model/script-bean';

@Component({
  selector: 'app-jarvis-resource-device',
  templateUrl: './jarvis-resource-device.component.html',
  styleUrls: ['./jarvis-resource-device.component.css']
})
export class JarvisResourceDeviceComponent extends JarvisResource<DeviceBean> implements OnInit {

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _resourceService: JarvisDataDeviceService) {
    super(2, '/devices', ['render'], _resourceService, _route, _router);
  }

  /**
   * complete resource
   */
  public complete(that: JarvisDataDeviceService, resource: DeviceBean): void {
    that.GetAllLinkedTrigger(resource.id)
      .subscribe(
      (data: TriggerBean[]) => resource.triggers = data,
      error => console.log(error),
      () => {
      });
    that.GetAllLinkedDevice(resource.id)
      .subscribe(
      (data: DeviceBean[]) => resource.devices = data,
      error => console.log(error),
      () => {
      });
    that.GetAllLinkedPluginScript(resource.id)
      .subscribe(
      (data: ScriptBean[]) => resource.plugins = data,
      error => console.log(error),
      () => {
      });
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this.complete);
  }
}
