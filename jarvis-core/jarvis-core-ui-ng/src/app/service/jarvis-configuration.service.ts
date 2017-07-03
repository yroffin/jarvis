import { Injectable } from '@angular/core';

@Injectable()
export class JarvisConfigurationService {

  public ServerWithUrl: string;
  public ServerWithApiUrl: string;
  public JarvisAuthToken: string;

  private Server: string = "/";
  private ApiUrl: string = "api/";

  /**
   * constructor
   */
  constructor() {
    this.ServerWithUrl = this.Server;
    this.ServerWithApiUrl = this.Server + this.ApiUrl;
  }

  /**
   * fix session token
   * @param JarvisAuthToken 
   */
  public setJarvisAuthToken(JarvisAuthToken: string): void {
    this.JarvisAuthToken = JarvisAuthToken;
  }

  /**
   * get token
   * @param JarvisAuthToken 
   */
  public getJarvisAuthToken(): string {
    return this.JarvisAuthToken;
  }
}
