import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MdDialogRef, MdDialog } from '@angular/material/dialog';

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
import { CompleteCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
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
export class JarvisResourceDeviceComponent extends JarvisResource<DeviceBean> implements OnInit {

  dialogRef: MdDialogRef<JarvisPickerComponent>;
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
    private _pluginService: JarvisDataPluginService,
    private dialog: MdDialog) {
    super(2, '/devices', ['render'], _deviceService, _route, _router);
    this.jarvisDeviceLink = new JarvisResourceLink<DeviceBean>();
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>();
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>();
  }

  /**
   * complete resource
   */
  public complete(that: JarvisDataDeviceService, resource: DeviceBean): void {
    resource.devices = [];
    (new JarvisResourceLink<DeviceBean>()).loadLinks(resource.id, resource.devices, that.allLinkedDevice);
    resource.triggers = [];
    (new JarvisResourceLink<TriggerBean>()).loadLinks(resource.id, resource.triggers, that.allLinkedTrigger);
    resource.plugins = [];
    (new JarvisResourceLink<PluginBean>()).loadLinks(resource.id, resource.plugins, that.allLinkedPlugin);
  }

  openDialog(action: string) {
    this.dialogRef = this.dialog.open(JarvisPickerComponent, {
      disableClose: false
    });

    if (action === 'devices') {
      this.dialogRef.componentInstance.loadResource<DeviceBean>('devices', 12, this._deviceService);
    }
    if (action === 'triggers') {
      this.dialogRef.componentInstance.loadResource<TriggerBean>('triggers', 12, this._triggerService);
    }
    if (action === 'plugins') {
      this.dialogRef.componentInstance.loadResource<PluginBean>('plugins', 12, this._pluginService);
    }

    this.dialogRef.afterClosed().subscribe(result => {
      this.dialogRef = null;
      if (result === null) {
        return;
      }

      /**
       * handle devices
       */
      if (action === 'devices') {
        this.jarvisDeviceLink.addLink(this.getResource().id, result.id, this.getResource().devices, {"order": "1", href: "HREF"}, this._deviceService.allLinkedDevice);
      }

      /**
       * handle triggers
       */
      if (action === 'triggers') {
        this.jarvisTriggerLink.addLink(this.getResource().id, result.id, this.getResource().triggers, {"order": "1", href: "HREF"}, this._deviceService.allLinkedTrigger);
      }

      /**
       * handle plugins
       */
      if (action === 'plugins') {
        this.jarvisPluginLink.addLink(this.getResource().id, result.id, this.getResource().plugins, {"order": "1", href: "HREF"}, this._deviceService.allLinkedPlugin);
      }
    });
  }

  /**
   * task action
   */
  public pick(picker: PickerBean): void {
    this.openDialog(picker.action);
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this.complete);
  }
}
