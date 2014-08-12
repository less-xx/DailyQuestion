/**
 * 
 */
package org.teapotech.dc.question;

import java.io.File;

/**
 * @author jiangl
 *
 */
public interface QuestionTypeChangeListener {
	void onCreateQuestionType(String questionTypeName, File file);

	void onModifyQuestionType(String questionTypeName, File file);

	void onDeleteQuestionType(String questionTypeName, File file);
}
