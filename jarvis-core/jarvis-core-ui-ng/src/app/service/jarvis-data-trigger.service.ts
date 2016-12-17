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
import { TriggerBean } from './../model/trigger-bean';
import { CronBean } from './../model/cron-bean';

@Injectable()
export class JarvisDataTriggerService extends JarvisDataCoreResource<TriggerBean> implements JarvisDefaultResource<TriggerBean> {

    public allLinkedCron: JarvisDefaultLinkResource<CronBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'triggers', _http);

        /**
         * map linked elements
         */
        this.allLinkedCron = new JarvisDataLinkedResource<CronBean>(this.actionUrl, '/crons', _http);
    }
}
