package main;

import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

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
            /*
                scan all source documents from directory, filters them and creates new files with filtered texts in new directory
                Sums up filesizes before and after filtering
            */
            File srcDir = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/src/");
            Iterator<File> srcIterator = FileUtils.iterateFiles(srcDir, null, false);

            long shrinkedFileSize = 0;
            long fileSize = 0;

            while(srcIterator.hasNext()){
                File srcFile = srcIterator.next();
                fileSize += srcFile.length();
                File shrinkedSrcFile = writeFileInDirectory("src", srcFile);
                shrinkedFileSize += shrinkedSrcFile.length();
            }
            //scan all suspiscious documents from directory, filters them and creates new files with filtered texts in new directory
            File suspDir = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/");
            Iterator<File> suspIterator = FileUtils.iterateFiles(suspDir, null, false);
            while(suspIterator.hasNext()){
                File suspFile = suspIterator.next();
                fileSize += suspFile.length();
                File shrinkedSrcFile = writeFileInDirectory("susp", suspFile);
                shrinkedFileSize += shrinkedSrcFile.length();
            }

            // Displays filesizes before and after filtering
            System.out.format("The size of the file: %d MB\n", fileSize / (1024 * 1024));
            System.out.format("The size of the shrinked file: %d MB\n", shrinkedFileSize / (1024 * 1024));

            //Runs Sherlock for plagiarism detection
            String result = checkPlagiarismn(new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-src/"), new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-susp/"));

            //Deletes results where files from the same directory are compared to eachother
            StringBuilder filteredResult = new StringBuilder();
            Scanner scanner = new Scanner(result);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                File partpath1 = new File (parts[0]);
                File partpath2 = new File (parts[1]);
                if (partpath1.getParent().hashCode()!=partpath2.getParent().hashCode()){

                    filteredResult.append(partpath1.getName()).append(" - ").append(partpath2.getName()).append(" : ").append(parts[2]).append("\n");
                }
            }
            scanner.close();

            //Creates cleaned resultsfile
            File endResult = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result.txt");
            FileUtils.writeStringToFile(endResult, filteredResult.toString(), StandardCharsets.UTF_8);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method for filtering a document
    private static File writeFileInDirectory (String dir, File currentFile) throws  IOException{
        String susps = FileReader.readFile(currentFile.getPath());
        ArrayList<Filter.Shrink> shrinks = new ArrayList<>(); //file as Arraylist
        shrinks.add(Filter.Shrink.stopwords); //stopwords
        shrinks.add(Filter.Shrink.folding); //folding
        shrinks.add(Filter.Shrink.stemming); // stemming
        String newtext = Filter.displayTokenUsingStopAnalyzer(shrinks, susps);
        File newFile = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-" + dir + "/"  + currentFile.getName());
        FileUtils.writeStringToFile(newFile, newtext, StandardCharsets.UTF_8);
        return newFile;
    }

    //Method for executing Sherlock
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    private static String checkPlagiarismn(File file, final File suspFile){
        String output = "";
        try {
            String sourceFilePath = file.getAbsolutePath();
            String candidateFilePath = suspFile.getAbsolutePath();
            output = new CommandExecutor("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock",
                    "-t",
                    "5","-e", "txt",
                    sourceFilePath,
                    candidateFilePath
            ).exec();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

}

