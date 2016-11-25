import { Component, Input, HostListener, OnInit, ChangeDetectorRef } from '@angular/core';
import * as _ from 'lodash';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';

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

  @Input() myView: ViewBean;
  @Input() myDevices: DeviceBean[];

  fixedCols = 1;
  basicRowHeight = 80;
  fixedRowHeight = 100;
  ratioGutter = 1;
  fitListHeight = '128px';
  ratio = '4:2';

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.responsive(event.target.innerWidth);
  }

  constructor(
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService
  ) {
    this.responsive(window.innerHeight);
  }

  ngOnInit() {
  }

  results: string[];
  value: string;

  private responsive(innerWidth: number): void {
    switch (this._jarvisConfigurationService.Layout(innerWidth)) {
      case this._jarvisConfigurationService.layoutXs:
        this.fixedCols = 2;
        break;
      case this._jarvisConfigurationService.layoutSm:
        this.fixedCols = 3;
        break;
      case this._jarvisConfigurationService.layoutMd:
        this.fixedCols = 9;
        break;
      case this._jarvisConfigurationService.layoutLg:
        this.fixedCols = 8;
        break;
      case this._jarvisConfigurationService.layoutXl:
        this.fixedCols = 12;
        break;
    }
  }

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
   * execute action on this device
   */
  public execute(device: DeviceBean): void {
    this._jarvisDataDeviceService.Task(device.id, 'execute')
      .subscribe(
      (data: any) => data,
      error => console.log(error),
      () => { }
      );

  }
}
