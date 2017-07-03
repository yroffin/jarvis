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

import { Injectable, Inject } from '@angular/core';
import { PlatformLocation } from '@angular/common';
import { DOCUMENT } from "@angular/platform-browser";
import * as _ from 'lodash';

@Injectable()
export class JarvisLoaderService {

  /**
   * internal pipe for security resolve
   */
  private doc: Document;
  private loaded: boolean;

  constructor(
    @Inject(DOCUMENT) doc: any,
  ) {
    this.doc = doc;
    this.loaded = false;
  }

  /**
   * fix loaded state
   */
  public setLoaded(): void {
    this.triggerOnDocument("angular2-app-ready");
  }

  /**
   * the given event on the document root.
   */
  private triggerOnDocument(eventType: string): Event {
    return (this.triggerOnElement(this.doc, eventType));
  }

  /**
   * trigger on element
   * Cf. https://github.com/bennadel/JavaScript-Demos/blob/master/demos/pre-bootstrap-evented-loading-screen-angular2
   */
  private triggerOnElement(
    nativeElement: any,
    eventType: string,
    bubbles: boolean = true,
    cancelable: boolean = false
  ): Event {

    var customEvent = this.createEvent(eventType, bubbles, cancelable);

    nativeElement.dispatchEvent(customEvent);

    return (customEvent);
  }

  /**
   * create and return a custom event with the given configuration
   */
  private createEvent(
    eventType: string,
    bubbles: boolean,
    cancelable: boolean
  ): Event {

    // IE (shakes fist) uses some other kind of event initialization. As such, 
    // we'll default to trying the "normal" event generation and then fallback to
    // using the IE version. 
    try {

      var customEvent: any = new CustomEvent(
        eventType,
        {
          bubbles: bubbles,
          cancelable: cancelable
        }
      );
    } catch (error) {
      var customEvent: any = this.doc.createEvent("CustomEvent");
      customEvent.initCustomEvent(eventType, bubbles, cancelable);
    }

    return (customEvent);
  }
}
