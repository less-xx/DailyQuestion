/**
 * 
 */
package org.teapotech.dc.question;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jiangl
 *
 */
@Component
public class QuestionFactoryManager implements QuestionTypeChangeListener {
	private static Logger logger = LoggerFactory
	        .getLogger(QuestionFactoryManager.class);
	@Autowired
	private QuestionTypeChangeDetector questionTypeChangeDetector;

	@PostConstruct
	public void init() {
		questionTypeChangeDetector.add(this);
	}

	public void onCreateQuestionType(String questionTypeName, File file) {
		logger.info("Create question type {}, {}", questionTypeName,
		        file.getAbsolutePath());
		File installFolder = QuestionTypeUtil
		        .getInstallationFolderByName(questionTypeName);
		if (!file.getName().equals(questionTypeName)) {
			QuestionTypeUtil.removeQuestionTypeConfig(installFolder);
		}
		try {
			QuestionTypeUtil.loadQuestionTypeConfig(QuestionTypeUtil
			        .getInstallationFolderByName(questionTypeName));
		} catch (ValidateQuestionInstallationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void onModifyQuestionType(String questionTypeName, File file) {
		logger.info("Modify question type {}, {}", questionTypeName,
		        file.getAbsolutePath());
		File installFolder = QuestionTypeUtil
		        .getInstallationFolderByName(questionTypeName);
		QuestionTypeUtil.removeQuestionTypeConfig(installFolder);

		try {
			QuestionTypeUtil.loadQuestionTypeConfig(installFolder);
		} catch (ValidateQuestionInstallationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void onDeleteQuestionType(String questionTypeName, File file) {
		logger.info("Delete question type {}, {}", questionTypeName,
		        file.getAbsolutePath());
		File installFolder = QuestionTypeUtil
		        .getInstallationFolderByName(questionTypeName);
		QuestionTypeUtil.removeQuestionTypeConfig(installFolder);
		if (!file.getName().equals(questionTypeName)) {
			try {
				QuestionTypeUtil.loadQuestionTypeConfig(installFolder);
			} catch (ValidateQuestionInstallationException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
