import { ResourceBean } from './../resource-bean';
import { TriggerBean } from './../trigger-bean';

export class ProcessBean extends ResourceBean {
    triggers: TriggerBean[];
    deploymentTime: Date;
    bpmId: string;
    bpm: string;
}
