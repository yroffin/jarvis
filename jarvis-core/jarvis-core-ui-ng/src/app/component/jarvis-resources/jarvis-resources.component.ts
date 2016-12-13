import { Component, HostListener, OnInit } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute, Params } from '@angular/router';

import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from '../../service/jarvis-data-core-resource';

import { JarvisDataDeviceService } from '../../service/jarvis-data-device.service';
import { JarvisDataPluginService } from '../../service/jarvis-data-plugin.service';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';

@Component({
  selector: 'app-jarvis-resources',
  templateUrl: './jarvis-resources.component.html',
  styleUrls: ['./jarvis-resources.component.css']
})
export class JarvisResourcesComponent implements OnInit {

  myResourceName: string = "default";
  myResources: ResourceBean[];

  /**
   * constructor
   */
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _jarvisDataDeviceService: JarvisDataDeviceService,
    private _jarvisDataPluginService: JarvisDataPluginService
  ) {
  }

  ngOnInit() {
    /**
     * listen on route change
     */
    this.router.events
      .filter(event => event instanceof NavigationEnd)
      .subscribe((navigationEnd: NavigationEnd) => {
        // You only receive NavigationEnd events
        if (navigationEnd.url === '/devices') {
          this.load('devices', this._jarvisDataDeviceService);
        }
        if (navigationEnd.url === '/plugins') {
          this.load('plugins', this._jarvisDataPluginService);
        }
      });
  }

  /**
   * load this component with a new resource
   */
  public load<T extends ResourceBean>(res: string, jarvisDataService: JarvisDefaultResource<T>): void {
    /**
     * check if already loaded
     */
    if (this.myResourceName === res) {
      return;
    } else {
      this.myResourceName = res;
    }

    jarvisDataService.GetAll()
      .subscribe(
      (data: ResourceBean[]) => this.myResources = data,
      error => console.log(error),
      () => {
      }
      );
  }

  /**
   * view this resource
   */
  public view(resource: ResourceBean) {
    this.router.navigate(['/'+this.myResourceName+'/' + resource.id]);
  }
}
