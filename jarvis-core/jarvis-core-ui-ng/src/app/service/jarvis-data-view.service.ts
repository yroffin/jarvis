import { Injectable, ChangeDetectorRef } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import {Subject} from 'rxjs/Rx';
import 'rxjs/add/operator/map'
import { Observable } from 'rxjs/Observable';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDataDeviceService } from './jarvis-data-device.service';
import { JarvisDataStoreService } from './jarvis-data-store.service';

import * as _ from 'lodash';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';

@Injectable()
export class JarvisDataViewService {

  private actionUrl: string;
  private headers: Headers;

  constructor(
    private _http: Http,
    private _changeDetectorRef: ChangeDetectorRef,
    private _configuration: JarvisConfigurationService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataDeviceService: JarvisDataDeviceService) {

    this.actionUrl = _configuration.ServerWithApiUrl + 'views';

    this.headers = new Headers();
    this.headers.append('Content-Type', 'application/json');
    this.headers.append('Accept', 'application/json');
  }

  public FindViewsAndDevices(): Observable<ViewBean[]> {
    let that = this;

    /**
     * find all views
     */
    let myViewsObservable = this.GetAll();

    myViewsObservable
        .subscribe(
          (data:ViewBean[]) => this._jarvisDataStoreService.setViews(data),
          error => console.log(error),
          () => {
            let myViews = this._jarvisDataStoreService.getViews();
            let myDevices: DeviceBean[];
            /**
             * iterate on each view
             */
            _.forEach(this._jarvisDataStoreService.getViews(), function(myView) {
              /**
               * find all devices, store this in an observable
               * to synchronize loading
               */
              let myDeviceObs = that.GetAllDevices(myView.id);
              myDeviceObs
                  .subscribe(
                    (data:DeviceBean[]) => myDevices = data,
                    error => console.log(error),
                    () => {
                      /**
                       * handle devices
                       */
                      myView.devices = myDevices;
                      let render: any;
                      _.forEach(myDevices, function(myDevice) {
                        that._jarvisDataDeviceService.Task(myDevice.id, 'render', {})
                        .subscribe(
                          (data:any) => render = data,
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

  public GetAllDevices = (id: string): Observable<DeviceBean[]> => {
    return this._http.get(this.actionUrl + '/' + id + '/devices')
      .map((response: Response) => <DeviceBean[]>response.json())
      .catch(this.handleError);
  }

  public GetAll = (): Observable<ViewBean[]> => {
    return this._http.get(this.actionUrl)
      .map((response: Response) => <ViewBean[]>response.json())
      .catch(this.handleError);
  }

  public GetSingle = (id: number): Observable<ViewBean> => {
    return this._http.get(this.actionUrl + id)
      .map((response: Response) => <ViewBean>response.json())
      .catch(this.handleError);
  }

  public Add = (itemName: string): Observable<ViewBean> => {
    let toAdd = JSON.stringify({ ItemName: itemName });

    return this._http.post(this.actionUrl, toAdd, { headers: this.headers })
      .map((response: Response) => <ViewBean>response.json())
      .catch(this.handleError);
  }

  public Update = (id: number, itemToUpdate: DeviceBean): Observable<ViewBean> => {
    return this._http.put(this.actionUrl + id, JSON.stringify(itemToUpdate), { headers: this.headers })
      .map((response: Response) => <ViewBean>response.json())
      .catch(this.handleError);
  }

  public Delete = (id: number): Observable<Response> => {
    return this._http.delete(this.actionUrl + id)
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
