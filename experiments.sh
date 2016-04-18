#!/bin/bash

# svm params
SVM_TYPE=0
KERNEL_TYPE=0

# base directory
BASE_DIR=~/programming-projects/libsvm

cd $BASE_DIR
OUTPUT_DIR=$BASE_DIR/experiments/output

# training
TRAIN_DIR=$BASE_DIR/experiments/train
MODEL_DIR=$BASE_DIR/experiments/models

for F in $(ls $TRAIN_DIR)
  do ID=$(echo $F | grep -o "_.*")
     cat /dev/null > $OUTPUT_DIR/$ID.output
     echo "./svm-train -s $SVM_TYPE -t $KERNEL_TYPE $TRAIN_DIR/$F $MODEL_DIR/$ID.model" >> $OUTPUT_DIR/$ID.output
     echo "" >> $OUTPUT_DIR/$ID.output          
     ./svm-train -s $SVM_TYPE -t $KERNEL_TYPE $TRAIN_DIR/$F $MODEL_DIR/$ID.model >> $OUTPUT_DIR/$ID.output 2>&1
     echo "" >> $OUTPUT_DIR/$ID.output
     echo "----" >> $OUTPUT_DIR/$ID.output
     echo "" >> $OUTPUT_DIR/$ID.output
done

# testing
TEST_DIR=$BASE_DIR/experiments/test
OUTPUT_DIR=$BASE_DIR/experiments/output

for F in $(ls $TEST_DIR)
  do ID=$(echo $F | grep -o "_.*")
     MODEL=$(ls $MODEL_DIR | grep $ID)
     echo "./svm-predict $TEST_DIR/$F $MODEL_DIR/$MODEL /dev/null" >> $OUTPUT_DIR/$ID.output
     echo "" >> $OUTPUT_DIR/$ID.output     
     ./svm-predict $TEST_DIR/$F $MODEL_DIR/$MODEL /dev/null >> $OUTPUT_DIR/$ID.output 2>&1
done
