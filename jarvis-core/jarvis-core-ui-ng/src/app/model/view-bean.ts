import { ResourceBean } from './resource-bean';
import { DeviceBean } from './device-bean';

export class ViewBean extends ResourceBean {
    public devices: DeviceBean[];
}
