import { ResourceBean } from './../resource-bean';
import { ConnectorBean } from './connector-bean';

export class MeasureBean extends ResourceBean {
    name: string
    datetime: string
    value: string
    connectors: ConnectorBean[]
}
