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

import { Component, Renderer, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { SelectItem, Message } from 'primeng/primeng';

declare var Prism: any;

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { WindowRef } from '../../service/jarvis-utils.service';
import { JarvisDataSnapshotService } from '../../service/jarvis-data-snapshot.service';
import { JarvisMessageService } from '../../service/jarvis-message.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { SnapshotBean } from '../../model/misc/snapshot-bean';

@Component({
  selector: 'app-jarvis-resource-snapshot',
  templateUrl: './jarvis-resource-snapshot.component.html',
  styleUrls: ['./jarvis-resource-snapshot.component.css']
})
export class JarvisResourceSnapshotComponent extends JarvisResource<SnapshotBean> implements NotifyCallback<SnapshotBean>, OnInit {

  @Input() mySnapshot: SnapshotBean;
  @ViewChild('fileInput') fileInput: ElementRef;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _renderer: Renderer,
    private _windowRef: WindowRef,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private jarvisMessageService: JarvisMessageService,
    private _snapshotService: JarvisDataSnapshotService) {
    super('/snapshots', [], _snapshotService, _route, _router);
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * file action
   */
  public onChange(event): void {
    let files = event.srcElement.files;

    let that = this;
    let myReader = this._windowRef.getFileReader();

    myReader.onloadend = function (e) {
      that.mySnapshot.json = myReader.result;
    }

    myReader.readAsText(files[0]);
  }

  /**
   * task action
   */
  public download(): void {
    let that = this;
    let myOutputData: string;
    this._snapshotService.Task(this.mySnapshot.id, 'download', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
        /**
         * TODO: compute locale
         */
        let datePipe = new DatePipe('en-US');
        let fileName = 'export-' + datePipe.transform(new Date(), 'yyyyMMdd-HHmmss') + '.json';
        let a: any = document.createElement("a");
        document.body.appendChild(a);
        a.style = "display: none";
        let file = new Blob([JSON.stringify(myOutputData, null, 2)], { type: 'application/text' });
        let fileURL = window.URL.createObjectURL(file);
        a.href = fileURL;
        a.download = fileName;
        a.click();
        this.jarvisMessageService.push({ severity: 'info', summary: 'Téléchargement', detail: this.mySnapshot.name });
      }
      );
  }

  /**
   * task action
   */
  public reload(): void {
    let event = new MouseEvent('click', { bubbles: true });
    this._renderer.invokeElementMethod(
      this.fileInput.nativeElement, 'dispatchEvent', [event]);
    this.jarvisMessageService.push({ severity: 'info', summary: 'Chargement', detail: this.mySnapshot.name });
  }

  /**
   * task action
   */
  public restore(): void {
    let myOutputData: string;
    this._snapshotService.Task(this.mySnapshot.id, 'restore', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
        this.mySnapshot.output = myOutputData;
        console.log("result:", myOutputData);
        this.jarvisMessageService.push({ severity: 'info', summary: 'Restoration', detail: this.mySnapshot.name });
      }
      );
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'complete') {
      this.mySnapshot = <SnapshotBean>resource;
    }
  }

  /**
   * highlight source
   */
  public hightlight(body: string): string {
    if(body) {
      return Prism.highlight(body, Prism.languages.javascript);
    } else {
      return "";
    }
  }
}
