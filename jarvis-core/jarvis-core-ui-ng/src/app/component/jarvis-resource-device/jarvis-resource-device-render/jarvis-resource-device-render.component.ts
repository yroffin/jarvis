import { Component, OnInit, ElementRef, Input } from '@angular/core';
import { HighlightJsService } from '../../../../../node_modules/angular2-highlight-js';

/**
 * data model
 */
import { DeviceBean } from '../../../model/device-bean';

@Component({
  selector: 'app-jarvis-resource-device-render',
  templateUrl: './jarvis-resource-device-render.component.html',
  styleUrls: ['./jarvis-resource-device-render.component.css']
})
export class JarvisResourceDeviceRenderComponent implements OnInit {
  @Input() myDevice: DeviceBean;

  constructor(private service: HighlightJsService) {
  }

  highlightByService(target: ElementRef) {
        this.service.highlight(target);
  }

  ngOnInit() {
  }

}
