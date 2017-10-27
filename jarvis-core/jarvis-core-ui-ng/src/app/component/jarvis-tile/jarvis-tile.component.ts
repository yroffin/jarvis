import { Component, Input, OnInit } from '@angular/core';

import { MatSnackBar } from '@angular/material';
import * as _ from 'lodash';

import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisMessageService } from '../../service/jarvis-message.service';
import { Message } from 'primeng/primeng';

/**
 * data model
 */
import { DeviceBean } from '../../model/device-bean';

@Component({
  selector: 'app-jarvis-tile',
  templateUrl: './jarvis-tile.component.html',
  styleUrls: ['./jarvis-tile.component.css']
})
export class JarvisTileComponent implements OnInit {
  @Input() myDevice: DeviceBean;

  constructor(
    private snackBar: MatSnackBar,
    private jarvisDataDeviceService: JarvisDataDeviceService,
    private jarvisMessageService: JarvisMessageService
  ) {
  }

  ngOnInit() {
  }

  results: string[];
  value: string;

  private getFromBetween(sub1: string, sub2: string): string {
    if (this.value.indexOf(sub1) < 0 || this.value.indexOf(sub2) < 0) return "";
    var SP = this.value.indexOf(sub1) + sub1.length;
    var string1 = this.value.substr(0, SP);
    var string2 = this.value.substr(SP);
    var TP = string1.length + string2.indexOf(sub2);
    return this.value.substring(SP, TP);
  }

  private removeFromBetween(sub1: string, sub2: string): void {
    if (this.value.indexOf(sub1) < 0 || this.value.indexOf(sub2) < 0) return;
    var removal = sub1 + this.getFromBetween(sub1, sub2) + sub2;
    this.value = this.value.replace(removal, "");
  }

  private getAllResults(sub1: string, sub2: string): void {
    // first check to see if we do have both substrings
    if (this.value.indexOf(sub1) < 0 || this.value.indexOf(sub2) < 0) return;

    // find one result
    var result = this.getFromBetween(sub1, sub2);
    // push it to the results array
    this.results.push(result);
    // remove the most recently found one from the string
    this.removeFromBetween(sub1, sub2);

    // if there's more substrings
    if (this.value.indexOf(sub1) > -1 && this.value.indexOf(sub2) > -1) {
      this.getAllResults(sub1, sub2);
    }
    else return;
  }

  private parse(val: string): string[] {
    this.results = [];
    if(val === undefined) {
      return this.results;
    }
    this.value = val;
    this.getAllResults("{{", "}}");
    return this.results;
  }

  /**
   * transform this template
   */
  public getTemplate(device: DeviceBean): string {
    let template: string = device.template;
    let data = device.render;
    _.forEach(this.parse(device.template), function (key) {
      try {
        let parsed = template.replace("{{" + key + "}}", eval(key));
        template = parsed;
      }
      catch (e) {
      }
    });
    return template;
  }

  /**
   * implement touch on device
   * @param device 
   */
  public touch(device: DeviceBean): void {
    this.jarvisDataDeviceService.Task(device.id, "execute", {})
      .subscribe(
      (data: any) => {
        /**
         * notify snackbar
         */
        this.snackBar.open('Touch', device.name, {
          duration: 2000,
        });
      }
      );
  }
}
