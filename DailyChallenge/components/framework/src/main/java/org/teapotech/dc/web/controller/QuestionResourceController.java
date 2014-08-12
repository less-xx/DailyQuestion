/**
 * 
 */
package org.teapotech.dc.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.teapotech.dc.app.util.ContentTypeDetector;
import org.teapotech.dc.question.QuestionTypeUtil;

/**
 * @author jiangl
 *
 */
@Controller
@RequestMapping("resources")
public class QuestionResourceController {
	private Logger logger = LoggerFactory
	        .getLogger(QuestionResourceController.class);

	@RequestMapping(value = "/{questionType}/{resource}", method = RequestMethod.GET)
	public void getResource(Map<String, Object> model,
	        @PathVariable String questionType, @PathVariable String resource,
	        HttpServletResponse response) throws FileNotFoundException,
	        IOException {
		File resourceFile = QuestionTypeUtil.getResourceFile(questionType,
		        resource);
		String contentType = ContentTypeDetector
		        .detectContentType(resourceFile);
		if (isText(contentType)) {
			response.setContentType(contentType + ";charset=UTF-8");
			try (OutputStreamWriter writer = new OutputStreamWriter(
			        response.getOutputStream(), Charset.forName("UTF-8"));
			        InputStreamReader reader = new InputStreamReader(
			                new FileInputStream(resourceFile),
			                Charset.forName("UTF-8"));) {
				char[] buf = new char[256];
				int i = 0;
				while ((i = reader.read(buf)) > 0) {
					writer.write(buf, 0, i);
				}
			}
		} else {
			response.setContentType(contentType);
			try (FileInputStream in = new FileInputStream(resourceFile);
			        OutputStream out = response.getOutputStream();) {
				byte[] buf = new byte[256];
				int i = 0;
				while ((i = in.read(buf)) > 0) {
					out.write(buf, 0, i);
				}
			}
		}
		logger.info("GET: /{}/{} [{}]", questionType, resource, contentType);
	}

	private boolean isText(String contentType) {
		if (contentType == null)
			return false;
		if (contentType.startsWith("text"))
			return true;
		if (contentType.endsWith("script") || contentType.endsWith("json"))
			return true;
		return false;
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler({ FileNotFoundException.class })
	public @ResponseBody String handleFileNotFoundException(
	        HttpServletRequest request, Exception ex) {
		logger.error(ex.getMessage(), ex);
		return ex.getMessage();
	}
}
