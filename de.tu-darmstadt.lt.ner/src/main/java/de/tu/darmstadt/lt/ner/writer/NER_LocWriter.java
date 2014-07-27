package de.tu.darmstadt.lt.ner.writer;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tu.darmstadt.lt.ner.SolrRetriever;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

public class NER_LocWriter extends JCasConsumer_ImplBase
{
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException
	{
		for (NamedEntity t : JCasUtil.select(jCas, NamedEntity.class))
		{
			SolrRetriever.result.add(t.getCoveredText());
		}
	}
}