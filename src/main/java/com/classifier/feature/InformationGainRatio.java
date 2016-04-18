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

		return (-(((p + 1) / (p + n + 1)) * (Math.log((p + 1) / (p + n + 1)) / Math.log(2))) - (((n + 1) / (p + n + 1)) * (Math.log((n + 1)
				/ (p + n + 1)) / Math.log(2))));
	}

	protected static double calculateScoreImproved(double docTotal, double p, double n) {
		return Math.abs((((p + 1) / (p + n + 1)) * (Math.log((p + 1) / (p + n + 1)) / Math.log(2)))
				- (((n + 1) / (p + n + 1)) * (Math.log((n + 1) / (p + n + 1)) / Math.log(2))));

	}

	public static List<String> select(TermDocumentMatrix matrix, int amt, int mode) {
		HashMap<String, Double> featureScores = new HashMap<String, Double>();
		List<String> features = new LinkedList<String>();
		double docTotal = Double.parseDouble(matrix.documentLookup.keySet().size() + "");
		for (String feature : matrix.dictionaryMatrix.keySet()) {
			double igscore = 0;
			double iigscore = 0;
			double iscore = 0;
			for (Integer freq : matrix.dictionaryMatrix.get(feature).keySet()) {
				double p = matrix.dictionaryMatrix.get(feature).get(freq)[0];
				double n = matrix.dictionaryMatrix.get(feature).get(freq)[1];
				igscore += ((p + n + 1) / (docTotal + 1)) * calculateScore(docTotal, p, n);
				iigscore += ((p + n + 1) / (docTotal + 1)) * calculateScoreImproved(docTotal, p, n);
			}
			igscore = 1 - igscore;
			for (Integer freq : matrix.dictionaryMatrix.get(feature).keySet()) {
				double p = matrix.dictionaryMatrix.get(feature).get(freq)[0];
				double n = matrix.dictionaryMatrix.get(feature).get(freq)[1];
				iscore += (-(p + n + 1) / (docTotal + 1)) * (Math.log((p + n + 1) / (docTotal + 1)) / Math.log(2));
			}
			double igrscore = igscore / iscore;
			double iigrscore = iigscore / iscore;
			switch (mode) {
			case 1:
				featureScores.put(feature, igscore);
				break;
			case 2:
				featureScores.put(feature, igrscore);
				break;
			case 3:
				featureScores.put(feature, iigscore);
				break;
			case 4:
				featureScores.put(feature, iigrscore);
				break;
			default:
				break;
			}
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
		String processedDir = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "processed"
				+ File.separator + "aclImdb" + File.separator;
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
		System.out.println("1");
		// List<String> ig = select(matrix, 100, 1);
		// System.out.println("2");
		// List<String> igr = select(matrix, 100, 2);
		// System.out.println("3");
		// List<String> iig = select(matrix, 100, 3);
		// System.out.println("4");
		// List<String> iigr = select(matrix, 100, 4);
		// System.out.println(ig.toString());
		// System.out.println(igr.toString());
		// System.out.println(iig.toString());
		// System.out.println(iigr.toString());

	}
}
