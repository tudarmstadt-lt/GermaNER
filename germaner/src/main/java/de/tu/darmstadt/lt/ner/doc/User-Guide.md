How to run GermaNER:

###  From Commandline

    * Download the jar file from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-09-09-2015.jar) or if you don't have enough memory, use GermaNER without freebase features from [here] (https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-nofb-09-09-2015.jar) -> the corresponding configuration file is found [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/config-nofreebase.properties)
    * Download the configuration file from [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/config.properties)
    * Download a test file [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/test_de.tsv)
    * Download a training file [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/full_train.tsv)
    * To test a file that was trained on our model, run as follows:
        * `java -jar GermaNER-09-09-2015.jar -t TESTFILE -o OUTPUTFILE`
        
 where `-t TESTFILE` is the document to be tagged and  `-o OUTFILE`  is name of the tagged output file,
However, if you want to test one of your own models, add the `-c CONFIGFILE` and `-d OUTPUTFOLDER` which are the configuration file used for training and the directory of the saved GermaNER model directory name respectively.  
    * To train (and optionally test) a model, run it as follows
       * `java -jar GermaNER-09-09-2015.jar -f TRAINFILE -t TESTFILE -o OUTPUTFILE -c CONFIGFILE -d OUTPUTFOLDER`

 Where `-t TESTFILE`, `-o OUTPUTFILE`, and `-c CONFIGFILE` are optional parameters.  `-t TESTFILE` is the document to be tagged,` -f TRAINFILE ` is the training data, as explained [here] (https://github.com/tudarmstadt-lt/GermaNER/wiki/File-Format), `-o OUTPUTFILE` is the tagged output file, and `-c CONFIGFILE` is the configuration file as explained [here](https://github.com/tudarmstadt-lt/GermaNER/wiki/Configuration-File) and `-d OUTPUTFOLDER` is the directory where the model will be saved (if`-d `is not provided, a folder called `output` will be created and the model will be stored therein.)
    * To tag your document, the document format should be as it is explained here, run it as follows
###  Inside an application

  TODO

###  From source
    * Get the source code of [GermaNER](https://github.com/tudarmstadt-lt/GermaNER) using git

    `git clone https://github.com/tudarmstadt-lt/GermaNER.git`

    * Compile GermaNER using the following command

   `mvn clean install -Dmaven.test.skip=true`

The generated jar will be available under the **target** folder
