package com.classifier.process;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import com.classifier.utilities.Util;

public class StopwordRemover {
	public static List<String> processStopwords(List<String> input, String stopwordsList) {
		try {
			List<String> output = new ArrayList<String>();
			if (!stopwordsList.isEmpty()) {
				List<String> stopwords = Util.readFileByLine("src" + File.separator + "main" + File.separator + "resources"
						+ File.separator + "aclImdb" + File.separator + stopwordsList);
				for (String word : input) {
					if (!stopwords.contains(word)) {
						output.add(word);
					}
				}
			} else {
				for (String word : input) {
					output.add(word);
				}
			}
			return output;
		} catch (Exception e) {
			return null;
		}
	}
}
