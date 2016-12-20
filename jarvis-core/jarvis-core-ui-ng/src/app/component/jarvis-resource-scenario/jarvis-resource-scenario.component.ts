import { Component, Input, ViewChild, OnInit, Inject, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataScenarioService } from '../../service/jarvis-data-scenario.service';
import { JarvisDataBlockService } from '../../service/jarvis-data-block.service';
import { JarvisDataTriggerService } from '../../service/jarvis-data-trigger.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { ScenarioBean } from '../../model/scenario-bean';
import { BlockBean } from '../../model/block-bean';
import { PickerBean } from '../../model/picker-bean';
import { TriggerBean } from '../../model/trigger-bean';

@Component({
  selector: 'app-jarvis-resource-scenario',
  templateUrl: './jarvis-resource-scenario.component.html',
  styleUrls: ['./jarvis-resource-scenario.component.css']
})
export class JarvisResourceScenarioComponent extends JarvisResource<ScenarioBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myScenario: ScenarioBean;

  @ViewChild('pickTriggers') pickTriggers;
  @ViewChild('pickBlocks') pickBlocks;

  /**
   * internal vars
   */
  myTrigger: TriggerBean;
  myBlock: BlockBean;

  private jarvisTriggerLink: JarvisResourceLink<TriggerBean>;
  private jarvisBlockLink: JarvisResourceLink<BlockBean>;

  /**
   * constructor
   */
  constructor(
    /**
     * route
     */
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _scenarioService: JarvisDataScenarioService,
    private _blockService: JarvisDataBlockService,
    private _triggerService: JarvisDataTriggerService) {
    super('/scenarios', ['execute'], _scenarioService, _route, _router);
    this.jarvisTriggerLink = new JarvisResourceLink<TriggerBean>();
    this.jarvisBlockLink = new JarvisResourceLink<BlockBean>();
  }

  /**
   * load resource and related data
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
    if (action === 'execute') {
      let myOutputData: any;
      this._scenarioService.Task(this.myScenario.id, action, {})
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
   * pick action
   */
  public pick(picker: PickerBean): void {
    /**
     * find notifications
     */
    if (picker.action === 'triggers') {
      this.pickTriggers.open(this);
    }
    if (picker.action === 'blocks') {
      this.pickBlocks.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'triggers') {
        this.jarvisTriggerLink.addLink(this.getResource().id, resource.id, this.getResource().triggers, { "order": "1", href: "HREF" }, this._scenarioService.allLinkedTrigger);
    }
    if (picker.action === 'blocks') {
        this.jarvisBlockLink.addLink(this.getResource().id, resource.id, this.getResource().blocks, { "order": "1", href: "HREF" }, this._scenarioService.allLinkedBlock);
    }
    if(picker.action === 'complete') {
      this.myScenario = <ScenarioBean> resource;
      this.myScenario.triggers = [];
      (new JarvisResourceLink<TriggerBean>()).loadLinks(resource.id, this.myScenario.triggers, this._scenarioService.allLinkedTrigger);
      this.myScenario.blocks = [];
      (new JarvisResourceLink<BlockBean>()).loadLinks(resource.id, this.myScenario.blocks, this._scenarioService.allLinkedBlock);
    }
  }

  /**
   * drop link
   */
  public dropTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.dropLink(linked, this.myScenario.id, this.myScenario.triggers, this._scenarioService.allLinkedTrigger);
  }

  /**
   * drop link
   */
  public updateTriggerLink(linked: TriggerBean): void {
    this.jarvisTriggerLink.updateLink(linked, this.myScenario.id, this._scenarioService.allLinkedTrigger);
  }

  /**
   * goto link
   */
  public gotoTriggerLink(linked: TriggerBean): void {
    this._router.navigate(['/triggers/' + linked.id]);
  }

  /**
   * drop link
   */
  public dropBlockLink(linked: BlockBean): void {
    this.jarvisBlockLink.dropLink(linked, this.myScenario.id, this.myScenario.blocks, this._scenarioService.allLinkedBlock);
  }

  /**
   * drop link
   */
  public updateBlockLink(linked: BlockBean): void {
    this.jarvisBlockLink.updateLink(linked, this.myScenario.id, this._scenarioService.allLinkedBlock);
  }

  /**
   * goto link
   */
  public gotoBlockLink(linked: BlockBean): void {
    this._router.navigate(['/blocks/' + linked.id]);
  }
}
