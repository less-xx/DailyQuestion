/**
 * 
 */
package org.teapotech.dc.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.teapotech.dc.question.QuestionFactory;
import org.teapotech.dc.question.QuestionTypeConfig;
import org.teapotech.dc.question.QuestionTypeUtil;
import org.teapotech.dc.question.entity.Question;

/**
 * @author jiangl
 *
 */
@Controller
@RequestMapping("question")
public class QuestionController {
	private Logger logger = LoggerFactory.getLogger(QuestionController.class);

	@RequestMapping(value = "/{questionType}/random", method = RequestMethod.GET)
	public @ResponseBody Question getResource(Map<String, Object> model,
	        @PathVariable String questionType, HttpServletResponse response)
	        throws FileNotFoundException, IOException {
		QuestionTypeConfig config = QuestionTypeUtil
		        .getQuestionConfig(questionType);
		QuestionFactory qFactory = config.getQuestionFactory();
		return qFactory.random();
	}
}
