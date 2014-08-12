package org.teapotech.dc.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.teapotech.dc.question.QuestionFactoryManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:beans-test.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestQuestionFactoryManager {
	private static File homeDir = new File(System.getProperty(
	        "QUESTION_TYPE_HOME", "question_types"));
	private static File testDir = new File(System.getProperty(
	        "APPLICATION_HOME", "test"));

	@Autowired
	QuestionFactoryManager manager;

	@Test
	public void testQuestionFactoryManager() {

		File newTypeFolder = new File(testDir, "test_question");
		try {
			FileUtils.copyDirectoryToDirectory(newTypeFolder, homeDir);
			Thread.sleep(20000);
			FileUtils.deleteDirectory(new File(homeDir, "test_question"));
			Thread.sleep(20000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
