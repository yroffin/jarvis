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

  @ViewChild('pickConditionnalPlugins') pickConditionnalPlugins;
  @ViewChild('pickThenPlugins') pickThenPlugins;
  @ViewChild('pickElsePlugins') pickElsePlugins;
  @ViewChild('pickThenBlocks') pickThenBlocks;
  @ViewChild('pickElseBlocks') pickElseBlocks;

  /**
   * internal vars
   */
  private jarvisBlockLink: JarvisResourceLink<BlockBean>;
  private jarvisPluginLink: JarvisResourceLink<PluginBean>;

  items: MenuItem[];

  private myPlugin: PluginBean;
  private viewCondition: boolean = false;
  
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
    this.jarvisPluginLink = new JarvisResourceLink<PluginBean>();
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
     * test this plugin
     */
    if (action === 'test') {
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
    if (picker.action === 'thenBlocks') {
      this.pickThenBlocks.open(this);
    }
    if (picker.action === 'elseBlocks') {
      this.pickElseBlocks.open(this);
    }
    /**
     * find plugins
     */
    if (picker.action === 'conditionnalPlugins') {
      this.pickConditionnalPlugins.open(this);
    }
    if (picker.action === 'thenPlugins') {
      this.pickThenPlugins.open(this);
    }
    if (picker.action === 'elsePlugins') {
      this.pickElsePlugins.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if (picker.action === 'thenBlocks') {
        this.jarvisBlockLink.addLink(this.getResource().id, resource.id, this.getResource().thenBlocks, { "order": "1", href: "HREF_THEN" }, this._blockService.allLinkedBlock);
    }
    if (picker.action === 'elseBlocks') {
        this.jarvisBlockLink.addLink(this.getResource().id, resource.id, this.getResource().elseBlocks, { "order": "1", href: "HREF_ELSE" }, this._blockService.allLinkedBlock);
    }
    if (picker.action === 'conditionnalPlugins') {
        this.jarvisPluginLink.addLink(this.getResource().id, resource.id, this.getResource().conditionnalPlugins, { "order": "1", href: "HREF_IF" }, this._blockService.allLinkedPlugin);
    }
    if (picker.action === 'thenPlugins') {
        this.jarvisPluginLink.addLink(this.getResource().id, resource.id, this.getResource().thenPlugins, { "order": "1", href: "HREF_THEN" }, this._blockService.allLinkedPlugin);
    }
    if (picker.action === 'elsePlugins') {
        this.jarvisPluginLink.addLink(this.getResource().id, resource.id, this.getResource().elsePlugins, { "order": "1", href: "HREF_ELSE" }, this._blockService.allLinkedPlugin);
    }
    /**
     * load all elements
     */
    if(picker.action === 'complete') {
      this.myBlock = <BlockBean> resource;
      this.myBlock.thenBlocks = [];
      (new JarvisResourceLink<BlockBean>()).loadFilteredLinks(resource.id, this.myBlock.thenBlocks, this._blockService.allLinkedBlock, "href=HREF_THEN");
      this.myBlock.elseBlocks = [];
      (new JarvisResourceLink<BlockBean>()).loadFilteredLinks(resource.id, this.myBlock.elseBlocks, this._blockService.allLinkedBlock, "href=HREF_ELSE");
      this.myBlock.conditionnalPlugins = [];
      (new JarvisResourceLink<PluginBean>()).loadFilteredLinks(resource.id, this.myBlock.conditionnalPlugins, this._blockService.allLinkedPlugin, "href=HREF_IF");
      this.myBlock.thenPlugins = [];
      (new JarvisResourceLink<PluginBean>()).loadFilteredLinks(resource.id, this.myBlock.thenPlugins, this._blockService.allLinkedPlugin, "href=HREF_THEN");
      this.myBlock.elsePlugins = [];
      (new JarvisResourceLink<PluginBean>()).loadFilteredLinks(resource.id, this.myBlock.elsePlugins, this._blockService.allLinkedPlugin, "href=HREF_ELSE");
    }
  }

  /**
   * drop link
   */
  public dropPluginLink(linked: PluginBean, href: string, collection: PluginBean[]): void {
    this.jarvisPluginLink.dropHrefLink(linked, this.myBlock.id, collection, href, this._blockService.allLinkedPlugin);
  }

  /**
   * drop link
   */
  public updatePluginLink(linked: PluginBean): void {
    this.jarvisPluginLink.updateLink(linked, this.myBlock.id, this._blockService.allLinkedPlugin);
  }

  /**
   * view link
   */
  public viewPluginLink(linked: PluginBean): void {
    this.myPlugin = linked;
    this.viewCondition = true;
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
  public dropBlockLink(linked: BlockBean, href: string, collection: BlockBean[]): void {
    this.jarvisBlockLink.dropHrefLink(linked, this.myBlock.id, collection, href, this._blockService.allLinkedBlock);
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
