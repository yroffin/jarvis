import { ResourceBean } from '../resource-bean';
import { ConnectorBean } from './connector-bean';

export class DataSourceBean extends ResourceBean {
    public adress: string;
    public pipes: string;
    public connectors: ConnectorBean[];
}
