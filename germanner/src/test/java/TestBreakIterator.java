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
