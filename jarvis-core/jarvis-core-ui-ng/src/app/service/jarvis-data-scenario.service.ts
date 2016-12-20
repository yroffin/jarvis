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
import { ScenarioBean } from './../model/scenario-bean';
import { BlockBean } from './../model/block-bean';
import { TriggerBean } from './../model/trigger-bean';

@Injectable()
export class JarvisDataScenarioService extends JarvisDataCoreResource<ScenarioBean> implements JarvisDefaultResource<ScenarioBean> {

    public allLinkedBlock: JarvisDefaultLinkResource<BlockBean>;
    public allLinkedTrigger: JarvisDefaultLinkResource<TriggerBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'scenarios', _http);

        /**
         * map linked elements
         */
        this.allLinkedBlock = new JarvisDataLinkedResource<BlockBean>(this.actionUrl, '/blocks', _http);
        this.allLinkedTrigger = new JarvisDataLinkedResource<TriggerBean>(this.actionUrl, '/triggers', _http);
    }
}
