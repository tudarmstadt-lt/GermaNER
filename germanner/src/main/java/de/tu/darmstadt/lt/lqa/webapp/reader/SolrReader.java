package de.tu.darmstadt.lt.lqa.webapp.reader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfigurationInterface;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.LeafNode;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class SolrReader extends JCasCollectionReader_ImplBase
{
	public static final String DATAURL = "DataUrl";
	@ConfigurationParameter(name = DATAURL,
			description = "The url of the Solr Index", mandatory = true)
	private String url;
	public static final String QUERYWORD = "QueryWord";
	@ConfigurationParameter(name = QUERYWORD,
			description = "A search term for finding articles for NER.",
			mandatory = true)
	private String queryWord;
	private String[] docs;
	private int docCounter = 0;
	
	private Compiler compiler;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException
	{
		super.initialize(context);
		try
		{
			// Set-up a simple wiki configuration
			SimpleWikiConfiguration config = new SimpleWikiConfiguration("classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");

			// Instantiate a compiler for wiki pages
			compiler = new Compiler(config);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		
		docs = querySolr(queryWord);
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException
	{
		if (docs == null)
			return false;
		
		//return docCounter < docs.length - 1;
		return docCounter == 0;
	}

	@Override
	public Progress[] getProgress()
	{
		return new Progress[] { new ProgressImpl(docCounter, 1, Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas j) throws IOException, CollectionException
	{
		DocumentMetaData dmd = new DocumentMetaData(j);
		dmd.setDocumentId(this.url);
		dmd.setDocumentUri(url);
		dmd.addToIndexes();
		
		StringBuilder allDocs = new StringBuilder();
		for (String s : docs)
		{
			allDocs.append(s);
			allDocs.append("\n");
		}
		
		j.setDocumentText(allDocs.toString());
		j.setDocumentLanguage("en");
		docCounter++;
	}

	public String[] querySolr(String query)
	{
		try
		{
			TreeMap<String, String> tm = new TreeMap<String, String>();
			tm.put("q", query);
			tm.put("wt", "json");
			tm.put("rows", "10");

			JSONParser p = new JSONParser();
			String response = getResponseFromUrl(url + "/select", tm);
			JSONObject json = (JSONObject) p.parse(response);
			JSONObject resp = (JSONObject) (p.parse(json.get("response").toString()));
			JSONArray docs = (JSONArray) (resp.get("docs"));

			String[] retVal = new String[docs.size()];
			for (int i = 0; i < docs.size(); i++)
			{
				JSONObject doc = (JSONObject)docs.get(i);
				String txt = StringEscapeUtils.unescapeJava(doc.get("text2").toString());
				retVal[i] = StringEscapeUtils.escapeHtml(cleanWikiText(doc.get("title").toString(), txt).replace("\n", " "));
			}

			return retVal;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private String cleanWikiText(String title, String wikiText)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			WikiConfigurationInterface config = compiler.getWikiConfig();
			// Retrieve a page
			PageTitle pageTitle = PageTitle.make(config, title);
			PageId pageId = new PageId(pageTitle, -1);
			
			// Compile the retrieved page
			CompiledPage cp = compiler.postprocess(pageId, wikiText, null);
			
			for (AstNode n : cp.getPage().getContent())
				sb.append(getStringOfASTNode(n));
		}
		catch (CompilerException e)
		{
			e.printStackTrace();
		}
		catch (LinkTargetException e)
		{
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private String getStringOfASTNode(AstNode a)
	{
		if (a instanceof LeafNode)
		{
			AstNodePropertyIterator iter = a.propertyIterator();
			while (iter.next())
			{
				if (iter.getName().equals("content"))
					return iter.getValue().toString();
			}
			
			return "";
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < a.size(); i++)
				sb.append(getStringOfASTNode((AstNode)a.get(i)));
			
			return sb.toString();
		}
	}

	public String getResponseFromUrl(String url, Map<String, String> parameters) throws IOException
	{
		String USER_AGENT = "Mozilla/5.0";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		if (!parameters.isEmpty())
		{
			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "";

			for (String key : parameters.keySet())
				urlParameters += "&" + key + "=" + parameters.get(key);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters.substring(1));
			wr.flush();
			wr.close();
		}

		int responseCode = con.getResponseCode();
		if (responseCode != 200)
			return con.getResponseMessage();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);

		in.close();

		return response.toString();
	}
}