package de.tu.darmstadt.lt.ner.reader;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;

import de.tu.darmstadt.lt.ner.PositionFeature;
import de.tu.darmstadt.lt.ner.SttsFeature;
import de.tu.darmstadt.lt.ner.UnivPosFeature;
import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tu.darmstadt.lt.ner.types.SimilarWord1;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NERReader extends JCasAnnotator_ImplBase
{
	public static final String CONLL_VIEW = "ConnlView";
	private Logger logger = null;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException
	{
		super.initialize(context);
		logger = context.getLogger();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		JCas docView;
		String tbText;
		try
		{
			docView = jcas.getView(CAS.NAME_DEFAULT_SOFA);
			tbText = jcas.getView(CONLL_VIEW).getDocumentText();
		}
		catch (CASException e)
		{
			throw new AnalysisEngineProcessException(e);
		}
		// a new sentence always starts with a new line
		if (tbText.charAt(0) != '\n') {
            tbText = "\n" + tbText;
        }

		String[] tokens = tbText.split("(\r\n|\n)");
		Sentence sentence = null;
		int idx = 0;
		Token token = null;
		POS posTag;
		GoldNamedEntity NamedEntityTag;
		String pos;
		String NamedEntity;
		boolean initSentence = false;
		StringBuffer docText = new StringBuffer();
		for (String line : tokens)
		{
			// new sentence if there's a new line
			if (line.equals(""))
			{
				if (sentence != null && token != null)
				{
					terminateSentence(sentence, token, docText);
					docText.append("\n");
					idx++;
				}
				// init new sentence with the next recognized token
				initSentence = true;
			}
			else
			{
				String[] tag = line.split("\\t");
				String word = tag[0];
				pos = tag.length >= 15 ? tag[14] : "";
				NamedEntity = tag.length >= 19 ? tag[19] : "";
				PositionFeature.pos.add(Integer.parseInt(tag[1]));
				UnivPosFeature.pos.add(tag[12]);
				SttsFeature.pos.add(tag[14]);
				docText.append(word);
				if (!word.matches("^(\\p{Punct}).*"))
				{
					token = new Token(docView, idx, idx + word.length());
					posTag = new POS(docView, idx, idx + word.length());
					NamedEntityTag = new GoldNamedEntity(docView, idx, idx + word.length());
					//sw=new SimilarWord1(docView, idx, idx + word.length());
					docText.append(" ");
					idx++;
				}
				else
				{
					if ((docText.length() - word.length()) > 0 && (docText.charAt(idx - word.length()) == ' '))
					{
						docText.deleteCharAt(idx - word.length());
						idx--;
					}
					token = new Token(docView, idx, idx + word.length());
					posTag = new POS(docView, idx, idx + word.length());
					NamedEntityTag = new GoldNamedEntity(docView, idx, idx + word.length());
					//sw=new SimilarWord1(docView, idx, idx + word.length());
				}
				// start new sentence
				if (initSentence)
				{
					sentence = new Sentence(docView);
					sentence.setBegin(token.getBegin());
					initSentence = false;
				}
				// increment actual index of text
				idx += word.length();
				// set POS value and add POS to the token and to the index
				posTag.setPosValue(pos);
				NamedEntityTag.setNamedEntityType(NamedEntity);
				
				//sw.setValue(tag[16]);
				
				//sw.addToIndexes();
				
				token.setPos(posTag);
				NamedEntityTag.addToIndexes();
				token.addToIndexes();
				
				
				
				logger.log(Level.FINE, "Token: [" + docText.substring(token.getBegin(), token.getEnd()) + "]" + token.getBegin() + "\t" + token.getEnd());
				logger.log(Level.FINE, "NamedEnity: [" + docText.substring(NamedEntityTag.getBegin(), NamedEntityTag.getEnd()) + "]" + NamedEntityTag.getBegin() + "\t" + NamedEntityTag.getEnd());
			}
		}
		if (sentence != null && token != null) {
            terminateSentence(sentence, token, docText);
        }

		docView.setSofaDataString(docText.toString(), "text/plain");
	}

	private void terminateSentence(Sentence sentence, Token token, StringBuffer docText)
	{
		sentence.setEnd(token.getEnd());
		sentence.addToIndexes();
		logger.log(Level.FINE, "Sentence:[" + docText.substring(sentence.getBegin(), sentence.getEnd()) + "]\t" + sentence.getBegin() + "\t" + sentence.getEnd());
	}
}