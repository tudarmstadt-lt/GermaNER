package de.tu.darmstadt.lt.ner.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChangeColon {

    
    public void run(String file1,String file2) throws FileNotFoundException, IOException {
        BufferedReader br=new BufferedReader( new FileReader(file1));
        BufferedWriter bw=new BufferedWriter(new FileWriter(file2));
        String input,output = null;
        while((input=br.readLine())!=null)
        {
            if(input.contains(":"))
            {
                output=input.replaceAll(":","__COLON__");
            }
            else
            {
                output=input;
            }
            bw.write(output+"\n");
        }
        bw.flush();
        bw.close();
        br.close();
    }
}