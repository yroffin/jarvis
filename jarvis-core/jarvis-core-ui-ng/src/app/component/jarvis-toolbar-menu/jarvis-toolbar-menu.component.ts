import { Component, Input, OnInit } from '@angular/core';

import { JarvisToolbarAction } from '../../interface/jarvis-toolbar-action';

/**
 * data model
 */
import { TaskBean } from '../../model/task-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-toolbar-menu',
  templateUrl: './jarvis-toolbar-menu.component.html',
  styleUrls: ['./jarvis-toolbar-menu.component.css']
})
export class JarvisToolbarMenuComponent implements OnInit {

  @Input() actions: JarvisToolbarAction;
  @Input() tasks: TaskBean[];
  @Input() pickers: PickerBean[];

  constructor() { }

  ngOnInit() {
  }

  public close(): void {
    this.actions.close();
  }

  public save(): void {
    this.actions.save();
  }

  public remove(): void {
    this.actions.remove();
  }

  public duplicate(): void {
    this.actions.duplicate();
  }

  public next(): void {
    this.actions.next();
  }

  public task(tsk: TaskBean): void {
    this.actions.task(tsk.action);
  }

  public pick(pck: PickerBean): void {
    this.actions.pick(pck);
  }
}
