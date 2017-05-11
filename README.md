#[GermaNER](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Home.md) - Free Open German Named Entity Recognition Tool

GermaNER is licensed under ASL 2.0 and other lenient licenses, allowing its use for academic and commercial purposes without restrictions. 

##GermaNER in three lines

To tag German texts:

1. Download the binary from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-09-09-2015.jar) or if you don't have enough memory, use GermaNER without freebase features from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-nofb-09-09-2015.jar).
1. Tokenize your text so that it is one word per line. Sentences should be marked with a blank new line. Read details [here] (https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/File-Format.md).
2. Run the jar file as follows (see details [here](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/User-Guide.md))

***`java -Xmx4g -jar GermaNER-09-09-2015.jar -t YourTokenizedTestFile -o OutputFileName`***
                              
                              OR (if you have less memmory)

***`java -Xmx1300m -jar GermaNER-nofb-09-09-2015.jar -t  YourTokenizedTestFile -o OutputFileName`***

The tagged document will be under **output/result.tsv**

```diff
- NEW
```


##Train GermaNER with your own training file and feature files

If you like to train GermaNER with your own training file or our training file from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/full_train.tsv) but with different feature files, do as follows

* Get the data.zip file from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/data.zip) and change the contents of any files as needed. Once done, zip back as data.zip
* Get the config file, ***config.properties***, [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/config.properties). set ***useFreeBase=0*** if you do not have enough memory. If you have lookup feature files like [this](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/lookUpFile.tsv), set ***lookUpFeature=1***. If you have list feature files like [this](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/listFile.tsv), set ***listFeature=1***. 
* Get the GermaNER jar file from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-train-04-12-2016.jar). This jar file is only meant to train an NER model on new dataset or modified features. It does not contain usable NER model.

For training and testing at the same time, run it as follows:

***`java -jar GermaNER-train-04-12-2016.jar -f YOURTRAINFILE -t YOURTESTFILE -r data.zip -d MODELDIR -o OUTPUTFILENAME -c config.properties`***

For testing, once your run the above command and you have the NER model under MODELDIR, run it without the -f switch  as follows

***`java -jar GermaNER-train-04-12-2016.jar -t YOURTESTFILE -r data.zip -d MODELDIR -o OUTPUTFILENAME -c config.properties`***

```diff
- NEW END
```
### Contents
* Resources including files for feature generation (data.zip) and configuration files (config.properties) are found [here](https://github.com/tudarmstadt-lt/GermaNER/releases/tag/germaNER0.9.1)
* [System requirements](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/System-Requirements.md)
* [Introduction](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Home.md)
* [Configurations](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Configuration-File.md)
* [User guide](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/User-Guide.md)
* [File format](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/File-Format.md)
* [Features](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Features.md)
* [Customizing GermaNER](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Customizing-GermaNER.md)
* [From source] (https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/fromsource.md)

