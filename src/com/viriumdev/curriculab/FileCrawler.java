package com.viriumdev.curriculab;

import java.io.*;
import java.util.*;

/**
 * Recursive file searcher class
 * 
 * @author Garrett T. Smith
 */

public class FileCrawler {

    private static HashMap<String, List<String>> masterMap = new HashMap<>();
    private static List<String> keyList = new ArrayList<String>();

    private static LinkedList<String> directoryList = new LinkedList<String>();
    private static int directoryIndex = -1;

    public static void runSearch(String root, String extension, String... searchTerm) {

        List<String> targetList = new ArrayList<String>();
        File directory = new File(root);

        System.out.println(directory.getAbsolutePath());

        File[] files = directory.listFiles();

        if (files.length > 0) {

            for (File file : files) {
                if (file.isDirectory()) {
                    directoryList.add(file.getAbsolutePath());
                    System.out.println("(FileCrawler) Directory Processed: " + file.getPath());

                } else if (isTarget(file, searchTerm, extension)) {
                    targetList.add(file.getAbsolutePath());
                    System.out.println("(FileCrawler) Target File Located: " + file.getPath());
                }
            }

            masterMap.put(directory.getAbsolutePath(), targetList);
            keyList.add(directory.getAbsolutePath());

            directoryIndex++;
            while (directoryIndex < directoryList.size()) {
                runSearch(directoryList.get(directoryIndex), extension, searchTerm);
            }

        }

    }

    public static boolean isTarget(File file, String[] targetList, String extension) {
        boolean targetFlag = false;

        if (!file.getName().endsWith(extension)) {
            return false;
        }

        for (String target : targetList) {
            if (file.getName().startsWith(target)) {
                if(targetList[0].equals("1") && file.getName().startsWith("11")) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    public static HashMap<String, List<String>> getMasterMap() {
        HashMap<String, List<String>> map = new HashMap<>();
        map.putAll(masterMap);
        masterMap.clear();
        return map;
    }

    public static List<String> getKeyList() {
        List<String> keys = new ArrayList<>();
        keys.addAll(keyList);
        keyList.clear();
        return keys;
    }

}
