import { Observable } from 'rxjs/Observable';

export interface JarvisDefaultResource<T> {
    Task(id: string, task: string): Observable<any>;
    GetAll(): Observable<T[]>;
    GetSingle(id: string): Observable<T>;
    Add(itemToAdd: T): Observable<T>;
    Update(id: string, itemToUpdate: T): Observable<T>;
    Delete(id: string): Observable<T>;
}
