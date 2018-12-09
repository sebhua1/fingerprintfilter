package filter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

        Analyzer fil;

        if(shrink.contains(Shrink.stemming)){ //decides which filter should be used (if folding is entered in main() stopwords are filtered!)
            fil = new EnglishAnalyzer();  //stopwords & stemming filtering
        } else {
            fil = new StandardAnalyzer(); //stopwords filtering
        }

        TokenStream tokenStream = fil.tokenStream(null, new StringReader(text));

        if(shrink.contains(Shrink.stopwords)){
            tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        }

        CharTermAttribute charTerm = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset(); // Resets tokenstream to beginning
            while (tokenStream.incrementToken()) { //loop for each filtered word

                System.out.println(charTerm.toString()); //displays every word after filtering


            }
            tokenStream.end();   // Perform end-of-stream operations, set final offset.
        } finally {
            tokenStream.close(); // Release resources associated with tokenstream
        }
    }


}
