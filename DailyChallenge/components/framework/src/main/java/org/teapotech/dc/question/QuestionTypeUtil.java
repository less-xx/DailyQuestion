/**
 * 
 */
package org.teapotech.dc.question;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.teapotech.dc.question.entity.Question;

/**
 * @author jiangl
 *
 */
public class QuestionTypeUtil {
	private static Logger logger = LoggerFactory
	        .getLogger(QuestionTypeUtil.class);
	private static HashMap<String, ApplicationContext> contextMap = new HashMap<String, ApplicationContext>();
	private static ObjectMapper mapper = new ObjectMapper();

	private static File homeDir = new File(System.getProperty(
	        "QUESTION_TYPE_HOME", "question_types"));

	public static File getResourceFile(String questionType, String resource)
	        throws FileNotFoundException {
		File qTypeFolder = new File(homeDir, questionType);
		if (!qTypeFolder.exists())
			throw new FileNotFoundException("Cannot find question type: "
			        + questionType);
		File resourceFile = new File(qTypeFolder, resource);
		if (!resourceFile.exists())
			throw new FileNotFoundException("Cannot find resource " + resource);
		return resourceFile;
	}

	private static ClassLoader createQuestionTypeClassLoader(File installFolder) {
		LinkedList<URL> classList = new LinkedList<URL>();
		File libFolder = new File(installFolder, "lib");
		if (libFolder.exists() && libFolder.isDirectory()) {
			loadClasses(classList, libFolder);
		}
		File classFolder = new File(installFolder, "classes");
		if (classFolder.exists() && classFolder.isDirectory()) {
			loadClasses(classList, classFolder);
		}
		URLClassLoader loader = new URLClassLoader(
		        classList.toArray(new URL[] {}),
		        QuestionTypeUtil.class.getClassLoader());
		return loader;
	}

	private static void loadClasses(LinkedList<URL> classList, File file) {
		if (file.isDirectory()) {
			File[] subfiles = file.listFiles();
			for (File subfile : subfiles)
				loadClasses(classList, subfile);
		} else {
			if (file.getName().endsWith(".class")
			        || file.getName().endsWith(".jar")) {
				try {
					classList.add(file.toURI().toURL());
					logger.debug("Loaded {}", file.getAbsolutePath());
				} catch (MalformedURLException e) {
				}
			}
		}
	}

	public static ApplicationContext loadQuestionTypeConfig(File installFolder)
	        throws ValidateQuestionInstallationException {
		File confFile = new File(installFolder, "config.xml");
		if (!confFile.exists()) {
			throw new ValidateQuestionInstallationException(
			        "Invalid package: missing config.xml in "
			                + installFolder.getAbsolutePath());
		}
		ClassLoader currentContextLoader = Thread.currentThread()
		        .getContextClassLoader();
		try {
			ClassLoader questionTypeClassLoader = createQuestionTypeClassLoader(installFolder);
			Thread.currentThread().setContextClassLoader(
			        questionTypeClassLoader);
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
			        new String[] { confFile.toURI().toURL().toString() }, true);
			context.setClassLoader(questionTypeClassLoader);
			contextMap.put(installFolder.getName(), context);
			logger.info("Load question type: {}", installFolder.getName());
			return context;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ValidateQuestionInstallationException(
			        "Invalid config.xml: " + e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(currentContextLoader);
		}
	}

	public static void removeQuestionTypeConfig(File installFolder) {
		FileSystemXmlApplicationContext context = (FileSystemXmlApplicationContext) contextMap
		        .remove(installFolder.getName());
		if (context != null) {
			context.close();
			logger.info("Removed question type: {}", installFolder.getName());
		}
	}

	public static QuestionTypeConfig getQuestionConfig(String questionTypeName) {
		ApplicationContext context = contextMap.get(questionTypeName);
		if (context != null) {
			return context.getBean(QuestionTypeConfig.class);
		}
		return null;
	}

	public static String questionToJson(Question q) {
		try {
			return mapper.writeValueAsString(q);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static File getInstallationFolderByName(String questionTypeName) {
		return new File(homeDir, questionTypeName);
	}
}
