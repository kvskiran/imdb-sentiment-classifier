## imdb-sentiment-classifier

### Creators
Abdullah Aljebreen, Austin Spadaro, Matt Usifer

This application aims to classify the sentiment of documents in a
corpus of 50,000 IMDB reviews. This corpus was originally assembled by
[Maas et al](http://ai.stanford.edu/~amaas/data/sentiment/). It was
created as our final project for CIS 5538 (Text Mining and Language
Processing) with Prof Yuhung Guo at Temple University.

It achieves sentiment classification accuracy of over 85% using
several different feature selection methods.

### Build & Run

```
$ mvn compile
$ mvn exec:java -Dexec.mainClass=com.classifier.App
```

Running the main application will:

1. Process all IMDB files and store the processed files in the `src/main/resources/processed` directory. During this step, all documents are tokenized, lemmatized, stemmed, and stripped of stopwords using different stopwords lists.
2. Build a vocabulary based on the processed files.
3. Insert all processed files into a term document matrix.
4. Iterate through all feature selection methods, selecting varying counts of features and creating copies of the pre-processed documents based on the features selected

In order to assess the effectiveness of the features that were selected for each method, we need to classify the documents. In order to perform sentiment classification, we used the [LibSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvm/) libary. Download the current package into ~/libsvm and `make` to compile it. Run the following commands to in ~/libsvm to create the necessary directories for experimentation:

```
$ mkdir experiments experiments/train experiments/test experiments/output experiments/models
```

Finally, execute the following within the `imdb-sentiment-classifier` base directory to run all of our experiments:

```
$ ./experiments.sh 2000
```

As shown above, `experiments.sh` accepts a single argument which will be passed into LibSVM to control how many MBs of memory in it is allotted when training the SVM. If no arguments are supplied, it will take 1000 MBs of memory by default.

