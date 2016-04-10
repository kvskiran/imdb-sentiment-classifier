package com.classifier.feature;

import java.io.File;

import com.classifier.organize.TermDocumentMatrix;

public class ChiSquared extends FeatureSelector {

    @Override
    protected double calculateScore(double n,
                                    double n1, 
                                    double n0,
                                    double n11,
                                    double n10,
                                    double n01,
                                    double n00) {
        return (n*(((n11*n00) - (n10*n01))*((n11*n00) - (n10*n01))))/((n11+n01)*(n11+n10)*(n10+n00)*(n01+n00));
    }

    public static void main(String[] args) {
        String processedDir = "src" + File.separator + "test" 
            + File.separator + "resources" + File.separator + "processed" 
            + File.separator + "aclImdb" + File.separator;
        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        ChiSquared chi = new ChiSquared();

        System.out.println(chi.select(matrix, 10));
    }
}
