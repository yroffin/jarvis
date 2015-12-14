package org.jarvis.core.services;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.model.rest.JobRest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:server.properties")
public class ApiJobService implements GenericService<JobRest> {

	public List<JobRest> findAll() {
		return new ArrayList<JobRest>();
	}

	public JobRest getById(String params) {
		return new JobRest();
	}

	public JobRest create(JobRest jobRest) {
		return jobRest;
	}

	public JobRest update(String params, JobRest jobRest) {
		return jobRest;
	}

}
