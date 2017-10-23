/**
 *  Copyright 2017 Yannick Roffin
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

package org.jarvis.core.resources.api.process;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.common.jersey.AbstractJerseyClient;
import org.jarvis.core.model.bean.process.ProcessBean;
import org.jarvis.core.model.bean.trigger.TriggerBean;
import org.jarvis.core.model.rest.process.ProcessRest;
import org.jarvis.core.model.rest.trigger.TriggerRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefProcessTriggerResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.swagger.annotations.Api;

/**
 * Block resource
 */
@Api(value = "process")
@Path("/api/processes")
@Produces("application/json")
@Component
@Declare(resource = ApiMapper.PROCESS_RESOURCE, summary = "Process resource", rest = ProcessRest.class)
public class ApiProcessResources extends ApiLinkedResources<ProcessRest, ProcessBean, TriggerRest,TriggerBean> {
	protected Logger logger = LoggerFactory.getLogger(ApiProcessResources.class);

	/**
	 * trigger
	 */
	@Autowired
	@DeclareLinkedResource(role=ApiMapper.TRIGGER_RESOURCE, param=ApiMapper.TRIGGER, sortKey=ApiMapper.SORTKEY)
	public ApiTriggerResources apiTriggerResources;

	/**
	 * href trigger
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.TRIGGER_RESOURCE, href=ApiMapper.HREF, target=TriggerRest.class)
	public ApiHrefProcessTriggerResources apiHrefProcessTriggerResources;

	@Autowired
	Environment env;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	RepositoryService repositoryService;

	private BpmnRestClient bpmnRestClient;
	
	/**
	 * xml manipulation
	 */
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	final XPathFactory xpf = XPathFactory.newInstance();

	private class BpmnRestClient extends AbstractJerseyClient {
		/**
		 * constructor
		 * 
		 * @param baseurl
		 * @param user
		 * @param password
		 * @param connect
		 * @param read
		 */
		public BpmnRestClient(String baseurl, String user, String password, String connect, String read) {
			/**
			 * initialize
			 */
			initialize(baseurl, user, password, connect, read);
		}
	}

	/**
	 * constructor
	 */
	public ApiProcessResources() {
		setRestClass(ProcessRest.class);
		setBeanClass(ProcessBean.class);
	}

	@PostConstruct
	@Override
	protected void init() {
		super.init();
		/**
		 * internal camunda engine
		 */
		this.bpmnRestClient = new BpmnRestClient(env.getProperty("jarvis.bpm.url"), env.getProperty("jarvis.bpm.user"),
				env.getProperty("jarvis.bpm.password"), env.getProperty("jarvis.bpm.timeout.connect", "2000"),
				env.getProperty("jarvis.bpm.timeout.read", "2000"));
	}

	@Override
	public GenericValue doRealTask(ProcessBean bean, GenericMap args, String taskType) throws TechnicalException {
		switch (taskType) {
		case "deploy":
			try {
				return deploy(bean);
			} catch (UnsupportedEncodingException e) {
				throw new TechnicalException(e);
			} catch (TechnicalNotFoundException e) {
				throw new TechnicalException(e);
			}
		case "execute":
			return execute(bean, args);
		case "api":
			return api(bean, args);
		default:
			return new GenericValue("{}");
		}
	}

	/**
	 * load from xml string
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private static Element loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    Document document = builder.parse(is);
	    Element element = document.getDocumentElement();
	    return element;
	}

	/**
	 * deploy this process
	 * 
	 * @param bean
	 * @return GenericValue
	 * @throws UnsupportedEncodingException
	 * @throws TechnicalNotFoundException
	 */
	public GenericValue deploy(ProcessBean bean) throws UnsupportedEncodingException, TechnicalNotFoundException {
		/**
		 * deploy this process
		 */
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().enableDuplicateFiltering(true)
				.source("internal").name(bean.name);
		deploymentBuilder.addString(bean.name + ".bpmn", bean.bpmn);
		Deployment res = deploymentBuilder.deploy();

		logger.warn("Deployment of {} successfull", bean.name);

		try {
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			Element root = loadXMLFromString(bean.bpmn);
			XPath path = xpf.newXPath();
			Node process = (Node) path.evaluate("/definitions/process[1]", root, XPathConstants.NODE);
			bean.bpmnId = process.getAttributes().getNamedItem("id").getTextContent();
			bean.name = process.getAttributes().getNamedItem("name").getTextContent();
		} catch (final ParserConfigurationException e) {
			throw new TechnicalException(e);
		} catch (SAXException e) {
			throw new TechnicalException(e);
		} catch (IOException e) {
			throw new TechnicalException(e);
		} catch (Exception e) {
			throw new TechnicalException(e);
		}

		/**
		 * update it
		 */
		bean.deploymentTime = new DateTime(res.getDeploymentTime());
		this.doUpdateBean(bean.id, bean);

		return new GenericValue(bean);
	}

	/**
	 * start a process by its name
	 * 
	 * @param bean
	 * @return GenericValue
	 */
	private GenericValue execute(ProcessBean bean, GenericMap args) {
		ProcessInstance process = runtimeService.startProcessInstanceByKey(bean.bpmnId);
		logger.warn("Start of {} successfull", process.getProcessDefinitionId());
		return new GenericValue(bean);
	}

	/**
	 * deploy this process
	 * 
	 * @param bean
	 * @return GenericValue
	 */
	private GenericValue api(ProcessBean bean, GenericMap args) {
		/**
		 * build call
		 */
		String entity = bpmnRestClient.target().path("api/engine/engine/default" + args.get("path"))
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).acceptEncoding("charset=UTF-8")
				.get(String.class);
		return new GenericValue(entity);
	}
}
