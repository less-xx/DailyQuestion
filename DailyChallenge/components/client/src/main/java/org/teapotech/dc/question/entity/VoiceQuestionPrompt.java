/**
 * 
 */
package org.teapotech.dc.question.entity;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author jiangl
 *
 */
public class VoiceQuestionPrompt implements QuestionPrompt {

	private final String value;

	public VoiceQuestionPrompt(String text) {
		this.value = text;
	}

	@JsonProperty("voice")
	public String getValue() {
		return this.value;
	}

}
