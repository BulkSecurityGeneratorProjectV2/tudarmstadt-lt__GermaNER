package de.tu.darmstadt.lt.ner.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class NewsleakCSVReader extends JCasCollectionReader_ImplBase {

	public static final String PARAM_DIRECTORY_NAME = "DirectoryName";
	@ConfigurationParameter(name = PARAM_DIRECTORY_NAME, description = "The name of the directory of text files to be read", mandatory = true)
	private File dir;

	List<File> documents;
	int i = 1;
	CSVReader reader;
	String[] nextLine = null;
	boolean hasNext = true;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
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

		return hasNext;
	}
	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(1, 1, Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas j) throws IOException, CollectionException {
		StringBuffer sb = new StringBuffer();
		int begin = 0;
		String prev = "";
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine[0].isEmpty()){
				continue;
			}
			try{
			String dcNum = nextLine[0];
			System.out.println("Line="+ dcNum);
			String text = nextLine[1];
			prev = text;
			System.out.println("yes");
			DocumentNumber dn = new DocumentNumber(j, begin, begin + text.length()-1);
			dn.setNumber(Integer.valueOf(dcNum));
			dn.setText(text);
			dn.addToIndexes();
			createSentence(j, begin, begin + text.length()-1);
			sb.append(text );
			begin = begin + text.length();
			}
			catch (Exception e){
				System.out.println("problem");
				System.out.println(prev);
			}
		}
		hasNext = false;
		j.setDocumentText(sb.toString());
	}

	protected Sentence createSentence(final JCas aJCas, final int aBegin, final int aEnd) {
		Sentence seg = new Sentence(aJCas, aBegin, aEnd);
		seg.addToIndexes(aJCas);
		return seg;
	}
}
