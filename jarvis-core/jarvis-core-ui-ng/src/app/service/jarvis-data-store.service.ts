import { Injectable } from '@angular/core';
import {Subject} from 'rxjs/Rx';

/**
 * data model
 */
import { ViewBean } from './../model/view-bean';
import { DeviceBean } from './../model/device-bean';

@Injectable()
export class JarvisDataStoreService {

  myViews: ViewBean[];
  myViewsChange: Subject<ViewBean[]> = new Subject<ViewBean[]>();

  constructor() {

  }

  public getViews(): ViewBean[] {
    return this.myViews;
  }

  public setViews(toStore: ViewBean[]) {
    this.myViews = toStore;
  }
}
