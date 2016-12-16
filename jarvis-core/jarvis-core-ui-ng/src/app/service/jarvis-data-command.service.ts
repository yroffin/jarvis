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
import { CommandBean } from './../model/command-bean';
import { NotificationBean } from './../model/notification-bean';
import { LinkBean } from './../model/link-bean';

@Injectable()
export class JarvisDataCommandService extends JarvisDataCoreResource<CommandBean> implements JarvisDefaultResource<CommandBean> {

    public allLinkedNotification: JarvisDefaultLinkResource<NotificationBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'commands', _http);

        /**
         * map linked elements
         */
        this.allLinkedNotification = new JarvisDataLinkedResource<NotificationBean>(this.actionUrl, '/notifications', _http);
    }
}
