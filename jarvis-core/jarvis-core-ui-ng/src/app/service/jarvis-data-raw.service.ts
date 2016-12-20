import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';

/**
 * data model
 */
import { ResourceBean } from './../model/resource-bean';

@Injectable()
export class JarvisDataRawService extends JarvisDataCoreResource<ResourceBean> implements JarvisDefaultResource<ResourceBean> {

    /**
     * constructor
     */
    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl, _http);
    }

    /**
     * get all resources
     */
    public RawGet = (urn: string): Observable<string> => {
        return this.http.get(this.actionUrl + urn)
            .map((response: Response) => <string> response.text())
            .catch(this.handleError);
    }
}
