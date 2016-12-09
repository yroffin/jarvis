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

  constructor(
    private _changeDetectorRef: ChangeDetectorRef,
    private _applicationRef: ApplicationRef,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataViewService: JarvisDataViewService) {
  }

  ngOnInit() {
    this.items = [
      {
        label: 'Ressource',
        icon: 'fa-file-o',
        items: [
          { label: 'Device', icon: 'fa-plus', routerLink: ['/devices'] },
          { label: 'Open' },
          { label: 'Quit' }
        ]
      },
      {
        label: 'Edit',
        icon: 'fa-edit',
        items: [
          { label: 'Undo', icon: 'fa-mail-forward' },
          { label: 'Redo', icon: 'fa-mail-reply' }
        ]
      },
      {
        label: 'Help',
        icon: 'fa-question',
        items: [
          {
            label: 'Contents'
          },
          {
            label: 'Search',
            icon: 'fa-search',
            items: [
              {
                label: 'Text',
                items: [
                  {
                    label: 'Workspace'
                  }
                ]
              },
              {
                label: 'File'
              }
            ]
          }
        ]
      }
    ];
  }
}
