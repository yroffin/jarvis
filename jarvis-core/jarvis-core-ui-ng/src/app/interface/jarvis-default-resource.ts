import { Observable } from 'rxjs/Observable';

export interface JarvisDefaultLinkResource<T> {
    GetAll(id: string): Observable<T[]>;
    GetSingle(id: string, linkId: string): Observable<T>;
    Add(id: string, linkId: string, linkToAdd: any): Observable<T>;
    Update(id: string, linkId: string, instance: string, linkToUpdate: any): Observable<T>;
    Delete(id: string, linkId: string, instance: string): Observable<T>;
}

export interface JarvisDefaultResource<T> {
    Task(id: string, task: string): Observable<any>;
    GetAll(): Observable<T[]>;
    GetSingle(id: string): Observable<T>;
    Add(itemToAdd: T): Observable<T>;
    Update(id: string, itemToUpdate: T): Observable<T>;
    Delete(id: string): Observable<T>;
}
