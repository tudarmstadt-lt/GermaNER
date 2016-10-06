/*******************************************************************************
 * Copyright 2014
 * FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.IOException;


import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import com.ibm.icu.text.BreakIterator;

import de.tu.darmstadt.lt.ner.reader.NewsleakCSVReader;
import de.tu.darmstadt.lt.ner.writer.TokensPerSentenceWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class TestBreakIterator
{
    public static void main(String args[]) throws UIMAException, IOException
    {
    	CollectionReader reader = createReader(NewsleakCSVReader.class,
    			NewsleakCSVReader.PARAM_DIRECTORY_NAME, "/home/seid/Desktop/tmp/test.csv");
    	 AnalysisEngine segmenter = createEngine(OpenNlpSegmenter.class, OpenNlpSegmenter.PARAM_LANGUAGE, "en");	
    	AnalysisEngine tpsWriter = createEngine(TokensPerSentenceWriter.class);
       runPipeline(reader, segmenter, tpsWriter);
    }

    public static void printEachForward(BreakIterator boundary, String source) {
        int start = boundary.first();
        for (int end = boundary.next();
             end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
             System.out.print(source.substring(start,end));
        }
    }

}
