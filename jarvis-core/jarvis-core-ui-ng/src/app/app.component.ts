import { Component, Input, OnInit, ChangeDetectorRef, ApplicationRef } from '@angular/core';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataViewService } from './service/jarvis-data-view.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';

/**
 * data model
 */
import { DeviceBean } from './model/device-bean';
import { ViewBean } from './model/view-bean';

@Component({
  selector: 'app-root',
  providers: [
    JarvisDataDeviceService,
    JarvisDataViewService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app works great plus!';

  constructor(
    private _changeDetectorRef: ChangeDetectorRef,
    private _applicationRef: ApplicationRef ,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataStoreService: JarvisDataStoreService,
    private _jarvisDataViewService: JarvisDataViewService) {
  }

  showDialog: boolean = false;
  editingTodo = null;
  fieldValue: string = '';
  todoList: any = [];
  okButtonText: string = 'Create task';

  myDevices: DeviceBean [];
  @Input() myViews: ViewBean [];

  private getAllViews(): void {
    this._jarvisDataViewService.FindViewsAndDevices().subscribe(
      (data:ViewBean[]) => this.myViews = this._jarvisDataStoreService.getViews()
    );
  }

  private getAllDevices(): void {
    var myDevices: DeviceBean [];

    this._jarvisDataDeviceService
        .GetAll()
        .subscribe((data:DeviceBean[]) => this.myDevices = data,
            error => console.log(error),
            () => console.log('Get all Items complete'));
  }

  ngOnInit() {
    this.getAllViews();
  }

  todoDialog(todo = null) {
    this.okButtonText = 'Create task';
    this.fieldValue = '';
    this.editingTodo = todo;
    if (todo) {
      this.fieldValue = todo.title;
      this.okButtonText = 'Edit task';
    }
    this.showDialog = true;
  }

  remove(index: number) {
    this.todoList.splice(index, 1);
  }

  updateTodo(title) {
    if (title) {
      title = title.trim();
      if (this.editingTodo) {
        this.editTodo(title);
      } else {
        this.addTodo(title);
      }
    }
    this.hideDialog();
  }

  editTodo(title) {
    this.editingTodo.title = title;
  }

  addTodo(title) {
    const todo = {title: title, completed: false};
    this.todoList.push(todo);
  }

  hideDialog() {
    this.showDialog = false;
    this.editingTodo = null;
    this.fieldValue = null; // make sure Input is always new
  }
}
