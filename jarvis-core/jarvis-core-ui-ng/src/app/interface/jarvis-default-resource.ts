/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Observable } from 'rxjs/Observable';

/**
 * data model
 */
import { ResourceBean } from '../model/resource-bean';

export interface JarvisDefaultLinkResource<T extends ResourceBean> {
    GetAll(id: string): Observable<T[]>;
    FindAll(id: string, filters: string): Observable<T[]>;
    GetSingle(id: string, linkId: string): Observable<T>;
    Add(id: string, linkId: string, linkToAdd: any): Observable<T>;
    Update(id: string, linkId: string, instance: string, linkToUpdate: any): Observable<T>;
    Delete(id: string, linkId: string, instance: string): Observable<T>;
    DeleteWithFilter(id: string, linkId: string, instance: string, filters: string): Observable<T>;
}

export interface JarvisDefaultResource<T extends ResourceBean> {
    Task(id: string, task: string, args: any): Observable<any>;
    GetAll(): Observable<T[]>;
    GetSingle(id: string): Observable<T>;
    Add(itemToAdd: T): Observable<T>;
    Update(id: string, itemToUpdate: T): Observable<T>;
    Delete(id: string): Observable<T>;
}
