import { ResourceBean } from './resource-bean';
import { PluginBean } from './plugin-bean';

export class BlockBean extends ResourceBean {
    /**
     * conditionnal blocks
     */
    blocks: BlockBean[];
    conditionnalPlugins: PluginBean[];
}
