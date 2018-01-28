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

import { Component, OnInit, Inject, Input, Output } from '@angular/core';
import { HostBinding, HostListener, ElementRef } from '@angular/core';
import { TreeNode } from 'primeng/primeng';
import { LoggerService } from '../../service/logger.service';

import * as THREE from 'three';
import * as _ from 'lodash';

import { JarvisCanvasService } from '../../service/jarvis-canvas.service';

import * as CANVAS from '../../model/3d/canvas.model'

@Component({
  selector: 'app-jarvis-scene-editor',
  templateUrl: './jarvis-scene-editor.component.html',
  styleUrls: ['./jarvis-scene-editor.component.css']
})
export class JarvisSceneEditorComponent extends CANVAS.Scene implements OnInit {

  /**
   * internal
   */
  public threejsId: string;
  private el: HTMLElement;
  public selectedItem: TreeNode;

  constructor(
    @Inject(ElementRef) elementRef: ElementRef,
    private logger: LoggerService,
    private _canvas: JarvisCanvasService
  ) {
    super();
    this.el = elementRef.nativeElement;
    this.threejsId = Math.random().toString(36).replace(/[^a-z]+/g, '');
  }

  /**
   * init component
   */
  ngOnInit() {
  }

  /**
   * view init
   */
  ngAfterViewInit() {
    this.viewInit();
  }

  ngOnChanges() {
  }

  /**
   * init the view
   */
  private viewInit(): void {
    // init the scene
    this.initialize(this.logger, this.threejsId);

    this.block(0, 10, 0, 'Hello three.js!\ntest\nline de plus xxxxxxxxx');
    this.block(12, 10, 0, 'Hello three.js!\ntest\nline de plus');
  }

  /**
   * isMesh
   */
  public isMesh() {
    return this.selectedItem && this.selectedItem.data.type === 'Mesh';
  }

  /**
   * isGroup
   */
  public isGroup() {
    return this.selectedItem && this.selectedItem.data.type === 'Group';
  }

  /**
   * isCamera
   */
  public isCamera() {
    return this.selectedItem && this.selectedItem.data.type === 'Camera';
  }

  /**
   * mouse move event
   * @param event 
   */
  @HostListener('mousemove', ['$event'])
  private handleMousemove(event) {
  }

  /**
   * wheel
   * @param event 
   */
  @HostListener('wheel', ['$event'])
  private handleWheel(event) {
  }

  /**
   * handle dblclick event
   * @param event 
   */
  @HostListener('dblclick', ['$event'])
  private handleDblclick(event) {
  }

  /**
   * handle keydown
   * @param event 
   */
  @HostListener('window:keydown', ['$event'])
  private handleKeydown(event) {
  }
}
