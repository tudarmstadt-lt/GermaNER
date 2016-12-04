GermaNER is easily customizable either for different languages or different application domains.

# GermaNER for different langauges

What makes GermaNER German specific is the resources used to generate different features. Some of the features such as the Clark POS induced file, the distributional thesaurus used for similarity, and the topic model class label feature files are solely used for German datasets. To adapt GermaNER for different languages:
* Replace the following datasets (in [data.zip](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/resources/data.zip)), with the same name and same data format
   * [200k_2d_wordlists](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-similarity)
   * [topicCluster.txt](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)
   * [topicCluster50.txt](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)
   * [topicCluster200.txt](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)
   * [topicCluster500.txt](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)
   * [uperCasetopicClaster.txt](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)
   * [clark10m256](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#clarks-unsupervised-pos-tagger)
* Zip the new datasets as data.zip and use it for training and testing with an option -r followed by the data.zip file as follows using this jar file.

Training: 
java -jar 

Test : 

# Extending GermaNER feature extractors
Extending the existing feature extractors requires changing the source code in a couple of places and re-compiling GermaNER.
Below we will explain how to extend feature extractors for GermaNER. we will have two kinds of feature extractor extensions. The first type is where we like to include a binary feature extractor which checks existence of a word in a list. An example is if a token is a first name, last name, organization name, and so on. Here, the feature to include are either 1 (exists in the list) or 0 (do not exists).

The second type of feature exctractor is a feature lookup extension. This type of feature extractor require a `<key,value>` pair - a dictionary structure, where the extractor looks for a `value` for a given `key`. The key is a single word and the value can be anything such as POS tag, lemma, stemm, topic cluster label, and so on.

### Binary feature extractor extension template
To include the binary feature extractor of your own, 
* Download the source code and open the following file

[src/main/java/de/tu/darmstadt/lt/ner/feature/extractor/TemplateBinaryFeatureExtractor.java](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/feature/extractor/TemplateBinaryFeatureExtractor.java)

* Change the name of your feature file to **listFileName.tsv**, in the following line. The file should have one feature value per line (for example list of first names or location names). 

` br = (BufferedReader) reader.getReader("listFileName.tsv");`

* Add the following line to the end of the configuration property file so that it looks like the following. If you change the property name, make sure you also change the property name, in the following step.
 
    `....`  
    `useCharacterCategoryFeature=1`  
    `listFeature=1`  
    `....`  

* If you change the configuration property name in the configuration file, or you implement new feature extractor, change the following line in the file [/src/main/java/de/tu/darmstadt/lt/ner/annotator/GetFeaturesFromConfigFile.java](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/annotator/GetFeaturesFromConfigFile.java)

>` if (aProp.getProperty("listFeature").equals("1")) {`
>            `germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),`
>                    `new TemplateBinaryFeatureExtractor()));`
 >       `}`

* Finaly build the jar file. See build GermaNER from [source](https://github.com/tudarmstadt-lt/GermaNER/wiki/User-Guide#from-source) for details.
### Lookup/`<key,value>` feature extractor extension template
Extending dictionary based features is similar to the binary feature extension. To add dictionary based feature extractor:
* Download the source code and modify the following file

[src/main/java/de/tu/darmstadt/lt/ner/feature/extractor/TemplateBinaryFeatureExtractor.java](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/feature/extractor/TemplateLookupFeatureExtractor.java)

* Change the name of your feature file to **lookUpFileName.tsv**, in the following line. The file should have TAB separated file where the first column is the word/token and the second column contains the feature value (example **word TAB POS-TAG**) 


`br = (BufferedReader) reader.getReader("lookUpFileName.tsv");`

* Add the following line to the end of the configuration property file so that it looks like the following. If you change the property name, make sure you also change the property name, in the following step.
 
    `....`  
    `useCharacterCategoryFeature=1`  
    `lookUpFeature=1`  
    `....`  

* If you change the configuration property name in the configuration file, or you implement new feature extractor, change the following line in the file [/src/main/java/de/tu/darmstadt/lt/ner/annotator/GetFeaturesFromConfigFile.java](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/annotator/GetFeaturesFromConfigFile.java)

>` if (aProp.getProperty("lookUpFeature").equals("1")) {`
>            `germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),`
>                    `new TemplateLookupFeatureExtractor()));`
 >       `}`

* Finaly build the jar file. See build GermaNER from [source](https://github.com/tudarmstadt-lt/GermaNER/wiki/User-Guide#from-source) for details.
