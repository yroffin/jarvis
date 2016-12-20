import { ResourceBean } from './resource-bean';
import { TriggerBean } from './trigger-bean';
import { BlockBean } from './block-bean';

export class ScenarioBean extends ResourceBean {
    triggers: TriggerBean[];
    blocks: BlockBean[];
}
