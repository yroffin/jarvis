import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

declare var Prism: any;

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataCommandService } from '../../service/jarvis-data-command.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';
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
import { DeviceBean } from '../../model/device-bean';
import { PluginBean } from '../../model/plugin-bean';
import { PickerBean } from '../../model/picker-bean';
import { LinkBean } from '../../model/link-bean';
import { CommandBean } from '../../model/command-bean';

@Component({
  selector: 'app-jarvis-resource-plugin',
  templateUrl: './jarvis-resource-plugin.component.html',
  styleUrls: ['./jarvis-resource-plugin.component.css']
})
export class JarvisResourcePluginComponent extends JarvisResource<PluginBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myPlugin: PluginBean;
  @Input() myJsonData: string = "{}";
  @Input() myRawData: string = "{}";

  private myData: any = {};
  private myOutputData: any = {};
  private types: SelectItem[];

  @ViewChild('pickCommands') pickCommands;

  /**
   * internal vars
   */
  display: boolean = false;
  myCommand: CommandBean;

  private jarvisCommandLink: JarvisResourceLink<CommandBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _pluginService: JarvisDataPluginService,
    private _commandService: JarvisDataCommandService) {
    super('/plugins', ['execute', 'render', 'clear'], _pluginService, _route, _router);
    this.jarvisCommandLink = new JarvisResourceLink<CommandBean>();
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Plugin Script', value: 'script' });
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);
  }

  /**
   * pretty
   */
  private pretty(val) {
    let body = JSON.stringify(val, null, 2);
    return Prism.highlight(body, Prism.languages.javascript);
  }

  /**
   * complete resource
   */
  public complete(resource: PluginBean): void {
    this.myPlugin = resource;
    this.myPlugin.commands = [];
    (new JarvisResourceLink<CommandBean>()).loadLinks(resource.id, resource.commands, this._pluginService.allLinkedCommand);
  }

  /**
   * task action
   */
  public task(action: string): void {
    /**
     * execute this plugin
     */
    if (action === 'execute') {
      this.myData = JSON.parse(this.myJsonData);
      this.myRawData = JSON.stringify(this.myData);
      this._pluginService.Task(this.myPlugin.id, action, this.myData)
        .subscribe(
        (result: any) => this.myOutputData = result,
        error => console.log(error),
        () => {
        }
        );
      return;
    }

    /**
     * render this plugin
     */
    if (action === 'render') {
      this.myData = JSON.parse(this.myJsonData);
      this.myRawData = JSON.stringify(this.myData);
      this._pluginService.Task(this.myPlugin.id, action, this.myData)
        .subscribe(
        (result: any) => this.myOutputData = result,
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
    if (picker.action === 'commands') {
      this.pickCommands.open(this);
    }
  }

  /**
   * notify to add new resource
   */
  public notify(action: string, resource: ResourceBean): void {
    if (action === 'commands') {
        this.jarvisCommandLink.addLink(this.getResource().id, resource.id, this.getResource().commands, { "order": "1", href: "HREF" }, this._pluginService.allLinkedCommand);
    }
    if(action === 'complete') {
      this.myPlugin = <PluginBean> resource;
      this.myPlugin.commands = [];
      (new JarvisResourceLink<CommandBean>()).loadLinks(resource.id, this.myPlugin.commands, this._pluginService.allLinkedCommand);
    }
  }

  /**
   * drop command link
   */
  public dropCommandLink(linked: CommandBean): void {
    this.jarvisCommandLink.dropLink(linked, this.myPlugin.id, this.myPlugin.commands, this._pluginService.allLinkedCommand);
  }

  /**
   * drop command link
   */
  public updateCommandLink(linked: CommandBean): void {
    this.jarvisCommandLink.updateLink(linked, this.myPlugin.id, this._pluginService.allLinkedCommand);
  }

  /**
   * drop command link
   */
  public viewCommandLink(linked: CommandBean): void {
    this.myCommand = linked;
    this.display = true;
  }

  /**
   * highlight source
   */
  public hightlight(body: string): void {
    return Prism.highlight(body, Prism.languages.javascript);
  }

  /**
   * goto command link
   */
  public gotoCommandLink(linked: CommandBean): void {
    this._router.navigate(['/commands/' + linked.id]);
  }
}