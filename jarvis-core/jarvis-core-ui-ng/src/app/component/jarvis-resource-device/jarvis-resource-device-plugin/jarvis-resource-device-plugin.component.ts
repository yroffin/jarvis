import { Component, OnInit, Input } from '@angular/core';

/**
 * data model
 */
import { DeviceBean } from '../../../model/device-bean';

@Component({
  selector: 'app-jarvis-resource-device-plugin',
  templateUrl: './jarvis-resource-device-plugin.component.html',
  styleUrls: ['./jarvis-resource-device-plugin.component.css']
})
export class JarvisResourceDevicePluginComponent implements OnInit {

  @Input() myDevice: DeviceBean;

  constructor() { }

  ngOnInit() {
  }

}
