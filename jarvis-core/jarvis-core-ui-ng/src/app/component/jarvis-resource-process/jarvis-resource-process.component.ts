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

import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

declare var Prism: any;

import { LoggerService } from '../../service/logger.service';
import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisDataProcessService } from '../../service/jarvis-data-process.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

/**
 * class
 */
import { JarvisPicker } from '../../class/jarvis-pickers';
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { ProcessBean } from '../../model/code/process-bean';
import { TriggerBean } from '../../model/trigger-bean';

@Component({
  selector: 'app-jarvis-resource-process',
  templateUrl: './jarvis-resource-process.component.html',
  styleUrls: ['./jarvis-resource-process.component.css']
})
export class JarvisResourceProcessComponent extends JarvisResource<ProcessBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myProcess: ProcessBean;
  @ViewChild('pickTriggers') pickTriggers;

  /**
   * internal data
   */
  private myData: any = {};
  private myOutputData: any = {};
  myTrigger: TriggerBean;
  private jarvisTriggerLink: JarvisResourceLink<TriggerBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private logger: LoggerService,
    private _processService: JarvisDataProcessService,
    private _triggerService: JarvisDataTriggerService
  ) {
    super('/processes', ['deploy', 'test'], _processService, _route, _router);
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>(this.logger);
  }

  ngOnInit() {
    this.init(this);
  }

  /**
   * highlight source
   */
  public hightlight(body: string): void {
    if (body) {
      return Prism.highlight(body, Prism.languages.xml);
    }
  }

  /**
   * pick action
   */
  public pick(picker: PickerBean): void {
    /**
     * find notifications
     */
    if (picker.action === 'triggers') {
      this.pickTriggers.open(this);
    }
  }

  /**
   * task action
   */
  public deploy(): void {
    this._processService.Update(this.myProcess.id, this.myProcess)
      .subscribe(
      (data: ProcessBean) => data,
      error => console.log(error),
      () => {
        this.logger.info("Save", this.myProcess);
        /**
         * execute this plugin
         */
        let myOutputData;
        this._processService.Task(this.myProcess.id, 'deploy', {})
          .subscribe(
          (result: any) => myOutputData = result,
          error => console.log(error),
          () => {
            this.logger.info("Deploy", this.myProcess);
          }
          );
      });
  }

  /**
   * task action
   */
  public test(): void {
    this.logger.info("Execute", this.myProcess);
    /**
     * execute this plugin
     */
    let myOutputData;
    this._processService.Task(this.myProcess.id, 'execute', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'triggers') {
      this.jarvisTriggerLink.addLink(this.getResource().id, resource.id, this.getResource().triggers, { "order": "1", href: "HREF" }, this._processService.allLinkedTrigger);
    }
    if (picker.action === 'complete') {
      this.myProcess = <ProcessBean>resource;
      this.myProcess.triggers = [];
      (new JarvisResourceLink<TriggerBean>(this.logger)).loadLinksWithCallback(resource.id, this.myProcess.triggers, this._processService.allLinkedTrigger, (elements) => {
        this.myProcess.triggers = elements;
      });
    }
  }

  /**
   * drop link
   */
  public dropTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.dropLink(linked, this.myProcess.id, this.myProcess.triggers, this._processService.allLinkedTrigger);
  }

  /**
   * drop link
   */
  public updateTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.updateLink(linked, this.myProcess.id, this._processService.allLinkedTrigger);
  }

  /**
   * goto link
   */
  public gotoTriggerLink(linked: TriggerBean): void {
    this._router.navigate(['/triggers/' + linked.id]);
  }
}
