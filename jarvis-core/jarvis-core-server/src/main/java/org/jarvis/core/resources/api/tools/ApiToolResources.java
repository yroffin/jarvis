package org.jarvis.core.resources.api.tools;

import java.util.Map;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.model.bean.tools.SnapshotBean;
import org.jarvis.core.model.rest.tools.SnapshotRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.ResourcePreListener;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import spark.Request;
import spark.Response;

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

	class ResourceListenerImpl implements ResourcePreListener<SnapshotRest> {

		@Override
		public void post(Request request, Response response, SnapshotRest snapshot) {
			inject(snapshot);
		}
		
		@Override
		public void put(Request request, Response response, SnapshotRest snapshot) {
			inject(snapshot);
		}

		public void inject(SnapshotRest snapshot) {
			try {
				Map<String, Map<String, GenericMap>> nodes = apiNeo4Service.findAllNodes();
				snapshot.json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodes);
			} catch (TechnicalHttpException e) {
				throw new TechnicalException(e);
			} catch (JsonProcessingException e) {
				throw new TechnicalException(e);
			}
		}
	}

	@Override
	public void mount() {
		/**
		 * snapshot
		 */
		declare(SNAPSHOT_RESOURCE);
		/**
		 * declare listener
		 */
		addListener(new ResourceListenerImpl());
	}

	@Override
	public String doRealTask(SnapshotBean bean, GenericMap args, TaskType taskType) throws Exception {
		/**
		 * TODO
		 */
		return "";
	}	
}
