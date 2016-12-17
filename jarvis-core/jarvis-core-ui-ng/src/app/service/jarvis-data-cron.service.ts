import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';
import { JarvisDataLinkedResource } from './jarvis-data-linked-resource';

/**
 * data model
 */
import { CronBean } from './../model/cron-bean';

@Injectable()
export class JarvisDataCronService extends JarvisDataCoreResource<CronBean> implements JarvisDefaultResource<CronBean> {

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'crons', _http);
    }
}
