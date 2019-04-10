package main;

import PANXMLreader.PANData;
import command.CommandExecutor;
import filereader.FileReader;
import filter.Filter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.*;
import java.util.List;

import static PANXMLreader.readXML.readXMLFile;

import static java.nio.charset.StandardCharsets.*;

public class Main {

    private static final String PANPath = ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/PAN/"); //Insert here directory to PAN
    private static final String OriginalSherlockPath =
            ("/Users/sebastianhuang/IdeaProjects/fingerprintfilter/Sherlock-master-3/lib_source/"); // Insert here directory to Sherlock folder (not to Sherlock directly)
    private static final String NoPlagiarism = ("01-no-plagiarism");
    private static final String NoObfuscation = ("02-no-obfuscation");

    public static void main(String[] args) {
        try {


            //Creates a list with pairs of susp and src from the XML files of the no-plagiarism folder
            File PANNoObfuscationDir = new File (PANPath+NoPlagiarism);
            Iterator<File> PANIterator = FileUtils.iterateFiles(PANNoObfuscationDir, null, false);
            List<PANData> PANList = new ArrayList<>();

            while (PANIterator.hasNext()) {
                File NoObfuscationFile= PANIterator.next();
                PANData panData = readXMLFile(NoObfuscationFile, 0, 1);
                PANList.add(panData);
                int nodes = panData.getNodesItems();
                if (nodes > 1) {
                    for (int i = 1; i < nodes; i++) {
                        PANData panData1 = readXMLFile(NoObfuscationFile, i, 1);
                        PANList.add(panData1);
                    }
                }
            }

            //Adds pairs of corresponding text segments from XML files of the no-obfuscation folder to the list
            File PANObfuscationDir = new File (PANPath+NoObfuscation);
            Iterator<File> PANIterator2 = FileUtils.iterateFiles(PANObfuscationDir, null, false);

            while (PANIterator2.hasNext()) {
                File ObfuscationFile= PANIterator2.next();
                PANData panData = readXMLFile(ObfuscationFile, 0, 2);
                PANList.add(panData);
                int nodes = panData.getNodesItems();
                if (nodes > 1) {
                    for (int i = 1; i < nodes; i++) {
                        PANData panData1 = readXMLFile(ObfuscationFile, i, 2);
                        PANList.add(panData1);
                    }
                }


            }
            //Counts pairs of no-plagiarism and no-obfuscation (not relevant, just to double check)
            int noPlagiarismPairs = 0;
            int noObfuscationPairs = 0;
            int pairs = 0;
            for (PANData panData : PANList){
               System.out.println(panData.getSuspFileName()+" " +panData.getSource_reference() + " " +panData.getPlagiarism());
               if (panData.getPlagiarism() == 1){
                   noPlagiarismPairs++;
               }
               if (panData.getPlagiarism() == 2){
                   noObfuscationPairs++;
               }
               pairs++;
            }
            System.out.println("No Plagiarism Pairs: " +noPlagiarismPairs);
            System.out.println("No Obfuscation Pairs: " + noObfuscationPairs);
            System.out.println("All Pairs: " + pairs);

            //Creates a list with Sherlock results by taking pairs of the PAN list
            long startSherlock = System.currentTimeMillis(); //Start runtime

            List<SherlockResult> sherlockResults = new ArrayList<>();
            int sherlockRuns = 1;
            long shrinkedFileSizeSubstring = 0;
            long fileSizeSubstring = 0;
            long fileSize = 0;
            long shrinkedFileSize = 0;
            String zerobits = "3";
            String words = "5";

            for (PANData panData : PANList){
                long start = System.currentTimeMillis();

                //Filtering no-plagiarism and run through Sherlock
                if (panData.getPlagiarism() ==1){

                    //gets susp and src text files
                    String suspText = FileUtils.readFileToString(new File(PANPath + "susp/" + panData.getSuspFileName()), UTF_8);
                    String sourceText = FileUtils.readFileToString(new File(PANPath + "src/" + panData.getSource_reference()), UTF_8);

                    fileSize += suspText.length();
                    fileSize += sourceText.length();


                    File subtextSuspFolder = new File(OriginalSherlockPath + "susp-subtexts/" + panData.getSuspFileName());
                    FileUtils.writeStringToFile(subtextSuspFolder, suspText, UTF_8);
                    File subtextSrcFolder = new File(OriginalSherlockPath + "src-subtexts/" + panData.getSource_reference());
                    FileUtils.writeStringToFile(subtextSrcFolder, sourceText, UTF_8);

                    //filters susp and src
                    File shrinkedSuspFile = writeFileInDirectory("susp", subtextSuspFolder);
                    File shrinkedSrcFile = writeFileInDirectory("src", subtextSrcFolder);

                    shrinkedFileSize += shrinkedSuspFile.length();
                    shrinkedFileSize += shrinkedSrcFile.length();

                    //Sherlock
                    String config1 = checkPlagiarism(shrinkedSrcFile, shrinkedSuspFile, zerobits, words);

                    //modify Sherlock results to needs
                    String modSherlockResult = config1.replaceAll(" and ", ": ");
                    String[] parts = modSherlockResult.split(": ");
                    File suspNameSherlock = new File (parts[0]);
                    File srcNameSherlock = new File (parts[1]);
                    String similarityPercentage = parts[2];
                    String similarityscore = StringUtils.removeEnd(similarityPercentage, "%\n");

                    String suspNameSherlock1 = suspNameSherlock.getName();
                    String srcNameSherlock1 = srcNameSherlock.getName();

                    //add result to List
                    SherlockResult sherlockPair = new SherlockResult(suspNameSherlock1, srcNameSherlock1, similarityscore, panData.getPlagiarism());


                    sherlockResults.add(sherlockPair);
                    subtextSrcFolder.delete();
                    subtextSuspFolder.delete();
                    shrinkedSrcFile.delete();
                    shrinkedSuspFile.delete();
                } else {
                    //Filtering no-obfuscation pairs and run through Sherlock
                    String suspText = FileUtils.readFileToString(new File(PANPath + "susp/" + panData.getSuspFileName()), UTF_8);
                    String sourceText = FileUtils.readFileToString(new File(PANPath + "src/" + panData.getSource_reference()), UTF_8);

                    //get substrings susp and src
                    String subtextSusp =suspText.substring(Integer.parseInt(panData.getThis_offset()),Integer.parseInt(panData.getThis_length())+
                            Integer.parseInt(panData.getThis_offset()));
                    fileSizeSubstring += subtextSusp.length();

                    String subtextSrc = sourceText.substring(Integer.parseInt(panData.getSource_offset()), Integer.parseInt(panData.getSource_length())+
                            Integer.parseInt(panData.getSource_offset()));
                    fileSizeSubstring += subtextSrc.length();

                    File subtextSuspFolder = new File(OriginalSherlockPath + "susp-subtexts/" + panData.getSuspFileName());
                    FileUtils.writeStringToFile(subtextSuspFolder, subtextSusp, UTF_8);
                    File subtextSrcFolder = new File(OriginalSherlockPath + "src-subtexts/" + panData.getSource_reference());
                    FileUtils.writeStringToFile(subtextSrcFolder, subtextSrc, UTF_8);

                    //filters substrings
                    File shrinkedSuspFile = writeFileInDirectory("susp", subtextSuspFolder);
                    File shrinkedSrcFile = writeFileInDirectory("src", subtextSrcFolder);

                    shrinkedFileSizeSubstring += shrinkedSrcFile.length();
                    shrinkedFileSizeSubstring += shrinkedSuspFile.length();

                    //Sherlock
                    String config1 = checkPlagiarism(shrinkedSrcFile, shrinkedSuspFile, zerobits, words);

                    //modify Sherlock result to needs
                    String modSherlockResult = config1.replaceAll(" and ", ": ");
                    String[] parts = modSherlockResult.split(": ");
                    File suspNameSherlock = new File(parts[0]);
                    File srcNameSherlock = new File(parts[1]);
                    String similarityPercentage = parts[2];
                    String similarityscore = StringUtils.removeEnd(similarityPercentage, "%\n");

                    String suspNameSherlock1 = suspNameSherlock.getName();
                    String srcNameSherlock1 = srcNameSherlock.getName();

                    //add result to list
                    SherlockResult sherlockPair = new SherlockResult(suspNameSherlock1, srcNameSherlock1, similarityscore, panData.getPlagiarism());


                    sherlockResults.add(sherlockPair);
                    subtextSrcFolder.delete();
                    subtextSuspFolder.delete();
                    shrinkedSrcFile.delete();
                    shrinkedSuspFile.delete();
                }
                long runtime = takeTime(start);
                System.out.println("Sherlock run number: " + sherlockRuns + "- runtime for this: " + runtime + " ms");
                System.out.println("Original substring size: " + fileSizeSubstring);
                System.out.println("Shrinked substring size: " + shrinkedFileSizeSubstring);
                System.out.println("Original file size: " +fileSize);
                System.out.println("Shrinked file size: " + shrinkedFileSize);
                sherlockRuns++;
            }

            long sherlockRuntime = takeTime(startSherlock);

            //Classification of results: differentiation between below/above the border and origin of the pair
            int truePositives = 0;
            int falseNegatives = 0;
            int trueNegatives = 0;
            int falsePositives = 0;
            for (SherlockResult sherlockPair : sherlockResults){
                System.out.println(sherlockPair.getSuspName()+ " " + sherlockPair.getSourceName() + " " + sherlockPair.getSimilarityscore());
                if (sherlockPair.getSimilarityscore() >= 10){
                    if (sherlockPair.getPlag() == 2){
                        truePositives++;
                    } else{
                        falsePositives++;
                    }

                } else {
                    if (sherlockPair.getPlag() == 2){
                        falseNegatives++;
                    } else{
                        trueNegatives++;
                    }
                }

            }

            //Calculation of precision and recall
            float recall = (float)truePositives / ((float)falseNegatives+(float)truePositives);
            float precision = (float) truePositives/ ((float)truePositives+(float)falsePositives);

            //Outputs
            System.out.println("Runtime plagiarism detection: " +sherlockRuntime+ "ms");
            System.out.println("No Obfuscation:");
            System.out.println("True positives: " + truePositives);
            System.out.println("False negatives " + falseNegatives);
            System.out.println("No Plagiarism:");
            System.out.println("True Negatives: " + trueNegatives);
            System.out.println("False Positives: " + falsePositives);

            System.out.println("Original substring size: " + fileSizeSubstring + " bytes");
            System.out.println("Shrinked substring size: " + shrinkedFileSizeSubstring + " bytes");

            System.out.println("Precision: " + precision);
            System.out.println("Recall: " +recall);


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

    //Apply filter by commenting out the filters that are not applied. Unfiltered: comment out all filters
    //Method for filtering a document
    private static File writeFileInDirectory (String dir, File currentFile) throws  IOException{
        String susps = FileReader.readFile(currentFile.getPath());
        ArrayList<Filter.Shrink> shrinks = new ArrayList<>(); //file as Arraylist

            //shrinks.add(Filter.Shrink.folding); //folding

            //shrinks.add(Filter.Shrink.stopwords); //stopwords

            //shrinks.add(Filter.Shrink.stemming); // stemming
        String newtext = Filter.displayTokenUsingStopAnalyzer(shrinks, susps);
        File newFile = new File(OriginalSherlockPath+ "filtered-"
                + dir + "/"  + currentFile.getName()); //Insert desired directory to store filtered texts
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
            output = new CommandExecutor(OriginalSherlockPath +"sherlock",
                    "-t",
                    "0", "-z", zerobits,"-n", words,
                    candidateFilePath,
                    sourceFilePath

            ).exec();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }
    //Runtime method
    private static long takeTime (long startTime) {
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        return runtime;
    }



}



