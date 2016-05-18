# Using GermaNER from code

 We don't have it yet in maven central but it is easier to add the jar in your application. 
 Once you have the jar added to your application, import the following

***de.tu.darmstadt.lt.ner.preprocessing.GermaNERMain***

Then call the method as follows on your test file:

***classifyTestFile(testFile, outputFile, null, null, "de")***

The first argument is the test file in the Test Data format: (https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/File-Format.md#test-data)

The second argument is the output file (testFile + prediction)

The last argument is the lanaguage, in this case, use **de** for German

The the third and fourth arguments are irrelevant and should be set **null**

Note: If you have less memory use the jar file without the freebase features from [here] (https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/GermaNER-nofb-09-09-2015.jar)
