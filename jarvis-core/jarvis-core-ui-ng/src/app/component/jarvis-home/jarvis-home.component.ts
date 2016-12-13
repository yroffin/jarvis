import { Component, Input, OnInit } from '@angular/core';

import { Message } from 'primeng/primeng';

import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataViewService } from '../../service/jarvis-data-view.service';
import { JarvisDataStoreService } from '../../service/jarvis-data-store.service';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';
import { ViewBean } from '../../model/view-bean';

@Component({
  selector: 'app-jarvis-home',
  templateUrl: './jarvis-home.component.html',
  styleUrls: ['./jarvis-home.component.css']
})
export class JarvisHomeComponent implements OnInit {

  msgs: Message[] = [];
  myViews: ViewBean[];

  constructor(
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataViewService: JarvisDataViewService) {
  }

  ngOnInit() {
    /**
     * load all views
     */
    this._jarvisDataViewService.FindViewsAndDevices()
      .subscribe(
      (data: ViewBean[]) => this.myViews = this._jarvisDataStoreService.getViews()
      );
  }

  private touch(device: DeviceBean): void {
    this._jarvisDataDeviceService.Task(device.id, "execute", {})
      .subscribe(
      (data: any) => this.msgs.push({severity:'info', summary:'Activation', detail:device.name})
      );
  }
}
