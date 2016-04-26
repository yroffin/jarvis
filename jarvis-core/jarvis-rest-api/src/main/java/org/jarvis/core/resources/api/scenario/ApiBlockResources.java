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
import java.util.Map;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.profiler.DefaultProcessService;
import org.jarvis.core.profiler.model.DefaultProcess;
import org.jarvis.core.profiler.model.GenericNode;
import org.jarvis.core.resources.api.ApiLinkedTwiceResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.resources.api.href.ApiHrefBlockBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockScriptPluginResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
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
	public ResourcePair doRealTask(BlockBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case TEST:
				return new ResourcePair(ResultType.OBJECT, test(bean, args, new GenericMap())+"");
			case EXECUTE:
				return new ResourcePair(ResultType.OBJECT, execute(new GenericMap(), bean, args)+"");
			default:
				result = new GenericMap();
				return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
		}
	}

	@Autowired
	PluginGroovyService pluginGroovyService;

	private boolean test(BlockBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		boolean result = true;
		for(GenericEntity cond : apiHrefBlockScriptPluginResources.findAllConditions(bean)) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(null,cond.id, args, TaskType.EXECUTE);
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
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(null,cond.id, args, TaskType.EXECUTE);
			if(pluginGroovyService.groovyAsBoolean(bean.expression, exec)) {
				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllThen(bean)) {
					GenericMap thn = (GenericMap) apiScriptPluginResources.doExecute(null,plugin.id, args, TaskType.EXECUTE);
				}
				for(GenericEntity subblock : apiHrefBlockBlockResources.findAllThen(bean)) {
					stack.put("run#"+stack.size(), args);
					GenericMap thn = (GenericMap) this.execute(stack, doGetByIdBean(subblock.id), args);
				}
			} else {
				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllElse(bean)) {
					GenericMap els = (GenericMap) apiScriptPluginResources.doExecute(null,plugin.id, args, TaskType.EXECUTE);
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
	 * @param process 
	 * @param currentBlock 
	 * @param calls 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public Map<String, GenericEntity> render(DefaultProcess process, BlockBean currentBlock, Map<String, GenericEntity> calls) throws TechnicalNotFoundException {
		/**
		 * create start node
		 */
		GenericNode startNode = DefaultProcessService.addStartNode(process, currentBlock.name, "start#" + currentBlock.id);

		/**
		 * stop recursion
		 */
		List<GenericEntity> conditions = apiHrefBlockScriptPluginResources.findAllConditions(currentBlock);
		if(conditions.size() > 0) {
			/**
			 * find condition
			 */
			ScriptPluginRest conditionScript = apiScriptPluginResources.doGetById(conditions.get(0).id);
			GenericNode pluginNode = DefaultProcessService.addActivity(process, conditionScript.name, "activity#" + conditionScript.id);
			DefaultProcessService.addSequenceFlowSimple(process, startNode, pluginNode);
			/**
			 * gateway node
			 */
			GenericNode gatewayNode = DefaultProcessService.addBooleanGateway(process, currentBlock.expression, "gateway#" + currentBlock.id);
			DefaultProcessService.addSequenceFlowSimple(process, pluginNode, gatewayNode);
			/**
			 * end node
			 */
			GenericNode end = DefaultProcessService.addEndNode(process, currentBlock.name, "end#" + currentBlock.id);
			/**
			 * then
			 */
			List<GenericEntity> pluginsThen = apiHrefBlockScriptPluginResources.findAllThen(currentBlock);
			List<GenericEntity> blocksThen = apiHrefBlockBlockResources.findAllThen(currentBlock);
			if(pluginsThen.size() > 0) {
				/**
				 * then plugin
				 */
				GenericNode pluginThenNode = null;
				for(GenericEntity plugin : pluginsThen) {
					ScriptPluginRest pluginScript = apiScriptPluginResources.doGetById(plugin.id);
					pluginThenNode = DefaultProcessService.addActivity(process, pluginScript.name, "activity#" + pluginScript.id);
					DefaultProcessService.addSequenceFlowDecision(process, "gateway-then->plugin", gatewayNode, pluginThenNode, true);
				}
				/**
				 * then block
				 */
				if(blocksThen.size()>0) {
					for(GenericEntity block : blocksThen) {
						BlockRest blockBean = doGetById(block.id);
						GenericNode subroutine = DefaultProcessService.addCallActivty(process, "call " + blockBean.name, "subroutine#" + block.id);
						calls.put(block.id, block);
						DefaultProcessService.addSequenceFlowSimple(process, pluginThenNode, subroutine);
						DefaultProcessService.addSequenceFlowSimple(process, subroutine, end);
					}
				} else {
					DefaultProcessService.addSequenceFlowSimple(process, pluginThenNode, end);
				}
			} else {
				/**
				 * then block
				 */
				if(blocksThen.size()>0) {
					for(GenericEntity block : blocksThen) {
						GenericNode subroutine = DefaultProcessService.addCallActivty(process, block.id, "subroutine#" + block.id);
						calls.put(block.id, block);
						DefaultProcessService.addSequenceFlowDecision(process, "gateway-then->block", gatewayNode, subroutine, true);
						DefaultProcessService.addSequenceFlowSimple(process, subroutine, end);
					}
				}
			}
			/**
			 * else
			 */
			List<GenericEntity> pluginsElse = apiHrefBlockScriptPluginResources.findAllElse(currentBlock);
			List<GenericEntity> blocksElse = apiHrefBlockBlockResources.findAllElse(currentBlock);
			if(pluginsElse.size() > 0) {
				/**
				 * else plugin
				 */
				GenericNode pluginElseNode = null;
				for(GenericEntity plugin : pluginsElse) {
					ScriptPluginRest pluginScript = apiScriptPluginResources.doGetById(plugin.id);
					pluginElseNode = DefaultProcessService.addActivity(process, pluginScript.name, "activity#" + pluginScript.id);
					DefaultProcessService.addSequenceFlowDecision(process, "gateway-else->plugin", gatewayNode, pluginElseNode, false);
				}
				/**
				 * else block
				 */
				if(blocksElse.size()>0) {
					for(GenericEntity block : blocksElse) {
						BlockRest blockBean = doGetById(block.id);
						GenericNode subroutine = DefaultProcessService.addCallActivty(process, "call " + blockBean.name, "subroutine#" + block.id);
						calls.put(block.id, block);
						DefaultProcessService.addSequenceFlowSimple(process, pluginElseNode, subroutine);
						DefaultProcessService.addSequenceFlowSimple(process, subroutine, end);
					}
				} else {
					DefaultProcessService.addSequenceFlowSimple(process, pluginElseNode, end);
				}
			} else {
				/**
				 * then block
				 */
				if(blocksElse.size()>0) {
					for(GenericEntity block : blocksElse) {
						GenericNode subroutine = DefaultProcessService.addCallActivty(process, block.id, "subroutine#" + block.id);
						calls.put(block.id, block);
						DefaultProcessService.addSequenceFlowDecision(process, "gateway-else->block", gatewayNode, subroutine, true);
						DefaultProcessService.addSequenceFlowSimple(process, subroutine, end);
					}
				}
			}
			/**
			 * no then neither else
			 */
			if(pluginsThen.size() == 0 && pluginsElse.size() == 0) {
				DefaultProcessService.addSequenceFlowSimple(process, pluginNode, end);
			}
			return calls;
		} else {
			GenericNode end = DefaultProcessService.addEndNode(process, currentBlock.name, "end#" + currentBlock.id);
			DefaultProcessService.addSequenceFlowSimple(process, startNode, end);
			return calls;
		}
	}
}
