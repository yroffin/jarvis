import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {MdCard} from '@angular/material/card';
import {MdInput} from '@angular/material/input';
import {MdToolbar} from '@angular/material/toolbar';
import {MdButton} from '@angular/material/button';

@Component({
  selector: 'app-jarvis-dialog',
  templateUrl: 'jarvis-dialog.component.html',
  entryComponents: [MdCard, MdToolbar, MdInput, MdButton],
  styleUrls: ['jarvis-dialog.component.css']
})
export class JarvisDialogComponent implements OnInit {
  @Input() value: string;
  @Input() showPrompt: boolean;
  @Input() placeholder: string;
  @Input() title: string;
  @Input() template: string;
  @Input() okText: string;
  @Input() cancelText: string;
  @Output() valueEmitted = new EventEmitter<string>();

  constructor() {
    this.okText = 'OK';
    this.cancelText = 'Cancel';
  }

  emitValue(value) {
    this.valueEmitted.emit(value);
  }

  ngOnInit() {
  }

}