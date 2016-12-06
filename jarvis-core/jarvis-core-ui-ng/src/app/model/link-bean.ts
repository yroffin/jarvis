import { ResourceBean } from './resource-bean';
import { TriggerBean } from './trigger-bean';
import { ScriptBean } from './script-bean';

export class LinkBean extends ResourceBean {
    public order: string;
    public href: string;
}
