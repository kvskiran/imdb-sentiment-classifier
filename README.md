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

This will process all IMDB files and store the processed files in the `src/main/resources/processed` directory
