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

import { Router, ActivatedRoute, Params } from '@angular/router';
import * as _ from 'lodash';

import { JarvisDefaultResource } from '../interface/jarvis-default-resource';
import { JarvisDataNotificationService } from '../service/jarvis-data-notification.service';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';
import { PickerDialogBean } from '../model/picker-bean';

/**
 * default class to handle default behaviour or resource
 * component
 */
export class JarvisPicker<T extends ResourceBean> {

  private myJarvisConfig: PickerDialogBean;
  private myJarvisResource: JarvisDefaultResource<T>;
  public nodes: any[] = null;

  /**
   * constructor
   */
  constructor(
    _notificationService: JarvisDefaultResource<T>,
    _config: PickerDialogBean
  ) {
    this.myJarvisResource = _notificationService;
    this.myJarvisConfig = _config;
  }

  /**
   * load a new resource in this dialog
   */
  public loadResource(max: number) {
    let all: T[];
    this.myJarvisResource.GetAll()
      .subscribe(
      (data: T[]) => all = data,
      error => console.log(error),
      () => {
        this.nodes = [];
        let that = this;
        /**
         * order by name then chunk it by piece of max
         */
        _.forEach(_.chunk(_.orderBy(all, ['name'], ['asc']), max), function (chunked) {
          let node = {
            expanded: true,
            name: _.head(chunked).name + ' ... ' + _.last(chunked).name,
            subTitle: name,
            children: [
            ]
          };
          /**
           * each chunk are pushrd in the array
           */
          that.nodes.push(node);
          _.forEach(chunked, function (element) {
            node.children.push({
              name: element.name + '#' + element.id,
              resourceData: element,
              resourceId: element.id,
              hasChildren: false
            });
          });
        });
      });
  }
}
