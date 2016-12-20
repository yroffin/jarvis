import { Router, ActivatedRoute, Params } from '@angular/router';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';

import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

/**
 * default class to handle default behaviour or resource
 * component
 */
export class JarvisDataLinkedResource<T extends ResourceBean> implements JarvisDefaultLinkResource<T> {

    private actionUrl: string;
    private headers: Headers;
    private link: string;
    private http: Http;

    /**
     * constructor
     */
    constructor(actionUrl: string, link: string, _http: Http) {
        this.http = _http;
        this.link = link;
        this.actionUrl = actionUrl;

        this.headers = new Headers();
        this.headers.append('Content-Type', 'application/json');
        this.headers.append('Accept', 'application/json');
    }

    /**
     * get all link
     */
    public GetAll = (id: string): Observable<T[]> => {
        return this.http.get(this.actionUrl + '/' + id + this.link, { headers: this.headers })
            .map((response: Response) => <T[]>response.json())
            .catch(this.handleError);
    }

    /**
     * get all link
     */
    public FindAll = (id: string, filters: string): Observable<T[]> => {
        return this.http.get(this.actionUrl + '/' + id + this.link + '?' + filters, { headers: this.headers })
            .map((response: Response) => <T[]>response.json())
            .catch(this.handleError);
    }

    /**
     * get single link
     */
    public GetSingle = (id: string, linkId: string): Observable<T> => {
        return this.http.get(this.actionUrl + '/' + id  + this.link + '/' + linkId, { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * add a new link
     */
    public Add = (id: string, linkId: string, linkToAdd: any): Observable<T> => {
        return this.http.post(this.actionUrl + '/' + id  + this.link + '/' + linkId, JSON.stringify(linkToAdd), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * update a link
     */
    public Update = (id: string, linkId: string, instance: string, linkToUpdate: any): Observable<T> => {
        return this.http.put(this.actionUrl + '/' + id  + this.link + '/' + linkId + '?instance=' + instance, JSON.stringify(linkToUpdate), { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * delete a link
     */
    public Delete = (id: string, linkId: string, instance: string): Observable<T> => {
        return this.http.delete(this.actionUrl + '/' + id  + this.link + '/' + linkId + '?instance=' + instance, { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * delete a link
     */
    public DeleteWithFilter = (id: string, linkId: string, instance: string, filters: string): Observable<T> => {
        return this.http.delete(this.actionUrl + '/' + id  + this.link + '/' + linkId + '?instance=' + instance + '&' + filters, { headers: this.headers })
            .map((response: Response) => <T>response.json())
            .catch(this.handleError);
    }

    /**
     * handle error
     */
    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }
}
