import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
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
import { TriggerBean } from '../../model/trigger-bean';
import { CronBean } from '../../model/cron-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-resource-trigger',
  templateUrl: './jarvis-resource-trigger.component.html',
  styleUrls: ['./jarvis-resource-trigger.component.css']
})
export class JarvisResourceTriggerComponent extends JarvisResource<TriggerBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myTrigger: TriggerBean;

  @ViewChild('pickCrons') pickCrons;

  /**
   * internal vars
   */
  myCron: CronBean;

  private jarvisCronLink: JarvisResourceLink<CronBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _triggerService: JarvisDataTriggerService,
    private _cronService: JarvisDataCronService) {
    super('/triggers', ['execute'], _triggerService, _route, _router);
    this.jarvisCronLink = new JarvisResourceLink<CronBean>();
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * complete resource
   */
  public complete(resource: TriggerBean): void {
    this.myTrigger = resource;
    this.myTrigger.crons = [];
    (new JarvisResourceLink<CronBean>()).loadLinks(resource.id, resource.crons, this._triggerService.allLinkedCron);
  }

  /**
   * task action
   */
  public execute(): void {
    /**
     * execute this plugin
     */
    let myOutputData;
    this._triggerService.Task(this.myTrigger.id, 'execute', {})
      .subscribe(
      (result: any) => myOutputData = result,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * pick action
   */
  public pick(picker: PickerBean): void {
    /**
     * find notifications
     */
    if (picker.action === 'crons') {
      this.pickCrons.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'crons') {
      this.jarvisCronLink.addLink(this.getResource().id, resource.id, this.getResource().crons, { "order": "1", href: "HREF" }, this._triggerService.allLinkedCron);
    }
    if (picker.action === 'complete') {
      this.myTrigger = <TriggerBean>resource;
      this.myTrigger.crons = [];
      (new JarvisResourceLink<CronBean>()).loadLinks(resource.id, this.myTrigger.crons, this._triggerService.allLinkedCron);
    }
  }

  /**
   * drop crontab link
   */
  public dropCronLink(linked: CronBean): void {
    this.jarvisCronLink.dropLink(linked, this.myTrigger.id, this.myTrigger.crons, this._triggerService.allLinkedCron);
  }

  /**
   * drop crontab link
   */
  public updateCronLink(linked: CronBean): void {
    this.jarvisCronLink.updateLink(linked, this.myTrigger.id, this._triggerService.allLinkedCron);
  }

  /**
   * goto crontab link
   */
  public gotoCronLink(linked: CronBean): void {
    this._router.navigate(['/crons/' + linked.id]);
  }
}
