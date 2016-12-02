import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MdInput } from '@angular2-material/input';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';

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
export class JarvisResourceDeviceComponent extends JarvisResource implements OnInit {

  myDevice: DeviceBean;

  /**
   * constructor
   */
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService) {
      super(2);
    }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.route.params
      .map(params => params['id'])
      .subscribe((id) => {
        this._jarvisDataDeviceService.GetSingle(id)
          .subscribe(
            (data: DeviceBean) => this.myDevice = data,
            error => console.log(error),
            () => {
              this._jarvisDataDeviceService.GetAllLinkedTrigger(id)
                .subscribe(
                  (data: TriggerBean[]) => this.myDevice.triggers = data,
                  error => console.log(error),
                  () => {
                  });
              this._jarvisDataDeviceService.GetAllLinkedDevice(id)
                .subscribe(
                  (data: DeviceBean[]) => this.myDevice.devices = data,
                  error => console.log(error),
                  () => {
                  });
              this._jarvisDataDeviceService.GetAllLinkedPluginScript(id)
                .subscribe(
                  (data: ScriptBean[]) => this.myDevice.plugins = data,
                  error => console.log(error),
                  () => {
                  });
            }
          );
      });
  }

  public close(): void {
    this.router.navigate(['/devices']);
  }

  public save(): void {
    this._jarvisDataDeviceService.Update(this.myDevice.id, this.myDevice)
      .subscribe(
        (data: DeviceBean) => data,
        error => console.log(error),
        () => {
        });
  }

  public remove(): void {
    this._jarvisDataDeviceService.Delete(this.myDevice.id)
      .subscribe(
        (data: DeviceBean) => data,
        error => console.log(error),
        () => {
          this.close();
        });
  }

  public duplicate(): void {
    this._jarvisDataDeviceService.Add(this.myDevice)
      .subscribe(
        (data: DeviceBean) => data,
        error => console.log(error),
        () => {
        });
  }
}
