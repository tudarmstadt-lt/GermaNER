package de.tu.darmstadt.lt.ner.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.tu.darmstadt.lt.ner.types.DocumentNumber;

public class NewsleakCSVReader2ManyCas extends JCasCollectionReader_ImplBase {

	public static final String PARAM_DIRECTORY_NAME = "DirectoryName";
	@ConfigurationParameter(name = PARAM_DIRECTORY_NAME, description = "The name of the directory of text files to be read", mandatory = true)
	private File dir;

	List<File> documents;
	int i = 1;
	Iterable<CSVRecord> records;
	CSVRecord record;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			records = CSVFormat.RFC4180.parse(new FileReader(dir));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		try {
			record = records.iterator().next();
		} catch (NoSuchElementException e) {
			record = null;
		}
		return record != null;
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(i, i, Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas j) throws IOException, CollectionException {

		String text = record.get(1).replaceAll("__COLON__", ":").replaceAll("__BACKSLASH__", "\\\\");
		String dcNum = record.get(0);
		DocumentNumber dn = new DocumentNumber(j);
		dn.setNumber(Integer.valueOf(dcNum));
		dn.setText(text);
		dn.addToIndexes();
		i++;
		j.setDocumentText(text);

	}

}
