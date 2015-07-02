package com.viriumdev.curriculab.cc;

import com.viriumdev.curriculab.CLUtilities;
import com.viriumdev.curriculab.FileCrawler;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Common Core XML parser class
 *
 * @author Garrett T. Smith
 */
public class CCParser {

    public static final String[] K = {"k"};
    public static final String[] FIRST = {"1"};
    public static final String[] SECOND = {"2"};
    public static final String[] THIRD = {"3"};
    public static final String[] FOURTH = {"4"};
    public static final String[] FIFTH = {"5"};
    public static final String[] SIXTH = {"6", "6-7", "6-8"};
    public static final String[] SEVENTH = {"7", "7-8", "6-7", "6-8"};
    public static final String[] EIGHTH = {"8", "7-8", "6-8"};
    public static final String[] HIGHSCHOOL = {"hs", "9-10", "11-12"};
    public static final String[] HIGHSCHOOL_LOWER = {"hs", "9-10"};
    public static final String[] HIGHSCHOOL_UPPER = {"hs", "11-12"};

    public static final int MATH = 0;
    public static final int LITERACY = 1;

    public List<File> ccFiles = new ArrayList<>();
    public List<CCStandard> standards = new ArrayList<CCStandard>();

    public String rootDirectory = CLUtilities.getResourcePath();

    public CCParser(int curriculumType, String... gradeLevels) {

        if (curriculumType == MATH) {
            rootDirectory = rootDirectory.concat("/ccssi/xml/math/content/");
            File parent = new File(rootDirectory);
            File[] fileList = parent.listFiles();
            for (File nextFile : fileList) {
                if (nextFile.isFile()
                        && FileCrawler.isTarget(nextFile, gradeLevels, ".xml")) {
                    ccFiles.add(nextFile);
                    System.out.println(nextFile.getPath());
                }
            }

        } else if (curriculumType == LITERACY) {
            rootDirectory = rootDirectory.concat("/ccssi/xml/ela-literacy/");
            searchByRoot(rootDirectory, ".xml", gradeLevels);
        }

        loadStandards();
    }

    public CCParser() {

    }

    private void loadStandards() {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();

            for (int i = 0; i < ccFiles.size(); i++) {
                File nextFile = ccFiles.get(i);
                Document nextDom = builder.parse(nextFile);
                Element domElement = nextDom.getDocumentElement();
                NodeList nodes = domElement.getElementsByTagName("StatementCode");
                NodeList childNodes = domElement.getElementsByTagName("Statement");

                if (nodes != null && nodes.getLength() > 0) {

                    for (int n = 0; n < nodes.getLength(); n++) {
                        String nextCode = ((Element) nodes.item(n)).getTextContent();
                        String nextDefinition = ((Element) childNodes.item(n)).getTextContent();

                        standards.add(new CCStandard(nextCode, nextDefinition));

                    }
                }

            }
            System.out.println("Standards Loaded : " + standards.size());

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace();
        }
    }

    public List<CCStandard> getStandardsByKeyword(String... keywords) {

        List<CCStandard> results = new ArrayList<>();
        for (CCStandard nextStandard : standards) {
            int validity = 0;
            String nextStatement = nextStandard.getChildValue();

            for (String keyword : keywords) {
                if (nextStatement.contains(keyword)) {
                    validity += 1;
                }
            }

            if (validity > 0) {
                results.add(nextStandard);
            }
        }

        return results;
    }

    private void searchByRoot(String rootDirectory, String ext, String[] gradeLevels) {

        FileCrawler.runSearch(rootDirectory, ext, gradeLevels);
        List<String> keys = new ArrayList<String>();
        keys = FileCrawler.getKeyList();
        HashMap<String, List<String>> fileMap = new HashMap<>();
        fileMap = FileCrawler.getMasterMap();

        for (String key : keys) {
            List<String> nextFileList = new ArrayList<>();
            nextFileList = fileMap.get(key);

            for (String file : nextFileList) {
                File nextFile = new File(file);
                ccFiles.add(nextFile);
            }
        }
        System.out.println("Common Core Files Loaded : " + ccFiles.size());
    }

}
