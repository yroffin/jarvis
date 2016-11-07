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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jarvis.core.exception.TechnicalException;
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
import org.jarvis.core.resources.api.GenericValue;
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
	public GenericValue doRealTask(BlockBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		switch(taskType) {
			case TEST:
			{
				/**
				 * in execute mode args are stored in parameter
				 */
				GenericMap testParameter = null;
				try {
					if(bean.testParameter != null) {
						testParameter = mapper.readValue(bean.testParameter, GenericMap.class);
					} else {
						testParameter = new GenericMap();
					}
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
				Object testResult;
				try {
					testResult = test(bean, testParameter, new GenericMap());
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				return new GenericValue(testResult+"");
			}
			case EXECUTE:
			{
				/**
				 * in execute mode args are stored in parameter
				 */
				GenericMap testParameter = null;
				try {
					if(bean.testParameter != null) {
						testParameter = mapper.readValue(bean.testParameter, GenericMap.class);
					} else {
						testParameter = new GenericMap();
					}
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
				Object executeResult;
				try {
					executeResult = execute(new ArrayList<String>(), 0, bean, testParameter);
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				return new GenericValue(executeResult+"");
			}
			default:
				return new GenericValue(new GenericMap());
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
	 * execute this block (and subblock)
	 * @param console 
	 * @param level 
	 * @param bean
	 * @param testParameter
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public boolean execute(List<String> console, int level, BlockBean bean, GenericMap testParameter) throws TechnicalNotFoundException {
		/**
		 * stop recursion
		 */
		if(level >= 16) {
			throw new TechnicalException("Stack overflow");
		}

		/**
		 * iterate on conditions
		 */
		boolean result = true;
		for(GenericEntity cond : apiHrefBlockScriptPluginResources.findAllConditions(bean)) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(null,cond.id, testParameter, TaskType.EXECUTE);
			boolean evaluate = pluginGroovyService.groovyAsBoolean(bean.expression, exec);
			logger.info("{} Block {} - Evaluate {} with context {} = {}", level, bean.id, bean.expression, exec, evaluate);
			console.add(MessageFormat.format("{0} Block {1} - Evaluate {2} with context {3} = {4}", level, bean.id, bean.expression, exec, evaluate).toString());
			result = result && evaluate;
			if(evaluate) {
				/**
				 * then parameter
				 */
				GenericMap thenParameter = null;
				try {
					if(bean.thenParameter != null) {
						thenParameter = mapper.readValue(bean.thenParameter, GenericMap.class);
					} else {
						thenParameter = new GenericMap();
					}
				} catch (IOException e) {
					throw new TechnicalException(e);
				}

				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllThen(bean)) {
					Object subResult = apiScriptPluginResources.doExecute(null,plugin.id, thenParameter, TaskType.EXECUTE);
					logger.info("{} Block {} - Then plugin {} = {}", level, bean.id, plugin.id, subResult);
					console.add(MessageFormat.format("{0} Block {1} - Then plugin {2} = {3}", level, bean.id, plugin.id, subResult).toString());
				}
				for(GenericEntity subblock : apiHrefBlockBlockResources.findAllThen(bean)) {
					boolean subResult = this.execute(console, level + 1, doGetByIdBean(subblock.id), thenParameter);
					result = result && subResult;
					logger.info("{} Block {} - Then block {} = {}", level, bean.id, subblock.id, subResult);
					console.add(MessageFormat.format("{0} Block {1} - Then block {2} = {3}", level, bean.id, subblock.id, subResult).toString());
				}
			} else {
				/**
				 * else parameter
				 */
				GenericMap elseParameter = null;
				try {
					if(bean.elseParameter != null) {
						elseParameter = mapper.readValue(bean.elseParameter, GenericMap.class);
					} else {
						elseParameter = new GenericMap();
					}
				} catch (IOException e) {
					throw new TechnicalException(e);
				}

				for(GenericEntity plugin : apiHrefBlockScriptPluginResources.findAllElse(bean)) {
					Object subResult = apiScriptPluginResources.doExecute(null,plugin.id, elseParameter, TaskType.EXECUTE);
					logger.info("{} Block {} - Else plugin {} = {}", level, bean.id, plugin.id, subResult);
					console.add(MessageFormat.format("{0} Block {1} - Else plugin {2} = {3}", level, bean.id, plugin.id, subResult).toString());
				}
				for(GenericEntity subblock : apiHrefBlockBlockResources.findAllElse(bean)) {
					boolean subResult = this.execute(console, level + 1, doGetByIdBean(subblock.id), elseParameter);
					result = result && subResult;
					logger.info("{} Block {} - Else block {} = {}", level, bean.id, subblock.id, subResult);
					console.add(MessageFormat.format("{0} Block {1} - Else block {2} = {3}", level, bean.id, subblock.id, subResult).toString());
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
			ScriptPluginBean conditionScript = apiScriptPluginResources.doGetByIdBean(conditions.get(0).id);
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
					ScriptPluginBean pluginScript = apiScriptPluginResources.doGetByIdBean(plugin.id);
					pluginThenNode = DefaultProcessService.addActivity(process, pluginScript.name, "activity#" + pluginScript.id);
					DefaultProcessService.addSequenceFlowDecision(process, "gateway-then->plugin", gatewayNode, pluginThenNode, true);
				}
				/**
				 * then block
				 */
				if(blocksThen.size()>0) {
					for(GenericEntity block : blocksThen) {
						BlockBean blockBean = doGetByIdBean(block.id);
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
					ScriptPluginBean pluginScript = apiScriptPluginResources.doGetByIdBean(plugin.id);
					pluginElseNode = DefaultProcessService.addActivity(process, pluginScript.name, "activity#" + pluginScript.id);
					DefaultProcessService.addSequenceFlowDecision(process, "gateway-else->plugin", gatewayNode, pluginElseNode, false);
				}
				/**
				 * else block
				 */
				if(blocksElse.size()>0) {
					for(GenericEntity block : blocksElse) {
						BlockBean blockBean = doGetByIdBean(block.id);
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

	/**
	 * @param level 
	 * @param stage 
	 * @param currentBlock 
	 * @throws TechnicalNotFoundException 
	 */
	public void renderActivity(Integer level, StringBuilder stage, BlockBean currentBlock) throws TechnicalNotFoundException {
		/**
		 * create start node
		 */
		DefaultProcessService.addCall(stage, currentBlock.name, "|");
		DefaultProcessService.addNote(stage, "right", "//id " + currentBlock.id + " level " + level + "//\n====\nExecute block "+ currentBlock.name  + "\n");

		/**
		 * stop recursion
		 */
		List<GenericEntity> conditions = apiHrefBlockScriptPluginResources.findAllConditions(currentBlock);
		if(conditions.size() > 0) {
			/**
			 * find condition
			 */
			ScriptPluginBean conditionScript = apiScriptPluginResources.doGetByIdBean(conditions.get(0).id);
			DefaultProcessService.addCall(stage, conditionScript.name, "]");
			DefaultProcessService.addNote(stage, "left", "//id " + conditionScript.id + "//\n====\nCondition computing "+ conditionScript.name  + "\n");
			/**
			 * generate if then
			 */
			DefaultProcessService.addIf(stage, "//id " + currentBlock.id + "//\n====\nCondition is <b>"+ currentBlock.expression  + "</b>\n", "true");
			List<GenericEntity> pluginsThen = apiHrefBlockScriptPluginResources.findAllThen(currentBlock);
			List<GenericEntity> blocksThen = apiHrefBlockBlockResources.findAllThen(currentBlock);
			/**
			 * then plugin
			 */
			if(pluginsThen.size() > 0) {
				for(GenericEntity plugin : pluginsThen) {
					ScriptPluginBean pluginScript = apiScriptPluginResources.doGetByIdBean(plugin.id);
					DefaultProcessService.addCall(stage, pluginScript.name, "|");
					DefaultProcessService.addNote(stage, "right", "//id " + pluginScript.id + "//\n====\nPlugin execution "+ pluginScript.name  + "\n");
				}
			}
			/**
			 * then block
			 */
			if(blocksThen.size()>0) {
				for(GenericEntity block : blocksThen) {
					BlockBean blockBean = doGetByIdBean(block.id);
					renderActivity(level + 1, stage, blockBean);
				}
			}
			/**
			 * else
			 */
			List<GenericEntity> pluginsElse = apiHrefBlockScriptPluginResources.findAllElse(currentBlock);
			List<GenericEntity> blocksElse = apiHrefBlockBlockResources.findAllElse(currentBlock);
			/**
			 * generate else
			 */
			if(pluginsElse.size() > 0 || blocksElse.size()>0) {
				DefaultProcessService.addElse(stage, "//id " + currentBlock.id + "//\n====\nCondition is "+ currentBlock.expression  + "\n", "false");
			}
			/**
			 * else plugin
			 */
			if(pluginsElse.size() > 0) {
				for(GenericEntity plugin : pluginsElse) {
					ScriptPluginBean pluginScript = apiScriptPluginResources.doGetByIdBean(plugin.id);
					DefaultProcessService.addCall(stage, pluginScript.name, "|");
					DefaultProcessService.addNote(stage, "left", "//id " + pluginScript.id + "//\n====\nPlugin execution "+ pluginScript.name  + "\n");
				}
			}
			/**
			 * else block
			 */
			if(blocksElse.size()>0) {
				for(GenericEntity block : blocksElse) {
					BlockBean blockBean = doGetByIdBean(block.id);
					renderActivity(level + 1, stage, blockBean);
				}
			}
			DefaultProcessService.addEndIf(stage);
		}
	}
}
