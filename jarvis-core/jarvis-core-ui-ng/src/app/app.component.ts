import { Component, Input, OnInit, ChangeDetectorRef, ApplicationRef } from '@angular/core';

import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataViewService } from './service/jarvis-data-view.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';
import { MenuItem } from 'primeng/primeng';

/**
 * Cf. https://material2-app.firebaseapp.com/
 * Cf. https://github.com/jelbourn/material2-app
 */

/**
 * data model
 */
import { DeviceBean } from './model/device-bean';
import { ViewBean } from './model/view-bean';

@Component({
  selector: 'app-root',
  providers: [
    JarvisDataDeviceService,
    JarvisDataViewService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private items: MenuItem[];
  private steps: MenuItem[];

  constructor(
    private _changeDetectorRef: ChangeDetectorRef,
    private _applicationRef: ApplicationRef,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataViewService: JarvisDataViewService) {
  }

  ngOnInit() {
    this.steps = [
            {label: 'Step 1'},
            {label: 'Step 2'},
            {label: 'Step 3'}
        ];
      
    this.items = [
      {
        label: 'Home',
        icon: 'fa-home',
        routerLink: ['/']
      },
      {
        label: 'Ressource',
        icon: 'fa-sliders',
        items: [
          { label: 'Device', icon: 'fa-server', routerLink: ['/devices'] }
        ]
      }
    ];
  }
}
