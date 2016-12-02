import { Router, ActivatedRoute, Params } from '@angular/router';

import { JarvisDefaultResource } from '../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

export interface CompleteCallback<T extends ResourceBean> {
    (that: JarvisDefaultResource<T>, resource: T): void 
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
    private state: number = 0;
    private maxState: number = 0;

    /**
     * constructor
     */
    constructor(
        _maxState: number,
        _uri: string,
        _tasks: string[],
        _resourceService: JarvisDefaultResource<T>,
        _route: ActivatedRoute,
        _router: Router) {
        this.maxState = _maxState;
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
    public init(completeCallback: CompleteCallback<T>): void {
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
                    completeCallback(this.myJarvisResource, this.getResource());
                }
            );
        });
    }

    public close(): void {
        this.router.navigate([this.uri]);
    }

    public save(): void {
        this.myJarvisResource.Update(this.myResource.id, this.myResource)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }

    public remove(): void {
        this.myJarvisResource.Delete(this.myResource.id)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
                this.close();
            });
    }

    public duplicate(): void {
        this.myJarvisResource.Add(this.myResource)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }

    public task(action: string): void {
        this.myJarvisResource.Task(this.myResource.id, action)
            .subscribe(
            (data: T) => data,
            error => console.log(error),
            () => {
            });
    }

    /**
     * retrieve current state
     */
    private getState(): number {
        return this.state;
    }

    /**
     * go to next state
     */
    private next(): number {
        this.state++;
        if (this.state > this.maxState) {
            this.state = 0;
        }
        return this.state;
    }
}
