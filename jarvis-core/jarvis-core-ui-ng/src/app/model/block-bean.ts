import { ResourceBean } from './resource-bean';
import { PluginBean } from './plugin-bean';

export class BlockBean extends ResourceBean {
    /**
     * conditionnal blocks
     */
    thenBlocks: BlockBean[];
    elseBlocks: BlockBean[];
    /**
     * conditionnal plugins
     */
    conditionnalPlugins: PluginBean[];
    thenPlugins: PluginBean[];
    elsePlugins: PluginBean[];
}
