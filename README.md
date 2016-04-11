## imdb-sentiment-classifier

### Build

```
$ git clone git@github.com:mattusifer/imdb-sentiment-classifier.git
$ cd imdb-sentiment-classifier
$ mvn compile
```

### Run
```
$ mvn exec:java -Dexec.mainClass=com.classifier.App
```
Running the main application will:

1. Process all IMDB files and store the processed files in the `src/main/resources/processed` directory. 
2. Build a vocabulary based on the processed files
3. Insert all processed files into a term document matrix
4. Select the top 100 features for each feature selection method
