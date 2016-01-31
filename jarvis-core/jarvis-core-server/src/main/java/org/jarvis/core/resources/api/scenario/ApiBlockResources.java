/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.core.resources.api.scenario;


import java.util.List;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.profiler.TaskProfiler;
import org.jarvis.core.profiler.model.GenericNode;
import org.jarvis.core.resources.api.ApiLinkedTwiceResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockScriptPluginResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Block resource
 *
 */
@Component
public class ApiBlockResources extends ApiLinkedTwiceResources<BlockRest,BlockBean,ScriptPluginRest,ScriptPluginBean,BlockRest,BlockBean> {

	@Autowired
	ApiHrefBlockScriptPluginResources apiHrefBlockScriptPluginResources;
	
	@Autowired
	ApiHrefBlockBlockResources apiHrefBlockBlockResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	/**
	 * constructor
	 */
	public ApiBlockResources() {
		setRestClass(BlockRest.class);
		setBeanClass(BlockBean.class);
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		/**
		 * blocks
		 */
		declare(BLOCK_RESOURCE);
		/**
		 * blocks->plugins
		 * blocks->blocks
		 */
		declare(BLOCK_RESOURCE, SCRIPT_RESOURCE, apiScriptPluginResources, apiHrefBlockScriptPluginResources, PLUGIN, SORTKEY, HREF);
		declareSecond(BLOCK_RESOURCE, BLOCK_RESOURCE, this, apiHrefBlockBlockResources, BLOCK, SORTKEY, HREF);
	}

