package main;

import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;
import org.apache.commons.io.FileUtils;

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

                String susp = FileReader.readFile("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/suspicious-document00001.txt");
                ArrayList<Filter.Shrink> shrinks = new ArrayList<Filter.Shrink>(); //file as Arraylist
                shrinks.add(Filter.Shrink.stopwords); //stopwords
                shrinks.add(Filter.Shrink.folding); //folding
                shrinks.add(Filter.Shrink.stemming); // stemming


                Filter.displayTokenUsingStopAnalyzer(shrinks, susp);
                FileUtils.touch(new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/Filtered/result.txt"));
 /*               String filterpath = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/Filtered/result.txt").getPath();
                Files.write(filterpath,shrinks,Charset.defaultCharset());

            //loop to scan all files from directory and filter them
            //To-Dos:   1) Write filtered strings into new file
            //          2) Create and write a filesize into new directory

            File dir = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/");

            int x = 1; //gives the created files a number/name

            for (File file : dir.listFiles()) {
                String susps = FileReader.readFile(file.getPath());
                ArrayList<Filter.Shrink> shrinks = new ArrayList<Filter.Shrink>(); //file as Arraylist
                shrinks.add(Filter.Shrink.stopwords); //stopwords
                shrinks.add(Filter.Shrink.folding); //folding
                shrinks.add(Filter.Shrink.stemming); // stemming


                Filter.displayTokenUsingStopAnalyzer(shrinks, susps);

                //create new file in given directory with x in filename, where x counts up
                FileUtils.touch(new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/Filtered/" +x+ ".txt"));
                x++;
                //Problem: Writing the filtered text into the new file. susps is the unfiltered string.

                //Tests "for" loop
                //System.out.print("Directory" + file.getPath());



            }



            //FileUtils.writeStringToFile(file, susps);       //writes susp (unfiltered String...) to newly created file
*/

            //Filesize before filter: To-Dos: integrate in loop, filezie before and after filter
            String fileName = "/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/suspicious-document00001.txt";

            File f = new File(fileName);
            long fileSize = susp.length(); //5684 bytes zu 5683 bytes????
            System.out.format("The size of the file: %d bytes\n", fileSize);

            //Integrate Sherlock: Take all prefiltered files and compare them
            try {
              /*ProcessBuilder pb = new ProcessBuilder("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock"
                      ,"-t","0",
                      "/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/susp/suspicious-document00001.txt",
                      "/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/src/source-document00001.txt");
               String string =  new CommandExecutor(pb).exec();
               System.out.println("Der String is leer " + string);*/
                String sourceFilePath = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/susp/suspicious-document00001.txt").getAbsolutePath();
                String candidateFilePath = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/src/source-document00001.txt").getAbsolutePath();
                System.out.println("Hallo");
                String output = new CommandExecutor("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock",
                        "-t",
                        "0","-o",
                        sourceFilePath,
                        candidateFilePath
                ).exec();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

