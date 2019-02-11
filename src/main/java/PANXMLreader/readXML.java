package PANXMLreader;

import org.jetbrains.annotations.Contract;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class readXML {


    @Contract("_ -> param1")
    public static String readXMLFile(File PANdir) throws IOException,XPathExpressionException, SAXException, ParserConfigurationException {


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(PANdir);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();




            //List<String> xmlData1 = new ArrayList<>();

            StringBuilder xmlData = new StringBuilder();
            XPathExpression expr = xPath.compile("//feature/@obfuscation_degree");
            Object result = expr.evaluate( document, XPathConstants.NODESET);
            /*
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {


                xmlData.append(xPath.compile("/document/@reference").evaluate(document) + ";" + xPath.compile("/document/feature/@source_reference").evaluate(document)
                            + ";" + nodes.item(i).getNodeValue()+ ";");

                }

            //System.out.println("");


        */ //For no obfuscation folder
            xmlData.append(xPath.compile("/document/@reference").evaluate(document
            )+ " " + xPath.compile("/document/feature/@source_reference").evaluate(document));


            return xmlData.toString();
    }

}

