package de.tu.darmstadt.lt.ner.writer;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tu.darmstadt.lt.ner.types.DocumentNumber;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TokensPerSentenceWriter extends JCasConsumer_ImplBase {

	// Implement a consumer which prints the number of tokens in
	// each sentence
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println("JCAS");
		DocumentNumber dn = JCasUtil.select(jcas, DocumentNumber.class).iterator().next();
		System.out.println("DN: "+ dn.getNumber());
		for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
			int numTokens = 0;
			System.out.println("Sentence: " + sentence.getCoveredText());
			System.out.print("Number of tokens: ");
			
			numTokens += JCasUtil.selectCovered(jcas, Token.class,sentence).size();
			
			System.out.println(numTokens);
		}
	}

}
