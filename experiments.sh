#!/bin/bash

# base directory
BASE_DIR=~/libsvm
SVM_OPTS=(0 1)
KERNEL_OPTS=(0 1 2 3)
CACHE_SIZE=1000

if [ "$1" ]; then
    CACHE_SIZE=$1
fi

cd $BASE_DIR
mkdir experiments experiments/train experiments/test experiments/output experiments/models

OUTPUT_DIR=$BASE_DIR/experiments/output

# training
TRAIN_DIR=$BASE_DIR/experiments/train
MODEL_DIR=$BASE_DIR/experiments/models

for SVM in ${SVM_OPTS[@]}; do
for KERNEL in ${KERNEL_OPTS[@]}; do

for F in $(ls $TRAIN_DIR)
  do ID=$(echo $F | grep -o "_.*")
     cat /dev/null > $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
     echo "./svm-train -s $SVM -t $KERNEL -m $CACHE_SIZE $TRAIN_DIR/$F $MODEL_DIR/$ID-$SVM-$KERNEL.model" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
     echo "" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output          
     ./svm-train -s $SVM -t $KERNEL -m $CACHE_SIZE $TRAIN_DIR/$F $MODEL_DIR/$ID-$SVM-$KERNEL.model >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output 2>&1
     echo "" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
     echo "----" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
     echo "" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
done

# testing
TEST_DIR=$BASE_DIR/experiments/test
OUTPUT_DIR=$BASE_DIR/experiments/output

for F in $(ls $TEST_DIR)
  do ID=$(echo $F | grep -o "_.*")
     MODEL=$(ls $MODEL_DIR | grep $ID-$SVM-$KERNEL)
     echo "./svm-predict $TEST_DIR/$F $MODEL_DIR/$MODEL /dev/null" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output
     echo "" >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output     
     ./svm-predict $TEST_DIR/$F $MODEL_DIR/$MODEL /dev/null >> $OUTPUT_DIR/$ID-$SVM-$KERNEL.output 2>&1
done

done; done

# analyze
ANALYSIS_OUTPUT_FILE=$OUTPUT_DIR/sorted_accuracies
cat /dev/null > $ANALYSIS_OUTPUT_FILE-temp

for F in $(ls $OUTPUT_DIR | grep output)
  do LINE=$(cat $OUTPUT_DIR/$F | grep Accuracy)
     PERCENT=$(echo $LINE | grep -ob %)
     PERCENT_INDEX=${PERCENT:0:2}
     ACCURACY=${LINE:11:$PERCENT_INDEX-11}
     echo "$ACCURACY :: $F" >> $ANALYSIS_OUTPUT_FILE-temp
done

sort -r $ANALYSIS_OUTPUT_FILE-temp > $ANALYSIS_OUTPUT_FILE
# rm $ANALYSIS_OUTPUT_FILE-temp
