import { Component, OnInit, Input } from '@angular/core';

/**
 * data model
 */
import { DeviceBean } from '../../../model/device-bean';

@Component({
  selector: 'app-jarvis-resource-device-general',
  templateUrl: './jarvis-resource-device-general.component.html',
  styleUrls: ['./jarvis-resource-device-general.component.css']
})
export class JarvisResourceDeviceGeneralComponent implements OnInit {

  @Input() myDevice: DeviceBean;

  constructor() { }

  ngOnInit() {
  }

}
