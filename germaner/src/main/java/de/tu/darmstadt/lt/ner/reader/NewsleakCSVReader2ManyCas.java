package de.tu.darmstadt.lt.ner.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.opencsv.CSVReader;

import de.tu.darmstadt.lt.ner.types.DocumentNumber;

public class NewsleakCSVReader2ManyCas extends JCasCollectionReader_ImplBase {

	public static final String PARAM_DIRECTORY_NAME = "DirectoryName";
	@ConfigurationParameter(name = PARAM_DIRECTORY_NAME,
			description = "The name of the directory of text files to be read",
			mandatory = true)
	private File dir;

	List<File> documents;
	int i = 1;
    CSVReader reader;
    String[] nextLine = null;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		
		try {
				reader = new CSVReader(new FileReader(dir));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		nextLine = reader.readNext();
		return nextLine != null;
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(i, i,
				Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas j) throws IOException, CollectionException {
		String s = nextLine[1];
		DocumentNumber dn = new DocumentNumber(j);
		dn.setNumber(i);
		dn.addToIndexes();
		i++;
		j.setDocumentText(s);

	}

}
