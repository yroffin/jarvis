/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Input, OnInit } from '@angular/core';

import { MatSnackBar } from '@angular/material';
import { Message } from 'primeng/primeng';

import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataViewService } from '../../service/jarvis-data-view.service';
import { JarvisDataStoreService } from '../../service/jarvis-data-store.service';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';
import { ViewBean } from '../../model/view-bean';
import { Oauth2Bean, MeBean } from '../../model/security/oauth2-bean';

@Component({
  selector: 'app-jarvis-desktop',
  templateUrl: './jarvis-desktop.component.html',
  styleUrls: ['./jarvis-desktop.component.css']
})
export class JarvisDesktopComponent implements OnInit {

  myViews: ViewBean[];

  constructor(
    private snackBar: MatSnackBar,
    private jarvisDataDeviceService: JarvisDataDeviceService,
    private jarvisDataStoreService: JarvisDataStoreService,
    private jarvisDataViewService: JarvisDataViewService) {
  }

  ngOnInit() {
    /**
     * get profile from store
     */
    this.jarvisDataStoreService.loadViews();
    this.myViews = this.jarvisDataStoreService.getViews();
  }

  /**
   * implement touch on device
   * @param device 
   */
  private touch(device: DeviceBean): void {
    this.jarvisDataDeviceService.Task(device.id, "execute", {})
      .subscribe(
      (data: any) => {
        /**
         * notify snackbar
         */
        this.snackBar.open('Touch', device.name, {
          duration: 2000,
        });
      }
      );
  }
}
