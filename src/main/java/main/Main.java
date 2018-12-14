package main;

import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try {
            /*
            Main as controller for the prefiltering of suspicious document
            To-Dos: 1) Hand over the document path to the FileReader
                    2) FileReader converts *.txt into a string
                    3) Hand over to Filter
                    4) Shrink strings with filteroptions
                    5) Hand over filtered Strings to FileWriter
                    6) Convert string into *.txt
                    7) Run Sherlock over prefiltered *.txt
                    8) Read results
             */

            String susp =  FileReader.readFile("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/suspicious-document00001.txt");

            ArrayList<Filter.Shrink> shrinks = new ArrayList<Filter.Shrink>(); //file as Arraylist
            shrinks.add(Filter.Shrink.stopwords); //stopwords
            shrinks.add(Filter.Shrink.folding); //folding
            shrinks.add(Filter.Shrink.stemming); // stemming


            Filter.displayTokenUsingStopAnalyzer(shrinks, susp);


            try {
               ProcessBuilder pb = new ProcessBuilder("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock","-o");
               String string =  new CommandExecutor(pb).exec();
               //System.out.println("Der String is leer " + string);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
