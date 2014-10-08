package de.tu.darmstadt.lt.ner.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class EvaluatedNERWriter
    extends JCasConsumer_ImplBase
{
    public static final String OUTPUT_FILE = "OutputFile";

    @ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
    private File OutputFile = null;

    public static final String LF = System.getProperty("line.separator");

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        try {
            FileWriter fw = new FileWriter(OutputFile);

            StringBuilder sb;
            for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
                for (NamedEntity a : JCasUtil.selectCovered(jcas, NamedEntity.class,
                        sentence.getBegin(), sentence.getEnd())) {
                    sb = new StringBuilder();
                    sb.append(a.getCoveredText().replace(" ", ""));
                    sb.append(" ");
                    sb.append(JCasUtil.selectCovered(jcas, GoldNamedEntity.class, a).get(0)
                            .getNamedEntityType());
                    sb.append(" ");
                    sb.append(a.getValue());
                    sb.append(LF);
                    fw.write(sb.toString());
                }
                fw.write(LF);

            }
            fw.close();

            getContext().getLogger().log(Level.INFO,
                    "Output written to: " + OutputFile.getAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}