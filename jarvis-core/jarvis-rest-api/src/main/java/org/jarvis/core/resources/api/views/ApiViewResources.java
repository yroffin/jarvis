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

package org.jarvis.core.resources.api.views;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.view.ViewBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.device.DeviceRest;
import org.jarvis.core.model.rest.view.ViewRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourceDefaultPostListenerImpl;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.href.ApiHrefViewResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.common.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import spark.Request;
import spark.Response;

/**
 * View resource
 */
@Component
@Api(value = "view")
@Path("/api/views")
@Produces("application/json")
@Declare(resource = ApiMapper.VIEW_RESOURCE, summary = "View resource", rest = ViewRest.class)
public class ApiViewResources extends ApiLinkedResources<ViewRest, ViewBean, DeviceRest, DeviceBean> {

	/**
	 * device
	 */
	@Autowired
	@DeclareLinkedResource(role = ApiMapper.DEVICE_RESOURCE, param = ApiMapper.DEVICE, sortKey = ApiMapper.SORTKEY)
	public ApiDeviceResources apiDeviceResources;

	/**
	 * device
	 */
	@Autowired
	@DeclareHrefResource(role = ApiMapper.DEVICE_RESOURCE, href = ApiMapper.HREF, target = DeviceRest.class)
	public ApiHrefViewResources apiHrefViewResources;

	/**
	 * constructor
	 */
	public ApiViewResources() {
		setRestClass(ViewRest.class);
		setBeanClass(ViewBean.class);
	}

	class ResourceListenerImpl extends ResourceDefaultPostListenerImpl<ViewRest, ViewBean>
			implements ResourcePostListener<ViewRest, ViewBean> {
		@Override
		public void getRest(Request request, Response response, ViewRest rest) {
		}
	}

	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(GenericMap args, TaskType taskType) {
		switch (taskType) {
		case GET:
			try {
				return new GenericValue(get(args));
			} catch (Exception e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		default:
		}
		return new GenericValue(args);
	}

	@Override
	public GenericValue doRealTask(ViewBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		switch (taskType) {
		case GET:
			try {
				return new GenericValue(get(bean, args));
			} catch (Exception e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		default:
		}
		return new GenericValue(args);
	}

	/**
	 * get all views
	 * @param args
	 * @return
	 */
	private List<ViewBean> get(GenericMap args) {
		List<ViewBean> beans = new ArrayList<ViewBean>();
		for(ViewBean view : this.doFindAllBean()) {
			try {
				if(view.ishome) {
					beans.add(get(view, args));
				}
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		}
		return beans;
	}

	/**
	 * mass get data for view (for performance issue)
	 * 
	 * @param bean
	 * @param args
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private ViewBean get(ViewBean bean, GenericMap args) throws TechnicalNotFoundException {
		bean.devices = new ArrayList<DeviceBean>();
		for (GenericEntity link : apiHrefViewResources.findAll(bean, "HREF")) {
			DeviceBean device = apiDeviceResources.doGetByIdBean(link.id);
			try {
				device.render = apiDeviceResources.render(device, args);
			} catch (Exception e) {
				throw new TechnicalException(e);
			}
			bean.devices.add(device);
		}
		return bean;
	}
}
