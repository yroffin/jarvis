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
import { ScriptBean } from './../model/script-bean';

@Injectable()
export class JarvisDataDeviceService extends JarvisDataCoreResource<DeviceBean> implements JarvisDefaultResource<DeviceBean> {

    public allLinkedDevice: JarvisDefaultLinkResource<DeviceBean>;
    public allLinkedTrigger: JarvisDefaultLinkResource<DeviceBean>;
    public allLinkedPluginScript: JarvisDefaultLinkResource<DeviceBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'devices', _http);

        /**
         * map linked elements
         */
        this.allLinkedDevice = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/devices', _http);
        this.allLinkedTrigger = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/triggers', _http);
        this.allLinkedPluginScript = new JarvisDataLinkedResource<DeviceBean>(this.actionUrl, '/plugins/scripts', _http);
    }
}
