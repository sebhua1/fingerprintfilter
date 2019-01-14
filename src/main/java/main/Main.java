package main;

import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;
import org.apache.commons.io.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
                    11)Memory usage

                    Count number of results - check
                    Compare results.txt and output a final results with results in common, missed and to much
            */
            /*
                scan all source documents from directory, filters them and creates new files with filtered texts in new directory
                Sums up filesizes before and after filtering
            */
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
                File shrinkedSrcFile = writeFileInDirectory("susp", suspFile);
                shrinkedFileSize += shrinkedSrcFile.length();
            }

            long runtimeSuspFilter = takeTime(startTimeSuspFilter);
            System.out.println("Suspicious files filtered");
            System.out.println("File sizes calculated");

            //Runs Sherlock for plagiarism detection
            long startTimePlagiarismCheck = System.currentTimeMillis();
            String result = checkPlagiarism(new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-src/"), new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/filtered-susp/"));
            long runtimePlagiarismCheck = takeTime(startTimePlagiarismCheck);
            System.out.print("Checked for plagiarism\n");

            //Deletes results where files from the same directory are compared to eachother
            StringBuilder filteredResult = new StringBuilder();
            Scanner scanner = new Scanner(result);
            int resultsCounter = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                File partpath1 = new File (parts[0]);
                File partpath2 = new File (parts[1]);
                if (partpath1.getParent().hashCode()!=partpath2.getParent().hashCode()){

                    filteredResult.append(partpath1.getName()).append(" - ").append(partpath2.getName()).append(" : ").append(parts[2]).append("\n");
                    resultsCounter++;
                }
            }
            scanner.close();

            //Creates cleaned resultsfile
            File endResult = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/result.txt");
            FileUtils.writeStringToFile(endResult, filteredResult.toString(), StandardCharsets.UTF_8);
            long runtimeNoCompare = takeTime(startTimeMainScrFilterNoCompare);
            System.out.print("Created results file\n");

            //Filtered results to a list without threshold
            long startTimeResultCompare = System.currentTimeMillis();
            String resultsCheck = FileReader.readFile(endResult.getPath());
            StringBuilder resultsCompare = new StringBuilder();
            Scanner resultsScanner = new Scanner(resultsCheck);
            while (resultsScanner.hasNextLine()) {
                String resultsLine = resultsScanner.nextLine();
                String[] resultsParts = resultsLine.split(":");
                File resultsPart = new File(resultsParts[0]);

                resultsCompare.append(resultsPart).append("\n");
            }
            //writes filtered results without threshold to a new txt file
            File resultsNoValue = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/resultsNoValue.txt");
            FileUtils.writeStringToFile(resultsNoValue, resultsCompare.toString(), StandardCharsets.UTF_8);

            //Unfiltered results to list
            String unfilteredResultsCheck = FileReader.readFile("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/resultNoFilter.txt");
            StringBuilder unfilteredResultsCompare = new StringBuilder();
            Scanner unfilteredResultsScanner = new Scanner(unfilteredResultsCheck);
            while (unfilteredResultsScanner.hasNextLine()) {
                String unfilteredResultsLine = unfilteredResultsScanner.nextLine();
                String[] unfilteredResultsParts = unfilteredResultsLine.split(":");
                File unfilteredResultsPart = new File(unfilteredResultsParts[0]);

                unfilteredResultsCompare.append(unfilteredResultsPart).append("\n");
            }

            //writes unfiltered results without threshold to a new txt file
            File unfilteredResultsNoValue = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/ufresultsNoValue.txt");
            FileUtils.writeStringToFile(unfilteredResultsNoValue, unfilteredResultsCompare.toString(), StandardCharsets.UTF_8);

            //Precision/recall: Filtered results minus unfiltered results, unfiltered results minus filtered results
            // and results in common
            final Path firstFile = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/resultsNoValue.txt");
            final Path secondFile = Paths.get("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/ufresultsNoValue.txt");
            final List<String> firstFileContent = Files.readAllLines(firstFile,
                    Charset.defaultCharset());
            final List<String> secondFileContent = Files.readAllLines(secondFile,
                    Charset.defaultCharset());

            int diffCounter1 = diffFiles(firstFileContent, secondFileContent, 1, 0);
            int diffCounter2 = diffFiles(secondFileContent, firstFileContent, 2, 0);
            int commCounter = commonFiles(firstFileContent, secondFileContent);

            long runtimeResultsCompare = takeTime(startTimeResultCompare);

            System.out.print("Completed comparisom with unfiltered results\n");

            //Memory usage
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long runtimeMain = takeTime(startTimeMainScrFilterNoCompare);
            long memory = runtime.totalMemory() - runtime.freeMemory();

            //Creates a file with runtimes, filesizes and number of results
            BufferedWriter relevantWriter = new BufferedWriter(new FileWriter
                    ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/log.txt"));
            relevantWriter.write("Filtered source files in: " + runtimeSrcFilter + "ms\n");
            relevantWriter.write("Filtered suspicious files in: " + runtimeSuspFilter + "ms\n");
            relevantWriter.write("Checked for plagiarism in: " + runtimePlagiarismCheck + "ms\n");
            relevantWriter.write("Total runtime without comparing: " + runtimeNoCompare + "ms\n");
            relevantWriter.write("Compared filtered and unfiltered results in: " + runtimeResultsCompare + "ms\n");
            relevantWriter.write("Total runtime: " + runtimeMain + "ms\n");
            relevantWriter.newLine();
            relevantWriter.write("Original file size: " + fileSize+ "bytes\n");
            relevantWriter.write("File size after filtering: " + shrinkedFileSize+ "bytes\n");
            relevantWriter.write ("Number of results: " +resultsCounter+ "\n");
            relevantWriter.write ("Number of different results (filtered - unfiltered): " + diffCounter1 + "\n");
            relevantWriter.write ("Number of different results (unfiltered - filtered): "+ diffCounter2 + "\n");
            relevantWriter.write("Number of common files: " + commCounter + "\n");
            relevantWriter.write("Memory used: " + memory + "bytes\n");
            relevantWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
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
        FileUtils.writeStringToFile(newFile, newtext, StandardCharsets.UTF_8);
        return newFile;
    }

    //Method for executing Sherlock
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    private static String checkPlagiarism(File file, final File suspFile){
        String output = "";
        try {
            String sourceFilePath = file.getAbsolutePath();
            String candidateFilePath = suspFile.getAbsolutePath();
            output = new CommandExecutor("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/sherlock",
                    "-t",
                    "50", "-z", "10","-e", "txt",
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
                                          final List<String> secondFileContent, int i, int diffCounter) throws IOException{
        final List<String> diff = new ArrayList<>();
        for (final String line : firstFileContent) {
            if (!secondFileContent.contains(line)) {
                diff.add((firstFileContent.indexOf(line) + 1) + " " + line + "\n");
                diffCounter++;
            }
        }
        String diffRes = String.join("", diff);
        if (i == 1) {
            File diff12 = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/diff12.txt");
            FileUtils.writeStringToFile(diff12, diffRes);
        }else {
            File diff21 = new File("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/diff21.txt");
            FileUtils.writeStringToFile(diff21, diffRes);
        }
        return diffCounter;
    }
    private static int commonFiles (final List<String> firstFileContent,
                                             final List<String> secondFileContent) throws IOException{
        final List<String> comm = new ArrayList<>();
        int commCounter = 0;
        for (final String line : firstFileContent){
            if (secondFileContent.contains(line)){
                comm.add((firstFileContent.indexOf(line) + 1) + " " + line + "\n");
                commCounter++;
            }
        }
        String commResults = String.join("", comm);
        File commons = new File ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/sherlock-master/commons.txt");
        FileUtils.writeStringToFile(commons, commResults);
        return commCounter;
    }
    private static long takeTime (long startTime) {
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        return runtime;
    }
}



