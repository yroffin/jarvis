import { Component } from '@angular/core';
import { NameService } from './name.service';

@Component({
  selector: 'name-component',
  template: 'Hello {{name}}'
})
export class NameComponent {
  
  name:string;

  constructor(private nameService: NameService) {}
  
  ngOnInit() {
    this.name = this.nameService.getName();
  }
}