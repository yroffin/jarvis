import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { JarvisConfigurationService } from './jarvis-configuration.service';
import { JarvisDefaultResource, JarvisDefaultLinkResource } from '../interface/jarvis-default-resource';
import { JarvisDataCoreResource } from './jarvis-data-core-resource';
import { JarvisDataLinkedResource } from './jarvis-data-linked-resource';

/**
 * data model
 */
import { DataSourceBean } from './../model/connector/datasource-bean';
import { ConnectorBean } from './../model/connector/connector-bean';

@Injectable()
export class JarvisDataDatasourceService extends JarvisDataCoreResource<DataSourceBean> implements JarvisDefaultResource<DataSourceBean> {

    public allLinkedConnector: JarvisDefaultLinkResource<ConnectorBean>;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        super(_configuration.ServerWithApiUrl + 'datasources', _http);
        /**
         * map linked elements
         */
        this.allLinkedConnector = new JarvisDataLinkedResource<ConnectorBean>(this.actionUrl, '/connectors', _http);
    }

}
