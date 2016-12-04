import {Component, OnInit} from '@angular/core';

import { MdCard } from '@angular/material/card';
import { MdInput } from '@angular/material/input';
import { MdToolbar } from '@angular/material/toolbar';
import { MdButton } from '@angular/material/button';

@Component({
  selector: 'app-jarvis-picker',
  templateUrl: './jarvis-picker.component.html',
  entryComponents: [MdCard, MdToolbar, MdInput, MdButton],
  styleUrls: ['./jarvis-picker.component.css']
})
export class JarvisPickerComponent implements OnInit {

  ngOnInit() {
  }

}
