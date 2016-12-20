import { Router, ActivatedRoute, Params } from '@angular/router';
import * as _ from 'lodash';

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
  ) {
  }

  /**
   * load link
   */
  public loadLinks(owner: string, elements: Array<T>, service: JarvisDefaultLinkResource<T>): void {
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
      (data: T[]) => _.merge(elements, data),
      error => console.log(error),
      () => {
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
      error => console.log(error),
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
      error => console.log(error),
      () => {
        /**
         * add this element to current view
         */
        elements.push(created);
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
      error => console.log(error),
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
      error => console.log(error),
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
      error => console.log(error),
      () => {
      });
  }
}
