import { ResourceBean } from './resource-bean';
import { TriggerBean } from './trigger-bean';
import { ScriptBean } from './script-bean';

export class DeviceBean extends ResourceBean {
    public template: string;
    public render: any;
    public triggers: TriggerBean[];
    public devices: DeviceBean[];
    public plugins: ScriptBean[];
}
