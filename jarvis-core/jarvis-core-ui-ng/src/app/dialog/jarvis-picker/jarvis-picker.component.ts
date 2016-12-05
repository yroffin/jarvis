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

import { Component, ViewChild, OnInit } from '@angular/core';
import { MdDialogRef, MdDialog } from '@angular/material/dialog';
import { TreeNode, TREE_ACTIONS, KEYS, IActionMapping } from 'angular2-tree-component';
import * as _ from 'lodash';

import { MdCard } from '@angular/material/card';
import { MdInput } from '@angular/material/input';
import { MdToolbar } from '@angular/material/toolbar';
import { MdButton } from '@angular/material/button';

import { JarvisDefaultResource } from '../../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';

@Component({
  selector: 'app-jarvis-picker',
  templateUrl: './jarvis-picker.component.html',
  entryComponents: [MdCard, MdToolbar, MdInput, MdButton],
  styleUrls: ['./jarvis-picker.component.css']
})
export class JarvisPickerComponent implements OnInit {

  @ViewChild('tree') treeNode;
  private nodes: any[] = null;

  constructor(public dialogRef: MdDialogRef<JarvisPickerComponent>) {
  }

  onEvent = console.log.bind(console);

  /**
   * validate current focused node
   */
  public validate(): void {
    let node = this.treeNode.treeModel.getFocusedNode();
    if (node != null) {
      if (node.data.resourceData != null) {
        this.dialogRef.close(node.data.resourceData);
      }
    } else {
      /**
       * close dialog without any selection
       */
      this.dialogRef.close();
    }
  }

  /**
   * close dialog without any selection
   */
  public cancel(): void {
    this.dialogRef.close();
  }

  /**
   * load a new resource in this dialog
   */
  public loadResource<T extends ResourceBean>(name: string, max: number, resource: JarvisDefaultResource<T>) {
    let all: T[];
    resource.GetAll()
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

  /**
   * init this component
   */
  ngOnInit() {
  }
}
