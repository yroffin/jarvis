import { Injectable } from '@angular/core';

@Injectable()
export class JarvisConfigurationService {

  public Server: string = "/";
  public ApiUrl: string = "api/";
  public ServerWithApiUrl = this.Server + this.ApiUrl;

  constructor() {

  }

  public layoutXs: number = 0;
  public layoutSm: number = 2;
  public layoutMd: number = 4;
  public layoutLg: number = 6;
  public layoutXl: number = 8;

  /**
   * compute configuration
   * layout-xs	width < 600px
   * layout-sm	600px <= width < 960px
   * layout-md	960px <= width < 1280px
   * layout-lg	1280px <= width < 1920px
   * layout-xl	width >= 1920px
   */
  public Layout(width: number): number {
    if(width >= 1920) return this.layoutXl;
    if(1280 <= width && width < 1920) return this.layoutLg;
    if(960 <= width && width < 1280) return this.layoutMd;
    if(600 <= width && width < 960) return this.layoutSm;
    if(width < 600) return this.layoutXs;
  }

}
