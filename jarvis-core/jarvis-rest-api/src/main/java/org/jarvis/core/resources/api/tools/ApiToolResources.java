package org.jarvis.core.resources.api.tools;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.model.bean.tools.SnapshotBean;
import org.jarvis.core.model.rest.tools.SnapshotRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;

/**
 * tools resources
 */
@Api(value = "snapshot")
@Path("/api/snapshots")
@Produces("application/json")
@Component
@Declare(resource=ApiMapper.SNAPSHOT_RESOURCE, summary="Snapshot resource", rest=SnapshotRest.class)
public class ApiToolResources extends ApiResources<SnapshotRest,SnapshotBean> {

	/**
	 * constructor
	 */
	public ApiToolResources() {
		setRestClass(SnapshotRest.class);
		setBeanClass(SnapshotBean.class);
	}

	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(SnapshotBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result;
		switch(taskType) {
			case DOWNLOAD:
				return new GenericValue(ResultType.FILE_STREAM, download(bean, args, new GenericMap()));
			case UPLOAD:
				return new GenericValue(upload(bean, args, new GenericMap()));
			case RESTORE:
				return new GenericValue(restore(bean, args, new GenericMap()));
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * restore task
	 * 
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 */
	private String restore(SnapshotBean bean, GenericMap args, GenericMap genericMap) {
		 try {
			GenericMap repository = mapper.readValue(bean.json, GenericMap.class);
			apiNeo4Service.restore(repository);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		 return "{}";
	}

	/**
	 * upload task
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 */
	private String upload(SnapshotBean bean, GenericMap args, GenericMap genericMap) {
		bean.json = (String) args.get("multipart/form-data");
		try {
			return  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * download task
	 * 
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 */
	private String download(SnapshotBean bean, GenericMap args, GenericMap genericMap) {
		try {
			Map<String, Map<String, GenericMap>> nodes = apiNeo4Service.findAllNodes();
			/**
			 * each node must be secured according its security level
			 */
			for(Entry<String, Map<String, GenericMap>> entry : nodes.entrySet()) {
				for(Entry<String, GenericMap> subEntry : entry.getValue().entrySet()) {
					if(subEntry.getValue().get("isSecured") != null) {
						if((boolean) subEntry.getValue().get("isSecured")) {
							secure(entry.getKey(), subEntry.getValue());
						}
					}
				}
			}
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodes);
		} catch (TechnicalHttpException e) {
			throw new TechnicalException(e);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	private void secure(String key, GenericMap value) {
		/**
		 * protect property
		 */
		if(key.equals("PropertyBean")) {
			value.put("value", "N/A");
		}		
	}	
}
