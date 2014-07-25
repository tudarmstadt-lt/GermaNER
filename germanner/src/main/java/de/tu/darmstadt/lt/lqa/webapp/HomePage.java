package de.tu.darmstadt.lt.lqa.webapp;

import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.uima.UIMAException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

public class HomePage extends WebPage
{
	private static final long serialVersionUID = 6086563866834153731L;
	QADetailForm detailForm;
	Label resultLabel;
	String resultValue;

	public HomePage()
	{
		add(detailForm = new QADetailForm("detailForm"));
	}

	private class QADetailForm extends Form<QADetailDataModel>
	{
		private static final long serialVersionUID = 2194820570994920884L;

		public QADetailForm(String id)
		{
			super(id, new CompoundPropertyModel<QADetailDataModel>(new QADetailDataModel()));
			add(new TextField<String>("question"));
			add(new Button("ask")
			{
				private static final long serialVersionUID = 2100461410327330520L;

				@Override
				public void onSubmit()
				{
					SolrRetriever sr = new SolrRetriever();
					try
					{
						resultValue = sr.queryDB(QADetailForm.this.getModelObject().question);
					}
					catch (UIMAException e)
					{
						error("unable to process the question:" + ExceptionUtils.getCause(e));
					}
					catch (IOException e)
					{
						error("unable to process the db:" + ExceptionUtils.getCause(e));
					}
					resultLabel.setDefaultModelObject(resultValue);
					resultLabel.setEscapeModelStrings(false);
				}
			});
			add(resultLabel = new Label("result"));
			resultLabel.setOutputMarkupId(true);
		}
	}

	private class QADetailDataModel implements Serializable
	{
		private static final long serialVersionUID = 6223579272408506439L;
		private String question = "";
		private String result = "";
	}
}