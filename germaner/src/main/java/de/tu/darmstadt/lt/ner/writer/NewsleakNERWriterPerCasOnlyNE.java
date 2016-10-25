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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tu.darmstadt.lt.ner.model.Entity;
import de.tu.darmstadt.lt.ner.model.Relation;
import de.tu.darmstadt.lt.ner.types.DocumentNumber;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

@SuppressWarnings("unused")
public class NewsleakNERWriterPerCasOnlyNE extends JCasConsumer_ImplBase {
	public static final String OUTPUT_DIR = "OutputFile";
	@ConfigurationParameter(name = OUTPUT_DIR, mandatory = true)
	private File OutputFolder = null;

	public static final String LF = System.getProperty("line.separator");
	public static final String TAB = "\t";

	Map<String, Integer> entIds = new HashMap<>();

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {

			DocumentNumber dn = JCasUtil.select(jCas, DocumentNumber.class).iterator().next();

			FileWriter entity = new FileWriter(new File(OutputFolder, "entity.tsv"), true);

			int begin = 0, end = 0;

			String prevNeType = "O";
			StringBuffer namedEntity = new StringBuffer();
			for (NamedEntity ne : JCasUtil.select(jCas, NamedEntity.class)) {
				if (ne.getValue().equals("O")) {
					if (prevNeType.equals("O")) {
						continue;
					} else {
						if(namedEntity.length()<3)
							continue;
						entIds.putIfAbsent(namedEntity.toString().toLowerCase() + prevNeType.substring(2),
								entIds.size() + 1);
						int id = entIds.get(namedEntity.toString().toLowerCase() + prevNeType.substring(2));
						entity.write(id + TAB + namedEntity + TAB + prevNeType.substring(2) + TAB + begin + TAB + end
								+ TAB + dn.getNumber() + LF);
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
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
					if(namedEntity.length()<3)
						continue;
					entIds.putIfAbsent(namedEntity.toString().toLowerCase() + prevNeType.substring(2),
							entIds.size() + 1);
					int id = entIds.get(namedEntity.toString().toLowerCase() + prevNeType.substring(2));
					entity.write(id + TAB + namedEntity + TAB + prevNeType.substring(2) + TAB + begin + TAB + end + TAB
							+ dn.getNumber() + LF);
					prevNeType = ne.getValue();
					namedEntity = new StringBuffer();
					namedEntity.append(ne.getCoveredText());
					begin = ne.getBegin();
					end = ne.getEnd();

				}
			}
			entity.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}