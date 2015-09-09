#[GermaNER](https://github.com/tudarmstadt-lt/GermaNER/wiki)
A binary is found [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-04-08-2015.jar)

To tag German texts

1. Tokenize your text so that it is one word per line. Sentences should be marked with a blank new line
2. Run the jar file as follows
***java -jar GermaNER-04-08-2015.jar -testf YOURTOKENIZEDTESTFILE***

The tagged document will be under **output/result.txt**
