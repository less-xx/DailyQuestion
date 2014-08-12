/**
 * 
 */
package org.teapotech.dc.question.basic_math;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import org.teapotech.dc.question.QuestionFactory;
import org.teapotech.dc.question.entity.Question;
import org.teapotech.dc.question.entity.TextQuestionPrompt;

/**
 * @author jiangl
 *
 */
public class BasicMathQuestionFactory extends QuestionFactory {

	final static int MAX_NUMBER = 55;
	final static int MAX_DAYS = 11;
	final static int QUESTION_PER_DAY = 10;

	private SecureRandom rnd = new SecureRandom();

	private int randomNumber() {
		return rnd.nextInt(MAX_NUMBER);
	}

	private int randomNumber(int min, int max) {
		return rnd.nextInt(max - min) + min;
	}

	private int randomNumber(int min) {
		return rnd.nextInt(MAX_NUMBER - min) + min;
	}

	private String randomOperator(int n1, int n2) {
		if (n1 >= n2) {
			return rnd.nextFloat() > 0.5 ? "+" : "-";
		} else {
			return "+";
		}
	}

	private String randomOperator(String opCollection) {
		return opCollection.charAt(rnd.nextInt(opCollection.length())) + "";
	}

	private int calculate(int n1, int n2, String operator) {
		if (operator.equals("+"))
			return n1 + n2;
		else
			return n1 - n2;
	}

	private ArrayList<String> buildQuestionElements() {
		int n1 = randomNumber(12);
		int n2 = randomNumber(5);
		String op1 = randomOperator(n1, n2);
		int r1 = calculate(n1, n2, op1);
		int n3 = randomNumber();
		String op2 = randomOperator(r1, n3);
		int r = calculate(r1, n3, op2);
		ArrayList<String> list = new ArrayList<String>();
		list.add(String.valueOf(n1));
		list.add(op1);
		list.add(String.valueOf(n2));
		list.add(op2);
		list.add(String.valueOf(n3));
		list.add("=");
		list.add(String.valueOf(r));
		return list;
	}

	@Override
	public Question random() {
		BasicMathQuestion q = new BasicMathQuestion();
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		q.setPrompt(new TextQuestionPrompt(
		        "What's the correct answer for the blank field?"));
		ArrayList<String> elements = buildQuestionElements();
		int p = rnd.nextInt(elements.size());
		int len = elements.size();
		for (int i = 0; i < len; i++) {
			if (i == p) {
				params.put("p" + i, "");
				continue;
			}
			params.put("p" + i, elements.get(i));
		}
		q.setParameters(params);
		return q;
	}

}
