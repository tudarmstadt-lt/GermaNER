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
package de.tu.darmstadt.lt.ner.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tu.darmstadt.lt.ner.types.DocumentNumber;
import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class NewsleakNERWriter extends JCasConsumer_ImplBase {
	public static final String OUTPUT_FILE = "OutputFile";
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private File OutputFile = null;

	public static final String LF = System.getProperty("line.separator");
	public static final String TAB = "\t";

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			FileWriter outputWriter = new FileWriter(OutputFile, true);

			for (DocumentNumber dn : JCasUtil.select(jCas, DocumentNumber.class)) {
				String prevNeType = "O";
				StringBuffer namedEntity = new StringBuffer();
				int begin = 0, end = 0;
				for (NamedEntity ne : JCasUtil.selectCovered(jCas, NamedEntity.class, dn)) {
					if (ne.getValue().equals("O")) {
						if (prevNeType == "O") {
							continue;
						} else {
							outputWriter.write(namedEntity + TAB + prevNeType.substring(2) + TAB + begin + TAB + end
									+ TAB + dn.getNumber() + LF);
						}
					} else if (prevNeType.equals("O")) {
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin();
						end = ne.getEnd();
					} else if (prevNeType.equals(ne.getValue())) {
						end = ne.getEnd();
						namedEntity.append(" " + ne.getCoveredText());
					} else {
						outputWriter.write(namedEntity + TAB + prevNeType.substring(2) + TAB + begin + TAB + end + TAB
								+ dn.getNumber() + LF);
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin();
						end = ne.getEnd();

					}
				}
			}
			outputWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}