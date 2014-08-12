/**
 * 
 */
package org.teapotech.dc.question.entity;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * @author jiangl
 *
 */
@JsonPropertyOrder({ "prompt", "params", "view" })
public abstract class Question {

	protected QuestionPrompt prompt;
	protected String viewUrl;
	protected Map<String, Object> parameters;

	public QuestionPrompt getPrompt() {
		return prompt;
	}

	public void setPrompt(QuestionPrompt prompt) {
		this.prompt = prompt;
	}

	@JsonProperty("view")
	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	@JsonProperty("params")
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}
