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
package de.tu.darmstadt.lt.ner.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.uima.UIMAException;

public class SentenceToCRFWriter
{
public static void main(String[] args) throws UIMAException, IllegalArgumentException, IOException
{
    LineIterator sentIt = FileUtils.lineIterator(new File(args[0]), "UTF-8");
List<String> sentences = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    int index  = 0;
    while (sentIt.hasNext()) {
        String line = sentIt.nextLine().toString().trim().split("\t")[1];
        if (line.equals("")) {
            continue;
        }
        sentences.add(line);
    }
    GermaNERMain.sentenceToCRFFormat(sentences, args[1],"de");
}
}
