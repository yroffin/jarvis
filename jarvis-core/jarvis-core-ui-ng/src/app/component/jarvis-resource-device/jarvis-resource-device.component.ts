import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MdDialogRef, MdDialog } from '@angular/material/dialog';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';

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
  }

  /**
   * complete resource
   */
  public complete(that: JarvisDataDeviceService, resource: DeviceBean): void {
    that.allLinkedTrigger.GetAll(resource.id)
      .subscribe(
      (data: TriggerBean[]) => resource.triggers = data,
      error => console.log(error),
      () => {
      });
    that.allLinkedDevice.GetAll(resource.id)
      .subscribe(
      (data: DeviceBean[]) => resource.devices = data,
      error => console.log(error),
      () => {
      });
    that.allLinkedPlugin.GetAll(resource.id)
      .subscribe(
      (data: PluginBean[]) => resource.plugins = data,
      error => console.log(error),
      () => {
      });
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
        let device: DeviceBean;
        let that = this;
        this._deviceService.allLinkedDevice.Add(this.getResource().id, result.id, {"order": "1", href: "HREF"})
          .subscribe(
          (data: DeviceBean) => device = data,
          error => console.log(error),
          () => {
            /**
             * add this device to current view
             */
            that.getResource().devices.push(device);
          });
      }

      /**
       * handle triggers
       */
      if (action === 'triggers') {
        let trigger: TriggerBean;
        let that = this;
        this._deviceService.allLinkedTrigger.Add(this.getResource().id, result.id, {"order": "1", href: "HREF"})
          .subscribe(
          (data: TriggerBean) => trigger = data,
          error => console.log(error),
          () => {
            /**
             * add this device to current view
             */
            that.getResource().triggers.push(trigger);
          });
      }

      /**
       * handle plugins
       */
      if (action === 'plugins') {
        let plugin: PluginBean;
        let that = this;
        this._deviceService.allLinkedPlugin.Add(this.getResource().id, result.id, {"order": "1", href: "HREF"})
          .subscribe(
          (data: PluginBean) => plugin = data,
          error => console.log(error),
          () => {
            /**
             * add this device to current view
             */
            that.getResource().plugins.push(plugin);
          });
      }
    });
  }

  /**
   * task action
   */
  public pick(picker: PickerBean): void {
    console.warn('picker', picker);
    this.openDialog(picker.action);
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this.complete);
  }
}

@Component({
  selector: 'pizza-dialog',
  template: `
  <button type="button" (click)="dialogRef.close('yes')">Yes</button>
  <button type="button" (click)="dialogRef.close('no')">No</button>
  `
})
export class PizzaDialog {
  constructor(public dialogRef: MdDialogRef<PizzaDialog>) { }
}