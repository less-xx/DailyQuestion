/**
 * 
 */
package org.teapotech.dc.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.teapotech.dc.question.QuestionFactory;
import org.teapotech.dc.question.QuestionTypeConfig;
import org.teapotech.dc.question.QuestionTypeUtil;
import org.teapotech.dc.question.entity.Question;

/**
 * @author jiangl
 *
 */
public class TestLoadQuestionType {

	private static File homeDir = new File(System.getProperty(
	        "QUESTION_TYPE_HOME", "question_types"));

	@Test
	public void testLoadQuestionType() {
		File installFolder = new File(homeDir, "math_basic");
		try {
			QuestionTypeUtil.loadQuestionTypeConfig(installFolder);

			QuestionTypeConfig config = QuestionTypeUtil
			        .getQuestionConfig(installFolder.getName());
			assertNotNull(config);
			QuestionFactory questionFac = config.getQuestionFactory();
			assertNotNull(questionFac);
			Question q = questionFac.random();
			assertNotNull(q);
			String json = QuestionTypeUtil.questionToJson(q);
			assertNotNull(json);
			System.out.println(json);
			q = questionFac.random();
			assertNotNull(q);
			json = QuestionTypeUtil.questionToJson(q);
			assertNotNull(json);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
