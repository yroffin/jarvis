import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisDefaultResource } from '../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';
import { PickerBean } from '../model/picker-bean';

/**
 * notify interface
 */
export interface NotifyCallback<T extends ResourceBean> {
    notify(action: PickerBean, resource: T): void 
};

/**
 * default class to handle default behaviour or resource
 * component
 */
export class JarvisResource<T extends ResourceBean> {

    private myJarvisResource: JarvisDefaultResource<T>;
    private myResource: T;
    private route: ActivatedRoute;
    private router: Router;

    private uri: string;
    private tasks: string[];

    /**
     * constructor
     */
    constructor(
        _uri: string,
        _tasks: string[],
        _resourceService: JarvisDefaultResource<T>,
        _route: ActivatedRoute,
        _router: Router) {
        this.uri = _uri;
        this.tasks = _tasks;
        this.myJarvisResource = _resourceService;
        this.route = _route;
        this.router = _router;
    }

    /**
     * accessor
     */
    public setResource(_resource: T) {
        this.myResource = _resource;
    }

    /**
     * accessor
     */
    public getResource(): T {
        return this.myResource;
    }

    /**
     * default init method
     */
    public init(that: NotifyCallback<T>): void {
        this.route.params
        .map(params => params['id'])
        .subscribe((id) => {
            this.myJarvisResource.GetSingle(id)
            .subscribe(
                (data: T) => this.setResource(data),
                error => console.log(error),
                () => {
                    /**
                     * complete resource
                     */
                    let picker: PickerBean = new PickerBean();
                    picker.action = 'complete';
                    that.notify(picker, this.getResource());
                }
            );
        });
    }

    /**
     * go back
     */
    public close(): void {
        this.router.navigate([this.uri]);
    }

    /**
     * save it
     */
    public save(): void {
        this.myJarvisResource.Update(this.myResource.id, this.myResource)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }

    /**
     * remove resource
     */
    public remove(): void {
        this.myJarvisResource.Delete(this.myResource.id)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
                this.close();
            });
    }

    /**
     * duplicate resource
     */
    public duplicate(): void {
        this.myJarvisResource.Add(this.myResource)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }

    /**
     * task action
     */
    public task(action: string): void {
        this.myJarvisResource.Task(this.myResource.id, action, {})
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }
}
