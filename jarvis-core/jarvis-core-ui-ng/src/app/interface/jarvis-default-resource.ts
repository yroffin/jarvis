import { Observable } from 'rxjs/Observable';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

export interface JarvisDefaultLinkResource<T extends ResourceBean> {
    GetAll(id: string): Observable<T[]>;
    GetSingle(id: string, linkId: string): Observable<T>;
    Add(id: string, linkId: string, linkToAdd: any): Observable<T>;
    Update(id: string, linkId: string, instance: string, linkToUpdate: any): Observable<T>;
    Delete(id: string, linkId: string, instance: string): Observable<T>;
}

export interface JarvisDefaultResource<T extends ResourceBean> {
    Task(id: string, task: string, args: any): Observable<any>;
    GetAll(): Observable<T[]>;
    GetSingle(id: string): Observable<T>;
    Add(itemToAdd: T): Observable<T>;
    Update(id: string, itemToUpdate: T): Observable<T>;
    Delete(id: string): Observable<T>;
}
