import { ResourceBean } from './resource-bean';
import { TriggerBean } from './trigger-bean';
import { PluginScriptBean } from './plugin-bean';

export class DeviceBean extends ResourceBean {
    public template: string;
    public tagColor: string;
    public render: any;
    public triggers: TriggerBean[];
    public devices: DeviceBean[];
    public plugins: PluginScriptBean[];
}
