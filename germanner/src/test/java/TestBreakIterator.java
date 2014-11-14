/*******************************************************************************
 * Copyright 2014
 * FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
import com.ibm.icu.text.BreakIterator;

public class TestBreakIterator
{

    public static void main(String args[])
    {

            String stringToExamine = "This is the man. That is the woman. Both are fat";
            // print each word in order
            BreakIterator boundary = BreakIterator.getWordInstance();
            boundary.setText(stringToExamine);
            printEachForward(boundary, stringToExamine);
            // print each sentence in reverse order
            //boundary = BreakIterator.getSentenceInstance(Locale.US);
           // boundary.setText(stringToExamine);
    }

    public static void printEachForward(BreakIterator boundary, String source) {
        int start = boundary.first();
        for (int end = boundary.next();
             end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
             System.out.print(source.substring(start,end));
        }
    }


}
