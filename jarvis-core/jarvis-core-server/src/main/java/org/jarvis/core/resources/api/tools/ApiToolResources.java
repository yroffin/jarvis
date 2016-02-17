package org.jarvis.core.resources.api.tools;

import java.io.IOException;
import java.util.Map;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.model.bean.tools.SnapshotBean;
import org.jarvis.core.model.rest.tools.SnapshotRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * tools resources
 */
@Component
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
		/**
		 * snapshot
		 */
		declare(SNAPSHOT_RESOURCE);
	}

	@Override
	public ResourcePair doRealTask(SnapshotBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case DOWNLOAD:
				return new ResourcePair(ResultType.FILE_STREAM, download(bean, args, new GenericMap()));
			case UPLOAD:
				return new ResourcePair(ResultType.OBJECT, upload(bean, args, new GenericMap()));
			case RESTORE:
				return new ResourcePair(ResultType.STRING, restore(bean, args, new GenericMap()));
			default:
				result = new GenericMap();
		}
		return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
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
		 return "";
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
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodes);
		} catch (TechnicalHttpException e) {
			throw new TechnicalException(e);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}	
}
