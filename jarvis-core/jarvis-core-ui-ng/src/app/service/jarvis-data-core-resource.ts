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
     * execute remote task on this device
     */
    public Task = (id: string, task: string): Observable<any> => {
        return this.http.post(this.actionUrl + '/' + id + '?task=' + task, {})
            .map((response: Response) => <any>response.json())
            .catch(this.handleError);
    }

    public GetAll = (): Observable<T[]> => {
        return this.http.get(this.actionUrl)
            .map((response: Response) => <T[]>response.json())
            .catch(this.handleError);
    }

    public GetSingle = (id: string): Observable<T> => {
        return this.http.get(this.actionUrl + '/' + id)
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    public Add = (itemToAdd: T): Observable<T> => {
        return this.http.post(this.actionUrl, JSON.stringify(itemToAdd), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    public Update = (id: string, itemToUpdate: T): Observable<T> => {
        return this.http.put(this.actionUrl + '/' + id, JSON.stringify(itemToUpdate), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    public Delete = (id: string): Observable<T> => {
        return this.http.delete(this.actionUrl + '/' + id, { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }
}
