import { Component } from '@angular/core';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';

/**
 * data model
 */
import { DeviceBean } from './model/device-bean';

@Component({
  selector: 'app-root',
  providers: [JarvisDataDeviceService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app works great plus!';

  constructor(private _jarvisDataDeviceService: JarvisDataDeviceService) {
  }

  showDialog: boolean = false;
  editingTodo = null;
  fieldValue: string = '';
  todoList: any = [];
  okButtonText: string = 'Create task';

  myDevices: DeviceBean [];

  private getAllDevices(): void {
    var myDevices: DeviceBean [];

    this._jarvisDataDeviceService
        .GetAll()
        .subscribe((data:DeviceBean[]) => this.myDevices = data,
            error => console.log(error),
            () => console.log('Get all Items complete'));
  }

  todoDialog(todo = null) {
    this.getAllDevices();

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
