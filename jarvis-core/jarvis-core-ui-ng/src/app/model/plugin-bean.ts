import { ResourceBean } from './resource-bean';
import { CommandBean } from './command-bean';
import { DeviceBean } from './device-bean';

export class PluginBean extends ResourceBean {
    public commands: CommandBean[];
    public devices: DeviceBean[];
}

export class PluginScriptBean extends PluginBean {
    public id: string
}
