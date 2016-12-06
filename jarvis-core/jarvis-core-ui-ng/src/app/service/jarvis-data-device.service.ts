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
import { DeviceBean } from './../model/device-bean';
import { TriggerBean } from './../model/trigger-bean';
import { PluginBean } from './../model/plugin-bean';
import { LinkBean } from './../model/link-bean';

@Injectable()
export class JarvisDataDeviceService extends JarvisDataCoreResource<DeviceBean> implements JarvisDefaultResource<DeviceBean> {

    public allLinkedDevice: JarvisDefaultLinkResource<DeviceBean>;
    public allLinkedTrigger: JarvisDefaultLinkResource<TriggerBean>;
    public allLinkedPlugin: JarvisDefaultLinkResource<PluginBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'devices', _http);

        /**
         * map linked elements
         */
        this.allLinkedDevice = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/devices', _http);
        this.allLinkedTrigger = new JarvisDataLinkedResource<TriggerBean>(this.actionUrl, '/triggers', _http);
        this.allLinkedPlugin = new JarvisDataLinkedResource<PluginBean>(this.actionUrl, '/plugins/scripts', _http);
    }
}
