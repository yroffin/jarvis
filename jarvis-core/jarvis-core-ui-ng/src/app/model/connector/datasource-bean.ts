import { ResourceBean } from '../resource-bean';
import { MeasureBean } from './measure-bean';

export class DataSourceBean extends ResourceBean {
    public adress: string;
    public pipes: string;
    public measures: MeasureBean[];
    public body: string;
    public resultset: any[];
}
