package org.jarvis.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.resources.api.ApiDefaultResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.type.GenericMap;
import org.reflections.Reflections;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;

/**
 * swagger init
 */
public class SwaggerParser {

	/**
	 * retrieve
	 * 
	 * @param packageName
	 * @return String
	 */
	public static String getSwaggerJson(String packageName) {
		Swagger swagger = getSwagger(packageName);
		String json;
		try {
			json = swaggerToJson(swagger);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		return json;
	}

	/**
	 * scan package
	 * 
	 * @param packageName
	 * @return Swagger
	 */
	public static Swagger getSwagger(String packageName) {
		Reflections reflections = new Reflections(packageName);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setResourcePackage(packageName);
		beanConfig.setScan(true);
		beanConfig.scanAndRead();
		Swagger swagger = beanConfig.getSwagger();

		Reader reader = new Reader(swagger);

		Set<Class<?>> apiClasses = reflections.getTypesAnnotatedWith(Api.class);
		reader.read(apiClasses);

		jarvisExtension(swagger, apiClasses);

		return swagger;
	}

	private static void jarvisExtensionScan(Swagger swagger, Class<ApiDefaultResources> resource) {
		swagger(swagger, resource);
		swaggerLink(swagger, resource);
	}

	/**
	 * load ressources in swagger definition
	 * 
	 * @param swagger
	 * @param klass
	 */
	private static void swagger(Swagger swagger, Class<ApiDefaultResources> klass) {
		Api apiAnnotation = klass.getAnnotationsByType(Api.class)[0];
		Declare declareAnnotation = klass.getAnnotationsByType(Declare.class)[0];

		/**
		 * add rest definitions
		 */
		ModelRest model = new ModelRest(declareAnnotation.rest());
		swagger.addDefinition(declareAnnotation.rest().getCanonicalName(), model);
		ModelRest genmap = new ModelRest(GenericMap.class);
		swagger.addDefinition(GenericMap.class.getCanonicalName(), genmap);

		BodyParameter body = null;
		body = new BodyParameter();
		body.schema(model);
		body.setName("body");
		body.setDescription("Resource body with object " + declareAnnotation.rest().getCanonicalName());

		BodyParameter genbody = null;
		genbody = new BodyParameter();
		genbody.schema(genmap);
		genbody.setName("map");
		genbody.setDescription("Resource body with generic map");

		SerializableParameter serializableParameter = null;
		List<SerializableParameter> serializableParameters = null;
		Map<Integer, Response> responses = null;

		/**
		 * get /api/<resource>
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new ArrayProperty(new RefProperty(declareAnnotation.rest().getCanonicalName())));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource(), "get",
				"Retrieve a collection of " + apiAnnotation.value(), serializableParameters, null, responses);

		/**
		 * get /api/<resource>/{id}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}", "get",
				"Retrieve a " + apiAnnotation.value(), serializableParameters, null, responses);

		/**
		 * post /api/<resource>
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource(), "post",
				"Create a new " + apiAnnotation.value(), serializableParameters, body, responses);

		/**
		 * post /api/<resource>/{id}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new QueryParameter();
		serializableParameter.setName("task");
		serializableParameter.setDescription("Task : execute ...");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(GenericMap.class.getCanonicalName()));
		responses.put(403, new Response().description("Forbidden"));
		responses.put(404, new Response().description("Not found"));
		responses.put(405, new Response().description("Method Not Allowed"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}", "post",
				"Apply task on " + apiAnnotation.value(), serializableParameters, genbody, responses);

		/**
		 * put /api/<resource>/{id}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(403, new Response().description("Forbidden"));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}", "put",
				"Update a " + apiAnnotation.value(), serializableParameters, body, responses);

		/**
		 * delete /api/<resource>/{id}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(403, new Response().description("Forbidden"));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}", "delete",
				"Delete a " + apiAnnotation.value(), serializableParameters, null, responses);
	}

	private static void swaggerLink(Swagger swagger, Class<ApiDefaultResources> klass) {
		/**
		 * scan all fields related to link
		 */
		Set<Field> declareLinkedResources = new HashSet<Field>();
		for (Field field : klass.getDeclaredFields()) {
			for (@SuppressWarnings("unused")
			DeclareLinkedResource declareLinkedResource : field
					.getDeclaredAnnotationsByType(DeclareLinkedResource.class)) {
				declareLinkedResources.add(field);
			}
			for (@SuppressWarnings("unused")
			DeclareHrefResource declareHrefResource : field.getDeclaredAnnotationsByType(DeclareHrefResource.class)) {
				declareLinkedResources.add(field);
			}
		}

		/**
		 * find all linked resource
		 */
		declareLinkedResources.forEach(new Consumer<Field>() {
			@Override
			public void accept(Field linked) {
				/**
				 * find all linked resource
				 */
				for (DeclareLinkedResource declareLinkedResource : linked
						.getDeclaredAnnotationsByType(DeclareLinkedResource.class)) {
					declareLinkedResources.forEach(new Consumer<Field>() {
						@Override
						public void accept(Field href) {
							/**
							 * first find HREF
							 */
							for (DeclareHrefResource declareHrefResource : href
									.getDeclaredAnnotationsByType(DeclareHrefResource.class)) {
								if (declareHrefResource.role().equals(declareLinkedResource.role())) {
									declareLink(swagger, klass, declareLinkedResource, declareHrefResource, linked,
											href);
								}
							}
						}
					});
				}
			}
		});
	}

	/**
	 * declare all link
	 * 
	 * @param swagger
	 * @param klass
	 * @param declareLinkedResource
	 * @param declareHrefResource
	 * @param href
	 * @param linked
	 */
	private static void declareLink(Swagger swagger, Class<ApiDefaultResources> klass,
			DeclareLinkedResource declareLinkedResource, DeclareHrefResource declareHrefResource, Field linked,
			Field href) {
		Declare declareAnnotation = klass.getAnnotationsByType(Declare.class)[0];
		SerializableParameter serializableParameter = null;
		List<SerializableParameter> serializableParameters = null;
		Map<Integer, Response> responses = null;

		ModelRest model = new ModelRest(declareHrefResource.target());
		swagger.addDefinition(declareHrefResource.target().getCanonicalName(), model);

		BodyParameter body = null;
		body = new BodyParameter();
		body.schema(model);
		body.setName("body");
		body.setDescription("Resource body with object " + GenericMap.class.getCanonicalName());

		/**
		 * get /api/<resource>/{id}/<linked>
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new ArrayProperty(new RefProperty(declareHrefResource.target().getCanonicalName())));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}/" + declareLinkedResource.role(),
				"get", "Retrieve all linked " + declareHrefResource.role(), serializableParameters, null, responses);

		/**
		 * post /api/<resource>/{id}/<linked>/{linkId}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new PathParameter();
		serializableParameter.setName("linkId");
		serializableParameter.setDescription("Resource linked id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}/" + declareLinkedResource.role() + "/{linkId}", "post",
				"Create a new linked " + declareHrefResource.role(), serializableParameters, body, responses);

		/**
		 * put /api/<resource>/{id}/<linked>/{linkId}/{instanceId}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new PathParameter();
		serializableParameter.setName("linkId");
		serializableParameter.setDescription("Resource linked id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new QueryParameter();
		serializableParameter.setName("instance");
		serializableParameter.setDescription("Resource linked instance id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(403, new Response().description("Forbidden"));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}/" + declareLinkedResource.role() + "/{linkId}", "put",
				"Update a linked " + declareHrefResource.role(), serializableParameters, body, responses);

		/**
		 * delete /api/<resource>/{id}/<linked>/{linkId}?instance={instance}
		 */
		serializableParameters = new ArrayList<SerializableParameter>();
		serializableParameter = new PathParameter();
		serializableParameter.setName("id");
		serializableParameter.setDescription("Resource id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new PathParameter();
		serializableParameter.setName("linkId");
		serializableParameter.setDescription("Resource linked id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		serializableParameter = new QueryParameter();
		serializableParameter.setName("instance");
		serializableParameter.setDescription("Resource linked instance id");
		serializableParameter.setType("string");
		serializableParameters.add(serializableParameter);
		responses = new HashMap<Integer, Response>();
		responses.put(200, new Response().description("Success"));
		responses.get(200).setSchema(new RefProperty(declareAnnotation.rest().getCanonicalName()));
		responses.put(403, new Response().description("Forbidden"));
		responses.put(404, new Response().description("Not found"));
		addOperation(swagger, klass, "/api/" + declareAnnotation.resource() + "/{id}/" + declareLinkedResource.role() + "/{linkId}", "delete",
				"Delete a linked " + declareHrefResource.role(), serializableParameters, null, responses);
	}

	/**
	 * add a new rest operation in swagger
	 * 
	 * @param swagger
	 * @param klass
	 * @param resource
	 * @param ope
	 * @param summary
	 * @param parameters
	 * @param body
	 * @param responses
	 */
	private static void addOperation(Swagger swagger, Class<?> klass, String resource, String ope, String summary,
			List<SerializableParameter> parameters, Parameter body, Map<Integer, Response> responses) {
		Api apiAnnotation = klass.getAnnotationsByType(Api.class)[0];

		if (swagger.getPath(resource) == null) {
			swagger.path(resource, new Path());
		}

		Operation operation = new Operation();
		/**
		 * fix tag, and operation attributes
		 */
		operation.tag(apiAnnotation.value());
		operation.summary(summary);
		operation.produces("application/json");
		/**
		 * set operation
		 */
		swagger.getPath(resource).set(ope, operation);
		/**
		 * parameters
		 */
		if (body != null) {
			operation.parameter(body);
		}
		for (Parameter prm : parameters) {
			operation.parameter(prm);
		}
		/**
		 * responses
		 */
		for (Entry<Integer, Response> entry : responses.entrySet()) {
			operation.response(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	private static void jarvisExtension(Swagger swagger, Set<Class<?>> apiClasses) {
		apiClasses.forEach(klass -> {
			/**
			 * scan for default resource
			 */
			jarvisExtensionScan(swagger, (Class<ApiDefaultResources>) klass);
		});
	}

	/**
	 * to json
	 * 
	 * @param swagger
	 * @return String
	 * @throws JsonProcessingException
	 */
	public static String swaggerToJson(Swagger swagger) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		String json = objectMapper.writeValueAsString(swagger);
		return json;
	}
}
