import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';
import { JarvisDataLinkedResource } from './jarvis-data-linked-resource';

import { JarvisDataDeviceService } from './jarvis-data-device.service';
import { JarvisDataStoreService } from './jarvis-data-store.service';

import * as _ from 'lodash';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';

@Injectable()
export class JarvisDataViewService extends JarvisDataCoreResource<ViewBean> implements JarvisDefaultResource<ViewBean> {

  public allLinkedDevice: JarvisDefaultLinkResource<DeviceBean>;

  /**
   * constructor
   */
  constructor(
    private _http: Http,
    private _configuration: JarvisConfigurationService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataDeviceService: JarvisDataDeviceService
  ) {
    super(_configuration.ServerWithApiUrl + 'views', _http);

        /**
         * map linked elements
         */
        this.allLinkedDevice = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/devices', _http);
  }

  /**
   * find views and devices
   */
  public FindViewsAndDevices(): Observable<ViewBean[]> {
    let that = this;

    /**
     * find all views
     */
    let myViewsObservable = this.GetAll();

    myViewsObservable
      .subscribe(
      (data: ViewBean[]) => this._jarvisDataStoreService.setViews(data),
      error => console.log(error),
      () => {
        let myViews = this._jarvisDataStoreService.getViews();
        let myDevices: DeviceBean[];
        /**
         * iterate on each view
         */
        _.forEach(this._jarvisDataStoreService.getViews(), function (myView) {
          /**
           * find all devices, store this in an observable
           * to synchronize loading
           */
          that.allLinkedDevice.GetAll(myView.id)
            .subscribe(
            (data: DeviceBean[]) => myDevices = data,
            error => console.log(error),
            () => {
              /**
               * handle devices
               */
              myView.devices = myDevices;
              let render: any;
              _.forEach(myDevices, function (myDevice) {
                that._jarvisDataDeviceService.Task(myDevice.id, 'render', {})
                  .subscribe(
                  (data: any) => render = data,
                  error => console.log(error),
                  () => {
                    myDevice.render = render
                  }
                  );
              });
            });
        });
      });

    /**
     * sync views and devices
     */
    return myViewsObservable;
  }
}
