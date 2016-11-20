import { Component, OnInit } from '@angular/core';
import { MdButton } from '@angular2-material/button';

@Component({
  selector: 'app-jarvis-dlg-component',
  templateUrl: './jarvis-dlg-component.component.html',
  styleUrls: ['./jarvis-dlg-component.component.css'],
  entryComponents: [ MdButton ]
})

export class JarvisDlgComponentComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
