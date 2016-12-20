import { Router, ActivatedRoute, Params } from '@angular/router';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';

import { JarvisDefaultResource } from '../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

/**
 * default class to handle default behaviour or resource
 * component
 */
export class JarvisDataCoreResource<T extends ResourceBean> implements JarvisDefaultResource<T> {

    protected actionUrl: string;
    protected headers: Headers;
    protected http: Http;

    /**
     * constructor
     */
    constructor(actionUrl: string, _http: Http) {
        this.http = _http;
        this.actionUrl = actionUrl;

        this.headers = new Headers();
        this.headers.append('Content-Type', 'application/json');
        this.headers.append('Accept', 'application/json');
    }

    /**
     * execute remote task on this resource
     */
    public Task = (id: string, task: string, args: any): Observable<any> => {
        return this.http.post(this.actionUrl + '/' + id + '?task=' + task, JSON.stringify(args), { headers: this.headers })
            .map((response: Response) => <any>response.json())
            .catch(this.handleError);
    }

    /**
     * get all resources
     */
    public GetAll = (): Observable<T[]> => {
        return this.http.get(this.actionUrl)
            .map((response: Response) => <T[]>response.json())
            .catch(this.handleError);
    }

    /**
     * get single resource
     */
    public GetSingle = (id: string): Observable<T> => {
        return this.http.get(this.actionUrl + '/' + id)
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * add a new resource
     */
    public Add = (itemToAdd: T): Observable<T> => {
        return this.http.post(this.actionUrl, JSON.stringify(itemToAdd), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * update this resource
     */
    public Update = (id: string, itemToUpdate: T): Observable<T> => {
        return this.http.put(this.actionUrl + '/' + id, JSON.stringify(itemToUpdate), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * delete this resource
     */
    public Delete = (id: string): Observable<T> => {
        return this.http.delete(this.actionUrl + '/' + id, { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * error handler
     */
    protected handleError(error: Response) {
        return Observable.throw(error || 'Server error');
    }
}
