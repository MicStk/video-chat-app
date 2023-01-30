//https://stackoverflow.com/questions/41280471/how-to-implement-routereusestrategy-shoulddetach-for-specific-routes-in-angular/


import {ActivatedRouteSnapshot, DetachedRouteHandle, BaseRouteReuseStrategy, Route} from '@angular/router';


export class CustomReuseStrategy extends BaseRouteReuseStrategy {

private handlers: {[key: string]: DetachedRouteHandle} = {};

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    return route.data.reuseRoute;
  }

  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle): void {
    this.handlers[route.url.join('/') || route.parent.url.join('/')] = handle;
  }

  shouldAttach(route: ActivatedRouteSnapshot): boolean {
   return !!this.handlers[route.url.join('/') || route.parent.url.join('/')];
  }

  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle {
    return this.handlers[route.url.join('/') || route.parent.url.join('/')];
  }

  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
   return future.routeConfig === curr.routeConfig;
  }

}

