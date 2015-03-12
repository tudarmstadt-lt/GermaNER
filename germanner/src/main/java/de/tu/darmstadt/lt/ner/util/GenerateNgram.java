package de.tu.darmstadt.lt.ner.util;

import java.util.ArrayList;
import java.util.List;

public class GenerateNgram
{
    public static void main(String[] args)
    {
        System.out.println(generateNgramsUpto("I like to go to the city by now", 3));
    }

    public static List<String> generateNgramsUpto(String text, int n)
    {
        // Use .isEmpty() instead of .length() == 0
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("null or empty text");
        }
        String[] words = text.split(" ");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder keyBuilder = new StringBuilder(words[i].trim());
            for (int j = 1; j < n - 1; j++) {
                keyBuilder.append(' ').append(words[i + j].trim());
            }
            String key = keyBuilder.toString();
            list.add(key+" "+ words[i + n - 1]);
        }
        return list;
    }
}
