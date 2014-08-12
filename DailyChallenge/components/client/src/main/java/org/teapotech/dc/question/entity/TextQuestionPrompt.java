/**
 * 
 */
package org.teapotech.dc.question.entity;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author jiangl
 *
 */
public class TextQuestionPrompt implements QuestionPrompt {

	private final String value;

	public TextQuestionPrompt(String text) {
		this.value = text;
	}

	@JsonProperty("text")
	public String getValue() {
		return this.value;
	}

}
