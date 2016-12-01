import { Component, HostListener, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisGrid } from '../../class/jarvis-grid';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';

@Component({
  selector: 'app-jarvis-resources',
  templateUrl: './jarvis-resources.component.html',
  styleUrls: ['./jarvis-resources.component.css']
})
export class JarvisResourcesComponent extends JarvisGrid implements OnInit {

  myResourceName: string = "default";
  myResources: ResourceBean[];

  /**
   * listen on resize event
   */
  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.responsive(event.target.innerWidth);
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService
  ) {
    super(_jarvisConfigurationService);
  }

  ngOnInit() {
    this._jarvisDataDeviceService.GetAll()
      .subscribe(
      (data: ResourceBean[]) => this.myResources = data,
      error => console.log(error),
      () => { }
      );
  }

  view(resource: ResourceBean) {
    this.router.navigate(['/devices/' + resource.id]);
  }
}
