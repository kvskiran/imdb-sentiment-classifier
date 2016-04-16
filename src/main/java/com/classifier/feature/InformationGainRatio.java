package com.classifier.feature;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.classifier.organize.TermDocumentMatrix;
import com.classifier.utilities.ValueComparator;

public class InformationGainRatio {
	protected static double calculateScore(double docTotal, double p, double n) {
//		return (Math.abs(((p + 1) / (p + n + 1))
//				* (Math.log((p + 1) / (p + n + 1)) / Math.log(2))
//				- ((n + 1) / (p	+ n + 1))
//				* (Math.log((n + 1) / (p + n + 1)) / Math.log(2))));

		return (-((p + 1) / (p + n + 1))
				* (Math.log((p + 1) / (p + n + 1)) / Math.log(2))
				- ((n + 1) / (p	+ n + 1))
				* (Math.log((n + 1) / (p + n + 1)) / Math.log(2)));
	}
	
	public static List<String> select(TermDocumentMatrix matrix, int amt) {
		HashMap<String, Double> featureScores = new HashMap<String, Double>();
		List<String> features = new LinkedList<String>();
		double docTotal = Double.parseDouble(matrix.documentLookup.keySet().size() + "");
		for (String feature : matrix.dictionaryMatrix.keySet()) {
			double vscore = 0;
			double iscore = 0;
			for (Integer freq : matrix.dictionaryMatrix.get(feature).keySet()) {
				double p = matrix.dictionaryMatrix.get(feature).get(freq)[0];
				double n = matrix.dictionaryMatrix.get(feature).get(freq)[1];
				vscore += ((p + n + 1) / (docTotal + 1)) * calculateScore(docTotal, p, n);
			}
			double fscore = 1 - vscore;
			for (Integer freq : matrix.dictionaryMatrix.get(feature).keySet()) {
				double p = matrix.dictionaryMatrix.get(feature).get(freq)[0];
				double n = matrix.dictionaryMatrix.get(feature).get(freq)[1];
				iscore += (-(p + n + 1) / (docTotal + 1)) * (Math.log((p + n + 1) / (docTotal + 1)) / Math.log(2));
			}
			double score = vscore / iscore;
				System.out.println(feature + ": " + fscore + " - " + iscore);
			featureScores.put(feature, score);
		}
		Comparator<String> comparator = new ValueComparator(featureScores);
		TreeMap<String, Double> featureScoresSorted = new TreeMap<String, Double>(comparator);
		featureScoresSorted.putAll(featureScores);
		int i = 0;
		for (Map.Entry<String, Double> entry : featureScoresSorted.entrySet()) {
			features.add(entry.getKey());
			i++;
			if (i == amt)
				break;
		}
		return features;
	}
	
	public static void main(String[] args) {
		String processedDir = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "processed" + File.separator + "aclImdb" + File.separator;
		TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
		matrix.initMatrix();
		for (String name : matrix.dictionaryMatrix.keySet()) {
			System.out.print(name + "[");
			for (Integer val : matrix.dictionaryMatrix.get(name).keySet()) {
				int valuep = matrix.dictionaryMatrix.get(name).get(val)[0];
				int valuen = matrix.dictionaryMatrix.get(name).get(val)[1];
				System.out.print("(" + val + "," + valuep + "-" + valuen + ")");
			}
			System.out.println("]");
		}
		List<String> s = select(matrix, 10);
		System.out.println(s.toString());
		// MutualInformation mi = new MutualInformation();
		// mi.select(matrix, 10);
	}
}


