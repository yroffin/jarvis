/* 
 * Copyright 2017 Yannick Roffin.
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

import { LoggerService } from '../service/logger.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

/**
 * default class to handle default link behaviour
 */
export class JarvisResourceLink<T extends ResourceBean> {

  /**
   * constructor
   */
  constructor(
    private logger: LoggerService
  ) {
  }

  /**
   * load link
   */
  public loadLinks(owner: string, elements: Array<T>, service: JarvisDefaultLinkResource<T>): void {
    this.loadLinksWithCallback(owner, elements, service, (data) => {
      elements = data;
    });
  }

  /**
   * load link
   */
  public loadLinksWithCallback(owner: string, elements: Array<T>, service: JarvisDefaultLinkResource<T>, callback: (element: Array<T>) => void): void {
    /**
     * clear array
     */
    _.remove(elements, function (element) {
      return true;
    })
    /**
     * then load it
     */
    service.GetAll(owner)
      .subscribe(
      (data: T[]) => {
        _.merge(elements, data)
        elements = [...elements];
      },
      error => this.logger.error("GetAll", error),
      () => {
        callback(elements);
      });
  }

  /**
   * load link with filter
   */
  public loadFilteredLinks(owner: string, elements: Array<T>, service: JarvisDefaultLinkResource<T>, filters: string): void {
    /**
     * clear array
     */
    _.remove(elements, function (element) {
      return true;
    })
    /**
     * then load it
     */
    service.FindAll(owner, filters)
      .subscribe(
      (data: T[]) => _.merge(elements, data),
      error => this.logger.error("FindAll", error),
      () => {
      });
  }

  /**
   * add link
   */
  public addLink(owner: string, linked: string, elements: Array<T>, value: any, service: JarvisDefaultLinkResource<T>): void {
    let created: T;
    service.Add(owner, linked, value)
      .subscribe(
      (data: T) => created = data,
      error => this.logger.error("addLink", error),
      () => {
        /**
         * add this element to current view
         */
        elements.push(created);
        elements = [...elements];
      });
  }

  /**
   * drop link
   */
  public dropLink(linked: T, owner: string, elements: Array<T>, service: JarvisDefaultLinkResource<T>): void {
    let deleted;
    service.Delete(owner, linked.id, linked.instance)
      .subscribe(
      (data: T) => deleted = data,
      error => this.logger.error("dropLink", error),
      () => {
        _.remove(elements, function (element) {
          return element.id === linked.id && element.instance === linked.instance;
        });
      });
  }

  /**
   * drop link
   */
  public dropHrefLink(linked: T, owner: string, elements: Array<T>, href: string, service: JarvisDefaultLinkResource<T>): void {
    let deleted;
    service.DeleteWithFilter(owner, linked.id, linked.instance, 'href=' + href)
      .subscribe(
      (data: T) => deleted = data,
      error => this.logger.error("dropHrefLink", error),
      () => {
        _.remove(elements, function (element) {
          return element.id === linked.id && element.instance === linked.instance;
        });
      });
  }

  /**
   * drop link
   */
  public updateLink(linked: T, owner: string, service: JarvisDefaultLinkResource<T>): void {
    let deleted;
    service.Update(owner, linked.id, linked.instance, linked.extended)
      .subscribe(
      (data: T) => deleted = data,
      error => this.logger.error("updateLink", error),
      () => {
      });
  }
}
