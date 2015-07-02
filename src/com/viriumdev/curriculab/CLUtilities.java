package com.viriumdev.curriculab;

import com.viriumdev.curriculab.cc.CCParser;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.*;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.swing.JOptionPane;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

/**
 * Utility/Manager class for CurricuLab
 *
 * @author Garrett
 */
public class CLUtilities {

    private static FXMLControlPaneController cpController;
    public static final int BL_SIZE = 200;
    private static String resFilePath;

    public static List<String> getBlacklistedKeywords() {
        List<String> blacklistedWords = new ArrayList<>();
        try {
            File blacklist = new File("CLResources/blacklist.txt");
            BufferedReader buffReader = new BufferedReader(new FileReader(blacklist));

            for (int i = 0; i < BL_SIZE; i++) {
                String nextWord = buffReader.readLine();
                blacklistedWords.add(nextWord);
            }

            buffReader.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return blacklistedWords;
    }

    public static List<Label> generateTagLabels(String... tags) {
        Cloud cloud = new Cloud();
        List<Label> cloudLabels = new ArrayList<>();

        for (String nextTag : tags) {
            cloud.addTag(nextTag);
        }

        for (Tag tag : cloud.tags()) {
            Label label = new Label(tag.getName());
            label.setFont(Font.font("Verdana",
                    FontWeight.findByWeight(tag.getWeightInt()),
                    new java.util.Random().nextInt(15) + 10));
            cloudLabels.add(label);
        }

        return cloudLabels;
    }

    public static void unpackResources() throws ZipException, IOException {

        File destination = copyFromJar("CLResources", "res.zip");
        resFilePath = destination.getAbsolutePath();

    }

    private static File copyFromJar(String destination, String target) throws ZipException, IOException {
        File destFile = new File(destination);
        if (!destFile.exists()) {
            InputStream in = CLUtilities.class.getResourceAsStream(target);
            FileOutputStream out = new FileOutputStream(target);

            byte[] buff = new byte[10 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buff)) != -1) {
                out.write(buff, 0, bytesRead);
            }
            in.close();
            out.flush();
            out.close();
            ZipFile zip = new ZipFile(target);
            zip.extractAll(destination);
        }
        return destFile;
    }

    public static String getResourcePath() {
        return resFilePath;
    }

    public static String[] getStringConstant(String constant) {
        switch (constant) {
            case "Kindergarten":
                return CCParser.K;
            case "First":
                return CCParser.FIRST;
            case "Second":
                return CCParser.SECOND;
            case "Third":
                return CCParser.THIRD;
            case "Fourth":
                return CCParser.FOURTH;
            case "Fifth":
                return CCParser.FIFTH;
            case "Sixth":
                return CCParser.SIXTH;
            case "Seventh":
                return CCParser.SEVENTH;
            case "Eighth":
                return CCParser.EIGHTH;
            case "Highschool":
                return CCParser.HIGHSCHOOL;
            case "Highschool(9th-10th)":
                return CCParser.HIGHSCHOOL_LOWER;
            case "Highschool(11th-12th)":
                return CCParser.HIGHSCHOOL_UPPER;
        }
        return null;
    }

    public static int getIntConstant(String constant) {
        switch (constant) {
            case "Math":
                return CCParser.MATH;
            case "ELA Literacy":
                return CCParser.LITERACY;
        }
        return 0;
    }

    public static void notify(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public static void setCPController(FXMLControlPaneController cpControl) {
        cpController = cpControl;
    }

    public static FXMLControlPaneController getCPController() {
        return cpController;
    }
}
