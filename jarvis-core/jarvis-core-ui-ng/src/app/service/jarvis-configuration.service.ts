import { Injectable } from '@angular/core';

@Injectable()
export class JarvisConfigurationService {

  public Server: string = "/";
  public ApiUrl: string = "api/";
  public ServerWithApiUrl = this.Server + this.ApiUrl;

  constructor() { }

}
