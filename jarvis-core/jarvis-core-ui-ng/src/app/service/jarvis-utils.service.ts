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

import { Injectable, Inject } from '@angular/core';

/**
 * return the global native browser window object
 */
function _window(): any {
    return window;
}

@Injectable()
export class WindowRef {
    private _window: any;

    constructor() {
        this._window = window;
    }

    /**
     * get hash
     */
    public getHash(): string {
        return this._window.location.hash;
    }

    /**
     * get search
     */
    public getSearch(): string {
        return this._window.location.search;
    }

    /**
     * get href
     */
    public getHref(): string {
        return this._window.location.href;
    }

    /**
     * get hosts
     */
    public getHost(): string {
        return this._window.location.host;
    }

    /**
     * get FileReader
     */
    public getFileReader(): any {
        return new FileReader();
    }

    /**
     * change href
     */
    public setHref(ref: string): void {
        this._window.location.href = ref;
    }

    /**
     * get window
     */
    public getWindow(): any {
        return this._window;
    }

    /**
     * get window
     */
    public getHostname(): any {
        return this._window.location.hostname;
    }
}
