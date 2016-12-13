import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MdDialogRef, MdDialog } from '@angular/material/dialog';
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
import { CompleteCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
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
export class JarvisResourcePluginComponent extends JarvisResource<PluginBean> implements OnInit {

  @Input() myPlugin: PluginBean;
  @Input() myJsonData: string = "{}";
  @Input() myRawData: string = "{}";

  private myData: any = {};
  private myOutputData: any = {};
  private types: SelectItem[];

  @ViewChild('dataContainer') dataContainer: ElementRef;

  /**
   * internal vars
   */
  display: boolean = false;
  myCommand: CommandBean;

  dialogRef: MdDialogRef<JarvisPickerComponent>;

  private jarvisCommandLink: JarvisResourceLink<CommandBean>;

  /**
   * constructor
   */
  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _pluginService: JarvisDataPluginService,
    private _commandService: JarvisDataCommandService,
    private dialog: MdDialog) {
    super('/plugins', ['execute', 'render', 'clear'], _pluginService, _route, _router);
    this.jarvisCommandLink = new JarvisResourceLink<CommandBean>();
    this.types = [];
    this.types.push({ label: 'Select type', value: null });
    this.types.push({ label: 'Plugin Script', value: { id: 1, name: 'Plugin Script', code: 'script' } })
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this.complete);
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
  public complete(owner: any, that: JarvisDataPluginService, resource: PluginBean): void {
    owner.myPlugin = resource;
    resource.commands = [];
    (new JarvisResourceLink<CommandBean>()).loadLinks(resource.id, resource.commands, that.allLinkedCommand);
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
    this.openDialog(picker.action);
  }

  /**
   * picker dialog
   */
  openDialog(action: string) {
    this.dialogRef = this.dialog.open(JarvisPickerComponent, {
      disableClose: false
    });

    if (action === 'commands') {
      this.dialogRef.componentInstance.loadResource<CommandBean>('plugins', 12, this._commandService);
    }

    this.dialogRef.afterClosed().subscribe(result => {
      this.dialogRef = null;
      if (result === null) {
        return;
      }

      /**
       * handle commands
       */
      if (action === 'commands') {
        this.jarvisCommandLink.addLink(this.getResource().id, result.id, this.getResource().commands, { "order": "1", href: "HREF" }, this._pluginService.allLinkedCommand);
      }
    });
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
