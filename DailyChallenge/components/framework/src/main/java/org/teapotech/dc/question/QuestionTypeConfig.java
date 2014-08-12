/**
 * 
 */
package org.teapotech.dc.question;

/**
 * @author jiangl
 *
 */
public class QuestionTypeConfig {
	private String defaultViewUrl;
	private QuestionFactory questionFactory;

	public String getDefaultViewUrl() {
		return defaultViewUrl;
	}

	public void setDefaultViewUrl(String defaultViewUrl) {
		this.defaultViewUrl = defaultViewUrl;
	}

	public QuestionFactory getQuestionFactory() {
		return questionFactory;
	}

	public void setQuestionFactory(QuestionFactory questionFactory) {
		this.questionFactory = questionFactory;
	}

}
