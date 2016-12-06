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
import { PluginBean } from './../model/plugin-bean';

@Injectable()
export class JarvisDataPluginService extends JarvisDataCoreResource<PluginBean> implements JarvisDefaultResource<PluginBean> {

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'plugins/scripts', _http);
    }
}

