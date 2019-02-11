package main;

import PANXMLreader.readXML;
import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static java.lang.String.*;
import static java.nio.charset.StandardCharsets.*;

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
                    9) File sizes
                    10)Timestamps


                    Compare results.txt and output a final results with results in common, missed and to much


                scan all source documents from directory, filters them and creates new files with filtered texts in new directory
                Sums up filesizes before and after filtering
            */

            //Folding filter
            long startTimeMainScrFilterNoCompare = System.currentTimeMillis();
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
            long runtimeSrcFilter = takeTime(startTimeMainScrFilterNoCompare);
            System.out.print("Source files filtered\n");


            //scan all suspiscious documents from directory, filters them and creates new files with filtered texts in new directory
            long startTimeSuspFilter = System.currentTimeMillis();
            File suspDir = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/susp/");
            Iterator<File> suspIterator = FileUtils.iterateFiles(suspDir, null, false);

            while(suspIterator.hasNext()){
                File suspFile = suspIterator.next();
                fileSize += suspFile.length();
                File shrinkedSuspFile = writeFileInDirectory("susp", suspFile);
                shrinkedFileSize += shrinkedSuspFile.length();
            }

            long runtimeSuspFilter = takeTime(startTimeSuspFilter);
            System.out.println("Suspicious files filtered");
            System.out.println("File sizes calculated");

            //Runs Sherlock for plagiarism detection
            File srcFolder = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-src/");
            File suspFolder = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-susp/");

            long startTimePlagiarismCheck = System.currentTimeMillis();
            String config1 = checkPlagiarism( srcFolder, suspFolder, "2", "3");
            long runtimePCConfig1 = takeTime(startTimePlagiarismCheck);
            long startTimePCconfig2 = System.currentTimeMillis();
            String config2 = checkPlagiarism( srcFolder, suspFolder, "2", "4");
            long runtimePCConfig2 = takeTime(startTimePCconfig2);
            long startTimePCconfig3 = System.currentTimeMillis();
            String config3 = checkPlagiarism( srcFolder, suspFolder, "2", "5");
            long runtimePCConfig3 = takeTime(startTimePCconfig3);
            long startTimePCconfig4 = System.currentTimeMillis();
            String config4 = checkPlagiarism( srcFolder, suspFolder, "4", "3");
            long runtimePCConfig4 = takeTime(startTimePCconfig4);
            long startTimePCconfig5 = System.currentTimeMillis();
            String config5 = checkPlagiarism( srcFolder, suspFolder, "4", "4");
            long runtimePCConfig5 = takeTime(startTimePCconfig5);
            long startTimePCconfig6 = System.currentTimeMillis();
            String config6 = checkPlagiarism( srcFolder, suspFolder, "4", "5");
            long runtimePCConfig6 = takeTime(startTimePCconfig6);
            long startTimePCconfig7 = System.currentTimeMillis();
            String config7 = checkPlagiarism( srcFolder, suspFolder, "6", "3");
            long runtimePCConfig7 = takeTime(startTimePCconfig7);
            long startTimePCconfig8 = System.currentTimeMillis();
            String config8 = checkPlagiarism( srcFolder, suspFolder, "6", "4");
            long runtimePCConfig8 = takeTime(startTimePCconfig8);
            long startTimePCconfig9 = System.currentTimeMillis();
            String config9 = checkPlagiarism( srcFolder, suspFolder, "6", "5");
            long runtimePCConfig9 = takeTime(startTimePCconfig9);


            System.out.print("Checked for plagiarism\n");



            createResultsFile(config1, 1);
            createResultsFile(config2, 2);
            createResultsFile(config3, 3);
            createResultsFile(config4, 4);
            createResultsFile(config5, 5);
            createResultsFile(config6, 6);
            createResultsFile(config7, 7);
            createResultsFile(config8, 8);
            createResultsFile(config9, 9);

            System.out.print("Created results file\n");


            System.out.print("Completed comparisom with unfiltered results\n");

            long runtimeMain = takeTime(startTimeMainScrFilterNoCompare);

            //Creates a file with runtimes, filesizes and number of results
            BufferedWriter relevantWriter = new BufferedWriter(new FileWriter
                    ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/log.txt"));
            relevantWriter.write("Filtered source files in: " + runtimeSrcFilter + "ms\n");
            relevantWriter.write("Filtered suspicious files in: " + runtimeSuspFilter + "ms\n");
            relevantWriter.write("Runtime plagiarism check config1 = " +runtimePCConfig1 + "ms\n" +
                    "Runtime plagiarism check config2 = " +runtimePCConfig2 + "ms\n"+
                    "Runtime plagiarism check config3 = " +runtimePCConfig3 + "ms\n"+
                    "Runtime plagiarism check config4 = " +runtimePCConfig4 + "ms\n"+
                    "Runtime plagiarism check config5 = " +runtimePCConfig5 + "ms\n"+
                    "Runtime plagiarism check config6 = " +runtimePCConfig6 + "ms\n"+
                    "Runtime plagiarism check config7 = " +runtimePCConfig7 + "ms\n"+
                    "Runtime plagiarism check config8 = " +runtimePCConfig8 + "ms\n"+
                    "Runtime plagiarism check config9 = " +runtimePCConfig9 + "ms\n");
            relevantWriter.write("Total runtime: " + runtimeMain + "ms\n");
            relevantWriter.newLine();
            relevantWriter.write("Original file size: " + fileSize+ "bytes\n");
            relevantWriter.write("File size after filtering: " + shrinkedFileSize+ "bytes\n");




            File PANdir = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/02-no-obfuscation/");
            Iterator<File> panIterator1 = FileUtils.iterateFiles(PANdir, null, false);
            String PAN = "";
            while(panIterator1.hasNext()){
                File panFile = panIterator1.next();
                PAN += readXML.readXMLFile(panFile) + "\n";
            }

            System.out.println(PAN);
            File PanResult = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/PANtruth.txt");
            FileUtils.writeStringToFile(PanResult, PAN, UTF_8);

            File PANobfuscationDir = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/03-random-obfuscation/");
            Iterator<File> panIterator2 = FileUtils.iterateFiles(PANobfuscationDir, null, false);
            String PANobfus = "";
            while (panIterator2.hasNext()){
                File panFile2 = panIterator2.next();
                PANobfus += readXML.readXMLFile(panFile2) + "\n";
            }
            File PANobfuscation = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/PAN2.txt");
            FileUtils.writeStringToFile(PANobfuscation, PANobfus, UTF_8);




            for (int r = 1; r < 10; r++) {
                final Path firstFile = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-"+ r +"+20.txt");
                final Path secondFile = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/PAN2.txt");
                final List<String> firstFileContent = Files.readAllLines(firstFile,
                        Charset.defaultCharset());
                final List<String> secondFileContent = Files.readAllLines(secondFile,
                        Charset.defaultCharset());

                int diffCounter1 = diffFiles(firstFileContent, secondFileContent, 1, 0, r);
                int diffCounter2 = diffFiles(secondFileContent, firstFileContent, 2, 0, r);
                int commCounter = commonFiles(firstFileContent, secondFileContent, r);

                Path resultPathover20 = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-"+ r +"+20.txt");
                Path resultPathUnder20 = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-"+ r +"-20.txt");
                long lineCount = Files.lines(resultPathover20).count() + Files.lines(resultPathUnder20).count();

                float recall = (float)commCounter/(float)1000;
                long resultsOver20 = Files.lines(resultPathover20).count();
                long resultsUnder20 = Files.lines(resultPathUnder20).count();
                float precision = (float)commCounter/((float)resultsOver20);

                relevantWriter.write("Number of common results config" + r + ": " +commCounter + "\n");
                relevantWriter.write("Recall for config" + r + ": " + recall +"\n");
                relevantWriter.write("Precision for config" + r + ": " + precision + "\n");
                relevantWriter.write("Number of true positives and false positives: " + lineCount + "\n");
            }

            relevantWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e1){
            e1.printStackTrace();
        } catch (SAXException e2){
            e2.printStackTrace();
        }   catch (ParserConfigurationException e3){
                e3.printStackTrace();
        }
    }

    //Method for filtering a document
    private static File writeFileInDirectory (String dir, File currentFile) throws  IOException{
        String susps = FileReader.readFile(currentFile.getPath());
        ArrayList<Filter.Shrink> shrinks = new ArrayList<>(); //file as Arraylist

            shrinks.add(Filter.Shrink.folding); //folding

            //shrinks.add(Filter.Shrink.stopwords); //stopwords

            //shrinks.add(Filter.Shrink.stemming); // stemming
        String newtext = Filter.displayTokenUsingStopAnalyzer(shrinks, susps);
        File newFile = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-" + dir + "/"  + currentFile.getName());
        FileUtils.writeStringToFile(newFile, newtext, UTF_8);
        return newFile;
    }

    //Method for executing Sherlock
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    private static String checkPlagiarism(File file, final File suspFile, String zerobits, String words){
        String output = "";
        try {
            String sourceFilePath = file.getAbsolutePath();
            String candidateFilePath = suspFile.getAbsolutePath();
            output = new CommandExecutor("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock",
                    "-t",
                    "1", "-z", zerobits,"-n", words,"-e", "txt",
                    candidateFilePath,
                    sourceFilePath

            ).exec();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }
    private static int diffFiles(final List<String> firstFileContent,
                                          final List<String> secondFileContent, int i, int diffCounter, int r) throws IOException{
        final List<String> diff = new ArrayList<>();
        for (final String line : firstFileContent) {
            if (!secondFileContent.contains(line)) {
                diff.add(line + "\n");
                diffCounter++;
            }
        }
        String diffRes = join("", diff);
        if (i == 1) {
            File diff12 = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/diff12_config" + r + ".txt");
            FileUtils.writeStringToFile(diff12, diffRes);
        }else {
            File diff21 = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/diff21config" +  r + ".txt");
            FileUtils.writeStringToFile(diff21, diffRes);
        }
        return diffCounter;
    }
    private static int commonFiles (final List<String> firstFileContent,
                                             final List<String> secondFileContent, int r) throws IOException{
        final List<String> comm = new ArrayList<>();
        int commCounter = 0;
        for (final String line : firstFileContent){
            if (secondFileContent.contains(line)){
                comm.add(line + "\n"); //(firstFileContent.indexOf(line) + 1) + " " +
                commCounter++;
            }
        }
        String commResults = join("", comm);
        File commons = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/commons_config" + r + ".txt");
        FileUtils.writeStringToFile(commons, commResults);
        return commCounter;
    }
    private static long takeTime (long startTime) {
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        return runtime;
    }

    private static int createResultsFile (String result, int configNumber) throws IOException{
        StringBuilder filteredResultOverTwenty = new StringBuilder();
        StringBuilder filteredResultUnderTwenty = new StringBuilder();
        StringBuilder filteredResult = new StringBuilder();
        Scanner scanner = new Scanner(result);
        int resultsCounter = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            File partpath1 = new File(parts[0]);
            File partpath2 = new File(parts[1]);
            String similarityPercentage = parts [2];
            if (partpath1.getParent().hashCode() != partpath2.getParent().hashCode()) {
                String similarity =  StringUtils.removeEnd(similarityPercentage, "%");
                int similarityScore = Integer.parseInt(similarity);
                resultsCounter++;
                filteredResult.append(partpath1.getName()).append(" ").append(partpath2.getName()).append(" : ").append(parts[2]).append("\n");
                if (similarityScore >= 20){
                filteredResultOverTwenty.append(partpath1.getName()).append(" ").append(partpath2.getName()).append("\n"); //.append(" : ").append(parts[2])

                }else if (similarityScore > 4) {
                    filteredResultUnderTwenty.append(partpath1.getName()).append(" ").append(partpath2.getName()).append("\n");
                }
            }
        }
        scanner.close();

        //Creates cleaned resultsfile
        File endResult = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-" + configNumber + "+20.txt");
        FileUtils.writeStringToFile(endResult, filteredResultOverTwenty.toString(), UTF_8);
        File endResult2 = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-" + configNumber + "-20.txt");
        FileUtils.writeStringToFile(endResult2, filteredResultUnderTwenty.toString(), UTF_8);
        File endResultWithValue = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result-" + configNumber + ".txt");
        FileUtils.writeStringToFile(endResultWithValue, filteredResult.toString(), UTF_8);
        return resultsCounter;
    }

}



