package de.tu.darmstadt.lt.ner.preprocessing;

import java.io.IOException;

import org.apache.uima.UIMAException;

public class NoDSentenceToCRFWriter
{
public static void main(String[] args) throws UIMAException, IllegalArgumentException, IOException
{
    TrainNERModel.sentenceToCRFFormat(args[0], args[1]);
}
}
