import { Component, Input, OnInit } from '@angular/core';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';
import { ViewBean } from '../../model/view-bean';

@Component({
  selector: 'app-jarvis-tiles-view',
  templateUrl: './jarvis-tiles-view.component.html',
  styleUrls: ['./jarvis-tiles-view.component.css']
})
export class JarvisTilesViewComponent implements OnInit {

  @Input() myDevices: DeviceBean[];

tiles: any[] = [
    {text: 'One', cols: 3, rows: 1, color: 'lightblue'},
    {text: 'Two', cols: 1, rows: 2, color: 'lightgreen'},
    {text: 'Three', cols: 1, rows: 1, color: 'lightpink'},
    {text: 'Four', cols: 2, rows: 1, color: '#DDBDF1'},
  ];

  dogs: Object[] = [
    { name: 'Porter', human: 'Kara' },
    { name: 'Mal', human: 'Jeremy' },
    { name: 'Koby', human: 'Igor' },
    { name: 'Razzle', human: 'Ward' },
    { name: 'Molly', human: 'Rob' },
    { name: 'Husi', human: 'Matias' },
  ];

  basicRowHeight = 80;
  fixedCols = 4;
  fixedRowHeight = 100;
  ratioGutter = 1;
  fitListHeight = '400px';
  ratio = '4:1';

  addTileCols() { this.tiles[2].cols++; }

  constructor() { }

  ngOnInit() {
  }

}
