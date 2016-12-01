import { Component, Input, HostListener, OnInit, ChangeDetectorRef } from '@angular/core';
import * as _ from 'lodash';

import { JarvisGrid } from '../../class/jarvis-grid';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';
import { ViewBean } from '../../model/view-bean';

@Component({
  selector: 'app-jarvis-tiles-view',
  templateUrl: './jarvis-tiles-view.component.html',
  styleUrls: ['./jarvis-tiles-view.component.css']
})
export class JarvisTilesViewComponent extends JarvisGrid implements OnInit {

  @Input() myView: ViewBean;
  @Input() myDevices: DeviceBean[];

  basicRowHeight = 80;
  ratioGutter = 1;
  fitListHeight = '128px';
  ratio = '4:2';

  /**
   * listen on resize event
   */
  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.responsive(event.target.innerWidth);
  }

  constructor(
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService
  ) {
    super(_jarvisConfigurationService);
  }

  ngOnInit() {
  }

  /**
   * execute action on this device
   */
  public execute(device: DeviceBean): void {
    this._jarvisDataDeviceService.Task(device.id, 'execute')
      .subscribe(
      (data: any) => data,
      error => console.log(error),
      () => { }
      );
  }
}
