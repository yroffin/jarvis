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
   * drop trigger link
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
   * drop trigger link
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
