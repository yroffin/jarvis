import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MenuItem } from 'primeng/primeng';

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
import { BlockBean } from '../../model/block-bean';
import { PluginBean } from '../../model/plugin-bean';
import { PickerBean } from '../../model/picker-bean';

@Component({
  selector: 'app-jarvis-resource-block',
  templateUrl: './jarvis-resource-block.component.html',
  styleUrls: ['./jarvis-resource-block.component.css']
})
export class JarvisResourceBlockComponent extends JarvisResource<BlockBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myBlock: BlockBean;

  @ViewChild('pickConditionnalBlocks') pickConditionnalBlocks;
  @ViewChild('pickConditionnalPlugins') pickConditionnalPlugins;

  /**
   * internal vars
   */
  myConditionnalBlock: BlockBean;

  private jarvisBlockLink: JarvisResourceLink<BlockBean>;
  private jarvisConditionnalPluginLink: JarvisResourceLink<PluginBean>;

  items: MenuItem[];

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _blockService: JarvisDataBlockService
  ) {
    super('/blocks', ['execute','test'], _blockService, _route, _router);
    this.jarvisBlockLink = new JarvisResourceLink<BlockBean>();
    this.jarvisConditionnalPluginLink = new JarvisResourceLink<PluginBean>();
  }

  /**
   * load resource and related data
   */
  ngOnInit() {
    this.init(this);

    /**
     * configure action bar
     */
    this.items = [
            {
              label: 'Condition',
              icon: 'fa-check',
              command: () => {
                let picker: PickerBean = new PickerBean();
                picker.action = 'conditionnalPlugins';
                this.pick(picker);
              },
            },
            {
              label: 'Condition',
              icon: 'fa-check',
              command: () => {
                let picker: PickerBean = new PickerBean();
                picker.action = 'conditionnalPlugins';
                this.pick(picker);
              },
            }
        ];
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
      this._blockService.Task(this.myBlock.id, action, {})
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
     * find blocks
     */
    if (picker.action === 'blocks') {
      this.pickConditionnalBlocks.open(this);
    }
    /**
     * find conditionnal plugins
     */
    if (picker.action === 'conditionnalPlugins') {
      this.pickConditionnalPlugins.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'blocks') {
        this.jarvisBlockLink.addLink(this.getResource().id, resource.id, this.getResource().blocks, { "order": "1", href: "HREF" }, this._blockService.allLinkedBlock);
    }
    if (picker.action === 'conditionnalPlugins') {
        this.jarvisConditionnalPluginLink.addLink(this.getResource().id, resource.id, this.getResource().conditionnalPlugins, { "order": "1", href: "HREF_IF" }, this._blockService.allLinkedPlugin);
    }
    if(picker.action === 'complete') {
      this.myBlock = <BlockBean> resource;
      this.myBlock.blocks = [];
      (new JarvisResourceLink<BlockBean>()).loadLinks(resource.id, this.myBlock.blocks, this._blockService.allLinkedBlock);
      this.myBlock.conditionnalPlugins = [];
      (new JarvisResourceLink<PluginBean>()).loadFilteredLinks(resource.id, this.myBlock.conditionnalPlugins, this._blockService.allLinkedPlugin, "href=HREF_IF");
    }
  }

  /**
   * drop link
   */
  public dropConditionnalPluginLink(linked: PluginBean): void {
    this.jarvisConditionnalPluginLink.dropLink(linked, this.myBlock.id, this.myBlock.conditionnalPlugins, this._blockService.allLinkedPlugin);
  }

  /**
   * drop link
   */
  public updateConditionnalPluginLink(linked: PluginBean): void {
    this.jarvisConditionnalPluginLink.updateLink(linked, this.myBlock.id, this._blockService.allLinkedPlugin);
  }

  /**
   * goto link
   */
  public gotoPluginLink(linked: PluginBean): void {
    this._router.navigate(['/plugins/' + linked.id]);
  }

  /**
   * drop link
   */
  public dropBlockLink(linked: BlockBean): void {
    this.jarvisBlockLink.dropLink(linked, this.myBlock.id, this.myBlock.blocks, this._blockService.allLinkedBlock);
  }

  /**
   * drop link
   */
  public updateBlockLink(linked: BlockBean): void {
    this.jarvisBlockLink.updateLink(linked, this.myBlock.id, this._blockService.allLinkedBlock);
  }

  /**
   * goto link
   */
  public gotoBlockLink(linked: BlockBean): void {
    this._router.navigate(['/blocks/' + linked.id]);
  }
}
