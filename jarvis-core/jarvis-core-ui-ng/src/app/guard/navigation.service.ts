import { Injectable } from '@angular/core';

import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";

@Injectable()
export class NavigationGuard implements CanActivate {

  protected current: ActivatedRouteSnapshot;

  constructor() {
  }

  public canActivate(destination: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    this.current = destination;
    return true;
  }

  public getUrl(): string {
    return this.current.url[0].path;
  }
}
