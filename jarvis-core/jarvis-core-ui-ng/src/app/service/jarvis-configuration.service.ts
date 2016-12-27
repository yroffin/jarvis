import { Injectable } from '@angular/core';

@Injectable()
export class JarvisConfigurationService {

  public ServerWithUrl;
  public ServerWithApiUrl;

  private Server: string = "/";
  private ApiUrl: string = "api/";

  constructor() {
    this.ServerWithUrl = this.Server;
    this.ServerWithApiUrl = this.Server + this.ApiUrl;
  }

}
