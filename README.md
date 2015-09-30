#[GermaNER](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Home.md) - Free Open German Named Entity Recognition Tool

##GermaNER in three lines

To tag German texts:

1. Download the binary from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-09-09-2015.jar) or if you don't have enough memory, use GermaNER without freebase features from [here] (https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-nofb-09-09-2015.jar).
1. Tokenize your text so that it is one word per line. Sentences should be marked with a blank new line. Read details [here] (https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/File-Format.md).
2. Run the jar file as follows (see details [here](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/User-Guide.md))

***`java -Xmx4g -jar GermaNER-09-09-2015.jar -t YourTokenizedTestFile -o OutputFileName`***
                              
                              OR (if you have less memmory)

***`java -Xmx1300m -jar GermaNER-nofb-09-09-2015.jar -t  YourTokenizedTestFile -o OutputFileName`***

The tagged document will be under **output/result.tsv**

### Contents
* [System requirements](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/System-Requirements.md)
* [Introduction](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Home.md)
* [Configurations](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Configuration-File.md)
* [User guide](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/User-Guide.md)
* [File format](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/File-Format.md)
* [Features](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/Features.md)
* [Customizing GermaNER](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/System-Requirements.md)

