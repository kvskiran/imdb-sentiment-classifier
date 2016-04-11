package com.classifier.classify;

import java.util.HashSet;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

public class SupportVectorMachine {
    
    private C_SVC<Integer, SparseVector> svm;
    private BinaryModel<Integer, SparseVector> model;

    public SupportVectorMachine(int[] sentimentVec, float[][] docVecs ) {
        svm = new C_SVC<Integer, SparseVector>();

        ImmutableSvmParameterGrid.Builder builder = ImmutableSvmParameterGrid.builder();

        HashSet<Float> cSet = new HashSet<Float>();
        cSet.add(1.0f);

        HashSet<LinearKernel> kernelSet = new HashSet<LinearKernel>();
        kernelSet.add(new LinearKernel());

        // configure params
        builder.eps = 0.001f; // epsilon
        builder.Cset = cSet; // C values used
        builder.kernelSet = kernelSet; // kernel used

        ImmutableSvmParameter params = builder.build();
        
        MutableBinaryClassificationProblemImpl problem
            = new MutableBinaryClassificationProblemImpl(String.class, 2);

        for (int i=0; i<sentimentVec.length; i++) {
            problem.addExample(generateFeatures(docVecs[i]), sentimentVec[i]);
        }

        // train
        System.out.println("Training SVM...");
        model = svm.train(problem, params);
        System.out.println("Training complete.");
    }    

    public int test(float[] documentVec) {
        return (Integer) model.predictLabel(generateFeatures(documentVec));
    }

    private SparseVector generateFeatures(float[] floats) {
        SparseVector sparseVector = new SparseVector(floats.length);
        int[] indices = new int[floats.length];
        for (int i=0; i<floats.length; i++) {
            indices[i] = new Integer(i);
        }
        sparseVector.indexes = indices;
        sparseVector.values = floats;
        return sparseVector;
    }

    // test
    public static void main(String[] args) {
        float[][] documentVecs = {{0f, 1f, 1f}, {1f, 0f, 0f}};
        int[] sentimentVec = {1, -1};
        
        SupportVectorMachine svm 
            = new SupportVectorMachine(sentimentVec, documentVecs);

        System.out.println(svm.test(new float[]{1f, 0f, 0f}));
    }
}
