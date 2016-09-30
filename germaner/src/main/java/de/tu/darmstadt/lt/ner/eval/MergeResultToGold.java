package de.tu.darmstadt.lt.ner.eval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.ivy.Main;

public class MergeResultToGold {

	public static void main(String[] args) throws IOException {
		LineIterator goldIt = FileUtils.lineIterator(new File("/home/seid/Desktop/tmp/engner/gold_testb.tsv"));
		LineIterator predIt = FileUtils.lineIterator(new File("/home/seid/Desktop/tmp/engner/pred.tsv"));
		boolean flag = false;
		OutputStream os = new FileOutputStream(new File("/home/seid/Desktop/tmp/engner/eval.tsv"));

		while (goldIt.hasNext()) {

			String goldLine = goldIt.next();
			String predLine = predIt.next();
			if (goldLine.isEmpty() && !flag) {
				flag = true;
				IOUtils.write("\n", os, "UTF8");
			} else if (goldLine.isEmpty()) {
				continue;
			} else {
				IOUtils.write(goldLine.replace("\t", " ") + " " + predLine.split("\t")[1] + "\n", os, "UTF8");
				flag = false;
			}

		}

	}
}
