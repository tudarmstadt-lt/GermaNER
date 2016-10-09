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
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tu.darmstadt.lt.ner.model.Entity;
import de.tu.darmstadt.lt.ner.model.Relation;
import de.tu.darmstadt.lt.ner.types.DocumentNumber;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

public class NewsleakNERWriter extends JCasConsumer_ImplBase {
	public static final String OUTPUT_DIR = "OutputFile";
	@ConfigurationParameter(name = OUTPUT_DIR, mandatory = true)
	private File OutputFolder = null;

	public static final String LF = System.getProperty("line.separator");
	public static final String TAB = "\t";

	List<Entity> ents = new ArrayList<Entity>(); // All entities
	List<Relation> rels = new ArrayList<Relation>();

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {

			FileWriter entDocOffsets = new FileWriter(new File(OutputFolder, "entDocOffsets.tsv"));
			FileWriter docEntity = new FileWriter(new File(OutputFolder, "docEntity.tsv"));
			FileWriter entity = new FileWriter(new File(OutputFolder, "entity.tsv"));
			int sentBegin = 0;
			int begin = 0, end = 0;
			for (DocumentNumber dn : JCasUtil.select(jCas, DocumentNumber.class)) {

				Map<Entity, Integer> entityFreqPerDoc = new HashMap<>();
				String prevNeType = "O";
				StringBuffer namedEntity = new StringBuffer();
				sentBegin = dn.getBegin();
				int emL = 0;// getEmptyLines(dn.getCoveredText());
				for (NamedEntity ne : JCasUtil.selectCovered(jCas, NamedEntity.class, dn)) {
					if (ne.getValue().equals("O")) {
						if (prevNeType.equals("O")) {
							continue;
						} else {

							writeEntDocOffsets(entDocOffsets, begin, end, dn, entityFreqPerDoc, prevNeType,
									namedEntity);
							prevNeType = ne.getValue();
							namedEntity = new StringBuffer();
						}
					} else if (prevNeType.equals("O")) {
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin() - sentBegin - emL;
						end = ne.getEnd() - sentBegin - emL;
					} else if (prevNeType.equals(ne.getValue())) {
						end = ne.getEnd() - sentBegin - emL;
						namedEntity.append(" " + ne.getCoveredText());
					} else {

						writeEntDocOffsets(entDocOffsets, begin, end, dn, entityFreqPerDoc, prevNeType, namedEntity);
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin() - sentBegin - emL;
						end = ne.getEnd() - sentBegin - emL;

					}
				}

				for (Entity ent : entityFreqPerDoc.keySet()) {
					docEntity.write(dn.getNumber() + TAB + ent.getId() + TAB + entityFreqPerDoc.get(ent) + LF);
					ent.setFrequency(ent.getFrequency() + entityFreqPerDoc.get(ent));
				}
			}

			for (Entity ent : ents) {
				entity.write(ent.getId() + TAB + ent.getName() + TAB + ent.getType() + TAB + ent.getFrequency() + TAB
						+ false + LF);
			}
			entDocOffsets.close();
			docEntity.close();
			entity.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeEntDocOffsets(FileWriter entDocOffsets, int begin, int end, DocumentNumber dn,
			Map<Entity, Integer> entityFreqPerDoc, String prevNeType, StringBuffer namedEntity) throws IOException {
		Entity ent = addEntity(namedEntity.toString(), prevNeType.substring(2));
		if (entityFreqPerDoc.keySet().contains(ent)) {
			entityFreqPerDoc.put(ent, entityFreqPerDoc.get(ent) + 1);
		} else {
			entityFreqPerDoc.put(ent, 1);
		}

		entDocOffsets.write(ent.getId() + TAB + begin + TAB + end + TAB + dn.getNumber() + LF);
	}

	private Entity addEntity(String aNme, String aType) {
		Entity newEnt = new Entity(aNme, aType);
		if (ents.contains(newEnt)) {
			return ents.get(ents.indexOf(newEnt));
		} else {
			newEnt.setId(ents.size() + 1);
			ents.add(newEnt);
			return newEnt;
		}
	}

	private int getEmptyLines(String aText) {
		String[] tokens = aText.split("(\r\n|\n)");
		int count = 0;
		for (String line : tokens) {

			// new sentence if there's a new line
			if (line.equals("")) {
				count++;
			}
		}
		return count;
	}
}