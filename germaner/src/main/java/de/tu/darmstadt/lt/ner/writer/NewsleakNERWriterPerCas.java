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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tu.darmstadt.lt.ner.model.Entity;
import de.tu.darmstadt.lt.ner.model.Relation;
import de.tu.darmstadt.lt.ner.types.DocumentNumber;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

public class NewsleakNERWriterPerCas extends JCasConsumer_ImplBase {
	public static final String OUTPUT_DIR = "OutputFile";
	@ConfigurationParameter(name = OUTPUT_DIR, mandatory = true)
	private File outputFolder = null;

	public static final String LF = System.getProperty("line.separator");
	public static final String TAB = "\t";

	List<Entity> ents = new ArrayList<Entity>(); // All entities

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {

	
			DocumentNumber dn = JCasUtil.select(jCas, DocumentNumber.class).iterator().next();
/*			String entDocOffset = 	new File(outputFolder, "entDocOffsets.tsv.tsv");
			if (!(new File(outputFolder, "entDocOffsets.tsv").exists())){
				FileUtils.forceMkdir(outputFolder);
				new File(outputFolder, "entDocOffsets.tsv.tsv").createNewFile();
				new File(outputFolder, "docEntity.tsv.tsv").createNewFile();
			}*/
			FileWriter entDocOffsets = new FileWriter(new File(outputFolder, "entDocOffsets.tsv"), true);
			FileWriter docEntity = new FileWriter(new File(outputFolder, "docEntity.tsv"), true);
			int begin = 0, end = 0;
			NavigableMap<Long, Integer> entityFreqPerDoc = new TreeMap<>();
			Map<Long, Entity> entIds = new HashMap<>();
			String prevNeType = "O";
			StringBuffer namedEntity = new StringBuffer();
			for (NamedEntity ne : JCasUtil.select(jCas, NamedEntity.class)) {
				if (ne.getValue().equals("O")) {
					if (prevNeType.equals("O")) {
						continue;
					} else {
						
						if (namedEntity.length() < 3) {
							prevNeType = ne.getValue();
							namedEntity = new StringBuffer();
							namedEntity.append(ne.getCoveredText());
							begin = ne.getBegin();
							end = ne.getEnd();
						} else {
							writeEntDocOffsets(entDocOffsets, begin, end, dn.getNumber(), entityFreqPerDoc, entIds,
									prevNeType, namedEntity);
							prevNeType = ne.getValue();
							namedEntity = new StringBuffer();
							namedEntity.append(ne.getCoveredText());
							begin = ne.getBegin();
							end = ne.getEnd();
						}				
						
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
					if (namedEntity.length() < 3) {
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin();
						end = ne.getEnd();
					} else {
						writeEntDocOffsets(entDocOffsets, begin, end, dn.getNumber(), entityFreqPerDoc, entIds, prevNeType,
								namedEntity);
						prevNeType = ne.getValue();
						namedEntity = new StringBuffer();
						namedEntity.append(ne.getCoveredText());
						begin = ne.getBegin();
						end = ne.getEnd();
					}					
				}
			}

			if (entityFreqPerDoc.size() > 0) {
				
				for (Long ent : entityFreqPerDoc.keySet()) {
					docEntity.write(dn.getNumber() + TAB + ent + TAB + entityFreqPerDoc.get(ent) + LF);
					entIds.get(ent).setFrequency(entIds.get(ent).getFrequency() + entityFreqPerDoc.get(ent));
				}
			}

			entDocOffsets.close();
			docEntity.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		try {
			if (!(new File(outputFolder, "entity.tsv").exists())){
				FileUtils.forceMkdir(outputFolder);
				new File(outputFolder, "entity.tsv").createNewFile();
			}
			FileWriter entity = new FileWriter(new File(outputFolder, "entity.tsv"), true);

			for (Entity ent : ents) {
				entity.write(ent.getId() + TAB + ent.getName() + TAB + ent.getType() + TAB + ent.getFrequency() + TAB
						+ false + LF);
			}
			entity.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.collectionProcessComplete();
	}

	private void writeEntDocOffsets(FileWriter entDocOffsets, int begin, int end, long dnum,
			Map<Long, Integer> entityFreqPerDoc, Map<Long, Entity> entIds, String prevNeType, StringBuffer namedEntity)
			throws IOException {
		Entity ent = addEntity(namedEntity.toString(), prevNeType.substring(2));
		if (entityFreqPerDoc.keySet().contains(ent.getId())) {
			entityFreqPerDoc.put(ent.getId(), entityFreqPerDoc.get(ent.getId()) + 1);
		} else {
			entityFreqPerDoc.put(ent.getId(), 1);
			entIds.put(ent.getId(), ent);
		}
		entDocOffsets.write(ent.getId() + TAB + begin + TAB + end + TAB + dnum + LF);
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
}