	@Override
	public String doRealTask(BlockBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case TEST:
				return test(bean, args, new GenericMap())+"";
			case EXECUTE:
				return execute(new GenericMap(), bean, args)+"";
			default:
				result = new GenericMap();
				return mapper.writeValueAsString(result);
		}
	}

	@Autowired
	PluginGroovyService pluginGroovyService;

	private boolean test(BlockBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		boolean result = true;
		for(GenericEntity cond : apiHrefBlockScriptPluginResources.findAllConditions(bean)) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(cond.id, args, TaskType.EXECUTE);
			result = result && pluginGroovyService.groovyAsBoolean(bean.expression, exec);
		}
		return result;
	}

	/**
	 * @param stack 
	 * @param bean
	 * @param args
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public GenericMap execute(GenericMap stack, BlockBean bean, GenericMap args) throws TechnicalNotFoundException {
		/**
		 * stop recursion
		 */
		if(stack.size() >= 8) {
			return stack;
		}
		GenericMap result = new GenericMap();
		for(GenericEntity cond : apiHrefBlockScriptPluginResources.findAllConditions(bean)) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(cond.id, args, TaskType.EXECUTE);
			if(pluginGroovyService.groovyAsBoolean(bean.expression, exec)) {
				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllThen(bean)) {
					GenericMap thn = (GenericMap) apiScriptPluginResources.doExecute(plugin.id, args, TaskType.EXECUTE);
				}
				for(GenericEntity subblock : apiHrefBlockBlockResources.findAllThen(bean)) {
					stack.put("run#"+stack.size(), args);
					GenericMap thn = (GenericMap) this.execute(stack, doGetByIdBean(subblock.id), args);
				}
			} else {
				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllElse(bean)) {
					GenericMap els = (GenericMap) apiScriptPluginResources.doExecute(plugin.id, args, TaskType.EXECUTE);
				}
				for(GenericEntity subblock : apiHrefBlockBlockResources.findAllElse(bean)) {
					stack.put("run#"+stack.size(), args);
					GenericMap els = (GenericMap) this.execute(stack, doGetByIdBean(subblock.id), args);
				}
			}
		}
		return result;
	}

	/**
	 * @param taskProfiler 
	 * @param level 
	 * @param startNode 
	 * @param bean
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public GenericNode render(TaskProfiler taskProfiler, int level, GenericNode startNode, BlockBean bean) throws TechnicalNotFoundException {
		/**
		 * stop recursion
		 */
		List<GenericEntity> conditions = apiHrefBlockScriptPluginResources.findAllConditions(bean);
		if(conditions.size() > 0 && level < 5) {
			/**
			 * gateway node
			 */
			GenericNode gatewayNode = taskProfiler.addBooleanGateway("test", "#TODO" + "#" + level);
			taskProfiler.addSequenceFlowSimple("start->gateway", startNode, gatewayNode);
			GenericNode end = taskProfiler.addEndNode("end", startNode.getDescription() + "#" + level);
			/**
			 * then
			 */
			List<GenericEntity> pluginsThen = apiHrefBlockScriptPluginResources.findAllThen(bean);
			List<GenericEntity> blocksThen = apiHrefBlockBlockResources.findAllThen(bean);
			if(pluginsThen.size() > 0) {
				/**
				 * then plugin
				 */
				GenericNode pluginThenNode = null;
				for(GenericEntity plugin : pluginsThen) {
					pluginThenNode = taskProfiler.addActivty("#" + plugin.id, "#" + plugin.id);
					taskProfiler.addSequenceFlowDecision("gateway-then->plugin", gatewayNode, pluginThenNode, true);
				}
				/**
				 * then block
				 */
				if(blocksThen.size()>0) {
					for(GenericEntity block : blocksThen) {
						GenericNode subStartNode = taskProfiler.addStartNode("start", block.id + "#" + (level+1));
						GenericNode subEnd = render(taskProfiler, level+1, subStartNode, doGetByIdBean(block.id));
						taskProfiler.addSequenceFlowSimple("plugin->start", pluginThenNode, subStartNode);
						taskProfiler.addSequenceFlowSimple("subEnd->end", subEnd, end);
					}
				} else {
					taskProfiler.addSequenceFlowSimple("plugin->end", pluginThenNode, end);
				}
			} else {
				/**
				 * then block
				 */
				if(blocksThen.size()>0) {
					for(GenericEntity block : blocksThen) {
						GenericNode subStartNode = taskProfiler.addStartNode("start", block.id + "#" + (level+1));
						GenericNode subEnd = render(taskProfiler, level+1, subStartNode, doGetByIdBean(block.id));
						taskProfiler.addSequenceFlowDecision("gateway->block", gatewayNode, subStartNode, true);
						taskProfiler.addSequenceFlowSimple("subEnd->end", subEnd, end);
					}
				}
			}
			/**
			 * else
			 */
			List<GenericEntity> pluginsElse = apiHrefBlockScriptPluginResources.findAllElse(bean);
			List<GenericEntity> blocksElse = apiHrefBlockBlockResources.findAllElse(bean);
			if(pluginsElse.size() > 0) {
				/**
				 * else plugin
				 */
				GenericNode pluginElseNode = null;
				for(GenericEntity plugin : pluginsElse) {
					pluginElseNode = taskProfiler.addActivty(plugin.id, "#" + plugin.id);
					taskProfiler.addSequenceFlowDecision("gateway-else->plugin", gatewayNode, pluginElseNode, false);
				}
				/**
				 * else block
				 */
				if(blocksElse.size()>0) {
					for(GenericEntity block : blocksElse) {
						GenericNode subStartNode = taskProfiler.addStartNode("start", block.id + "#" + (level+1));
						GenericNode subEnd = render(taskProfiler, level+1, subStartNode, doGetByIdBean(block.id));
						taskProfiler.addSequenceFlowSimple("plugin->start", pluginElseNode, subStartNode);
						taskProfiler.addSequenceFlowSimple("subEnd->end", subEnd, end);
					}
				} else {
					taskProfiler.addSequenceFlowSimple("plugin->end", pluginElseNode, end);
				}
			} else {
				/**
				 * then block
				 */
				if(blocksElse.size()>0) {
					for(GenericEntity block : blocksElse) {
						GenericNode subStartNode = taskProfiler.addStartNode("start", block.id + "#" + (level+1));
						GenericNode subEnd = render(taskProfiler, level+1, subStartNode, doGetByIdBean(block.id));
						taskProfiler.addSequenceFlowDecision("gateway->block", gatewayNode, subStartNode, false);
						taskProfiler.addSequenceFlowSimple("subEnd->end", subEnd, end);
					}
				}
			}
			return end;
		} else {
			GenericNode end = taskProfiler.addEndNode("end", startNode.getDescription() + "#" + level);
			taskProfiler.addSequenceFlowSimple("subEnd->end", startNode, end);
			return end;
		}
	}
}
