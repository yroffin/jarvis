import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataCronService } from '../../service/jarvis-data-cron.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { CronBean } from '../../model/cron-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-resource-cron',
  templateUrl: './jarvis-resource-cron.component.html',
  styleUrls: ['./jarvis-resource-cron.component.css']
})
export class JarvisResourceCronComponent extends JarvisResource<CronBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myCron: CronBean;
  private types: SelectItem[];

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _cronService: JarvisDataCronService) {
    super('/crons', ['toggle','test'], _cronService, _route, _router);
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Couché du soleil', value: 'SUNSET' });
    this.types.push({ label: 'Levée du soleil', value: 'SUNRISE' });
    this.types.push({ label: 'Crontab', value: 'CRONTAB' });
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * task action
   */
  public task(action: string): void {
    /**
     * execute this plugin
     */
    if (action === 'toggle') {
      let myOutputData;
      this._cronService.Task(this.myCron.id, action, {})
        .subscribe(
        (result: any) => myOutputData = result,
        error => console.log(error),
        () => {
        }
        );
      return;
    }

    if (action === 'test') {
      let myOutputData;
      this._cronService.Task(this.myCron.id, action, {})
        .subscribe(
        (result: any) => myOutputData = result,
        error => console.log(error),
        () => {
        }
        );
      return;
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if(picker.action === 'complete') {
      this.myCron = resource;
    }
  }
}
