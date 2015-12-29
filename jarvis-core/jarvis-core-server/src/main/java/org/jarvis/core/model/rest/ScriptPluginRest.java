package org.jarvis.core.model.rest;

import org.jarvis.core.model.rest.plugin.PluginRest;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * plugin script
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScriptPluginRest extends PluginRest {
}
