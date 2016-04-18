package com.classifier.feature;

import java.io.File;

import com.classifier.organize.TermDocumentMatrix;

public class MutualInformation extends FeatureSelector {

    @Override
    protected double calculateScore(double n, 
                                    double n1, 
                                    double n0, 
                                    double n11,
                                    double n10,
                                    double n01,
                                    double n00) {
        return -1*((((n11+1)/(n+1)) * Math.log(((n*n11)+1)/((n1*n1)+1)))
            + (((n01+1)/(n+1)) * Math.log(((n*n01)+1)/((n0*n1)+1)))
            + (((n10+1)/(n+1)) * Math.log(((n*n10)+1)/((n1*n0)+1)))
            + (((n00+1)/(n+1)) * Math.log(((n*n00)+1)/((n0*n0)+1))));
    }

    public static void main(String[] args) {
        String processedDir = "src" + File.separator + "test" 
            + File.separator + "resources" + File.separator + "processed" 
            + File.separator + "aclImdb" + File.separator;
        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        MutualInformation mi = new MutualInformation();

        mi.select(matrix, 10);
    }
}
