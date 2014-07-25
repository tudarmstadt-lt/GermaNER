package de.tu.darmstadt.lt.lqa.webapp.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PreprocessNERTrainingData
{
	public static void main(String[] args)
	{
		PreprocessNERTrainingData me = new PreprocessNERTrainingData();
		me.start();
	}

	public void start()
	{
		try
		{
			FileReader fr = new FileReader("ner_eng.full");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter("ner_eng_transformed.full");
			BufferedWriter bw = new BufferedWriter(fw);

			String line;
			String latestNE_Tag = "";
			while ((line = br.readLine()) != null)
			{
				if (line.isEmpty())
				{
					bw.write("\n");
					continue;
				}

				String[] parts = line.split(" ");
				String POS_Tag = parts[1];
				String NE_Tag = parts[2];

				bw.write(parts[0] + " " + POS_Tag + " " + " " +
						transformFormat(NE_Tag, latestNE_Tag) + "\n");
				latestNE_Tag = NE_Tag;
			}

			br.close();
			fr.close();
			bw.close();
			fw.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String transformFormat(String newTag, String oldTag)
	{
		if (!newTag.equals(oldTag) && newTag.indexOf("-") != -1)
		{
			String[] parts = newTag.split("-");
			if (parts[0].equals("I")) {
                return "B-" + parts[1];
            }
		}

		return newTag;
	}
}