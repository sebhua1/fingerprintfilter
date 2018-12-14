package filter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;

    /*
    Filters strings with lucene
    Lucene Analyzer requires Tokenstreams to analyze text
     */


public class Filter {

    public enum Shrink{  // Filteroptions set as enumerations
        stemming,
        folding,
        stopwords
    }

    public static void displayTokenUsingStopAnalyzer(List<Shrink> shrink, String text) throws IOException {
        for (Shrink item : shrink) {
            TokenStream tokenStream = new SimpleAnalyzer().tokenStream(null, new StringReader(text));
            switch (item) {
                case stemming:
                    tokenStream = new PorterStemFilter(tokenStream);
                    break;
                case folding:
                    tokenStream = new ASCIIFoldingFilter(tokenStream);
                    break;
                case stopwords:
                    tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
                    break;
            }
            text = filterText(tokenStream);
        }


    }

    private static String filterText(TokenStream tokenStream) throws IOException {
        CharTermAttribute charTerm = tokenStream.addAttribute(CharTermAttribute.class);
        String newText = "";
        try {
            tokenStream.reset(); // Resets tokenstream to beginning
            while (tokenStream.incrementToken()) { //loop for each filtered word
                newText += charTerm.toString() + " ";
                System.out.println(charTerm.toString()); //displays every word after filtering
            }
            tokenStream.end();   // Perform end-of-stream operations, set final offset.
        } finally {
            tokenStream.close(); // Release resources associated with tokenstream
        }
        return newText;
    }

}
