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
	private File OutputFolder = null;

	public static final String LF = System.getProperty("line.separator");
	public static final String TAB = "\t";

	List<Entity> ents = new ArrayList<Entity>(); // All entities
	List<Relation> rels = new ArrayList<Relation>();

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {

	
			DocumentNumber dn = JCasUtil.select(jCas, DocumentNumber.class).iterator().next();
			
			/*
			 * FileWriter entDocOffsets = new FileWriter(new File(OutputFolder,
			 * dn.getNumber()+"_entDocOffsets.tsv")); FileWriter docEntity = new
			 * FileWriter(new File(OutputFolder,
			 * dn.getNumber()+"_docEntity.tsv")); FileWriter entity = new
			 * FileWriter(new File(OutputFolder, dn.getNumber()+"_entity.tsv"));
			 * 
			 * FileWriter documentRelationship = new FileWriter(new
			 * File(OutputFolder, dn.getNumber()+"_documentRelationship.tsv"));
			 * FileWriter relationship = new FileWriter(new File(OutputFolder,
			 * dn.getNumber()+"_relationship.tsv"));
			 */
			FileWriter entDocOffsets = new FileWriter(new File(OutputFolder, "entDocOffsets.tsv"), true);
			FileWriter docEntity = new FileWriter(new File(OutputFolder, "docEntity.tsv"), true);

			FileWriter documentRelationship = new FileWriter(new File(OutputFolder, "documentRelationship.tsv"), true);

			int begin = 0, end = 0;

			/*
			 * for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			 */
			NavigableMap<Long, Integer> entityFreqPerDoc = new TreeMap<>();
			Map<Long, Entity> entIds = new HashMap<>();
			String prevNeType = "O";
			StringBuffer namedEntity = new StringBuffer();
			for (NamedEntity ne : JCasUtil.select(jCas, NamedEntity.class)) {
				if (ne.getValue().equals("O")) {
					if (prevNeType.equals("O")) {
						continue;
					} else {

						writeEntDocOffsets(entDocOffsets, begin, end, dn.getNumber(), entityFreqPerDoc, entIds,
								prevNeType, namedEntity);
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

					writeEntDocOffsets(entDocOffsets, begin, end, dn.getNumber(), entityFreqPerDoc, entIds, prevNeType,
							namedEntity);
					prevNeType = ne.getValue();
					namedEntity = new StringBuffer();
					namedEntity.append(ne.getCoveredText());
					begin = ne.getBegin();
					end = ne.getEnd();

				}
			}

			if (entityFreqPerDoc.size() > 0) {
				Map<Long, Relation> relIds = new HashMap<>();
				Map<Long, Integer> relationFreqPerDoc = createDocEntRel(docEntity, dn.getNumber(), entityFreqPerDoc,
						entIds, relIds);
				for (long rel : relationFreqPerDoc.keySet()) {
					documentRelationship.write(dn.getNumber() + TAB + rel + TAB + relationFreqPerDoc.get(rel) + LF);
					relIds.get(rel).setFrequency(relIds.get(rel).getFrequency() + relationFreqPerDoc.get(rel));
				}
			}
			/*
			 * }
			 */

			entDocOffsets.close();
			docEntity.close();

			documentRelationship.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub

		try {
			FileWriter entity = new FileWriter(new File(OutputFolder, "entity.tsv"), true);
			FileWriter relationship = new FileWriter(new File(OutputFolder, "relationship.tsv"), true);

			for (Entity ent : ents) {
				entity.write(ent.getId() + TAB + ent.getName() + TAB + ent.getType() + TAB + ent.getFrequency() + TAB
						+ false + LF);
			}

			for (Relation rel : rels) {
				relationship.write(rel.getId() + TAB + rel.getEntity1().getId() + TAB + rel.getEntity2().getId() + TAB
						+ rel.getFrequency() + TAB + false + LF);
			}
			entity.close();
			relationship.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.collectionProcessComplete();
	}

	private Map<Long, Integer> createDocEntRel(FileWriter docEntity, long dnum,
			NavigableMap<Long, Integer> entityFreqPerDoc, Map<Long, Entity> EntIds, Map<Long, Relation> relIds)
			throws IOException {
		
		for (Long ent : entityFreqPerDoc.keySet()) {
			docEntity.write(dnum + TAB + ent + TAB + entityFreqPerDoc.get(ent) + LF);
			EntIds.get(ent).setFrequency(EntIds.get(ent).getFrequency() + entityFreqPerDoc.get(ent));
		}
		
		Map<Long, Integer> relationFreqPerDoc = new HashMap<>();
		Long lastEnt = entityFreqPerDoc.lastKey();
		for (Long ent1 : entityFreqPerDoc.keySet()) {
			Map<Long, Integer> entity2FreqPerDoc = entityFreqPerDoc.subMap(ent1, false, lastEnt, true);
			for (Long ent2 : entity2FreqPerDoc.keySet()) {
				Relation rel = addRelation(EntIds.get(ent1), EntIds.get(ent2));
				relationFreqPerDoc.put(rel.getId(), Math.max(entityFreqPerDoc.get(ent1), entityFreqPerDoc.get(ent2)));
				relIds.put(rel.getId(), rel);
				/*if (relationFreqPerDoc.keySet().contains(rel.getId())) {
					relationFreqPerDoc.put(rel.getId(), relationFreqPerDoc.get(rel) + 1);
				} else {
					relationFreqPerDoc.put(rel.getId(), 1);
					relIds.put(rel.getId(), rel);
				}*/
			}
		}

		return relationFreqPerDoc;
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

	private Relation addRelation(Entity ent1, Entity ent2) {
		Relation newRel = new Relation(ent1, ent2);
		Relation newRelRev = new Relation(ent2, ent1);
		if (rels.contains(newRel)) {
			return rels.get(rels.indexOf(newRel));
		} else if (rels.contains(newRelRev)) {
			return rels.get(rels.indexOf(newRel));
		} else {
			newRel.setId(rels.size() + 1);
			rels.add(newRel);
			return newRel;
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