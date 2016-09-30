package de.tu.darmstadt.lt.ner.eval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Stats {

	public static void main(String[] args) throws IOException {
		List<String> gold = new ArrayList<>();
		List<String> pred = new ArrayList<>();
		double tp = 0, fp = 0, fn = 0;
		LineIterator goldIt = FileUtils.lineIterator(new File("/home/seid/Desktop/tmp/engner/gold_testb.tsv"));
		LineIterator predIt = FileUtils.lineIterator(new File("/home/seid/Desktop/tmp/engner/pred.tsv"));
		while (goldIt.hasNext()) {
			String goldLine = goldIt.next();
			String predLine = predIt.next();
			if (goldLine.isEmpty())
				continue;
			gold.add(goldLine.split("\t")[1]);
			pred.add(predLine.split("\t")[1]);
		}
		for (int i = 0; i < gold.size() - 1; i++) {
			if (gold.get(i).equals(pred.get(i)) && gold.get(i).equals("O")) {
				continue;
			}
			if (gold.get(i).equals(pred.get(i)) ) {
				tp++;
			} else if (gold.get(i).equals("O")) {
				fp++;
			} else {
				fn++;
			}
		}
		System.out.println(tp);
		System.out.println(fp);
		System.out.println(fn);
		
		double p = tp / (tp + fp);
		double r = tp / (tp + fn);
		
		double ac = (tp+fn)/(tp+fp+fn);
		double f1 = 2*((p*r)/(p+r));
		
		System.out.println("p="+p+" r="+r + " f1="+f1 +"ac ="+ac);
	}
}
