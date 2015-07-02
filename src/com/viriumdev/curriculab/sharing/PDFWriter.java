/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viriumdev.curriculab.sharing;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.viriumdev.curriculab.ContentType;
import com.viriumdev.curriculab.plan.LessonPlan;
import com.viriumdev.curriculab.plan.Objective;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Class for rendering a PDF representation of a Lesson Plan
 *
 * @author Garrett T. Smith
 */
public class PDFWriter {

    private Font headerFont = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    private Font labelFont = new Font(Font.FontFamily.COURIER, 13, Font.BOLDITALIC);
    private Font bodyFont = new Font(Font.FontFamily.COURIER, 11, Font.NORMAL);

    private Document doc = new Document();
    private LessonPlan lessonPlan;
    private Paragraph root;
    private File pdfRoot;
    private File pdfFile;

    public PDFWriter(LessonPlan lessonPlan) {
        this.lessonPlan = lessonPlan;
        pdfRoot = new File("CLResources/pdfs/");
        if (!pdfRoot.exists()) {
            pdfRoot.mkdir();
        }
        try {
            pdfFile = new File(pdfRoot, lessonPlan.getName() + ".pdf");
            if (!pdfFile.exists()) {
                pdfFile.createNewFile();
            }
            PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
            doc.open();

        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public File renderLessonPlan() {
        root = new Paragraph();
        printEmpty(1);
        root.add(new Paragraph(lessonPlan.getName(), headerFont));
        List<Objective> objectives = new ArrayList<>();
        objectives = lessonPlan.getAllObjectives();
        for (int i = 0; i < objectives.size(); i++) {
            printEmpty(3);
            Objective nextObj = objectives.get(i);
            root.add(new Paragraph("Objective " + (i + 1), labelFont));
            root.add(new Paragraph("---------------------------------------------------"));
            root.add(new Paragraph(nextObj.getBody().replaceAll("\\<.*?\\>", "")));
            printEmpty(1);
            root.add(new Paragraph("Strategies: ", labelFont));
            root.add(new Paragraph("------------------"));
            for (String nextStrat : nextObj.getStrategies()) {
                printEmpty(1);
                root.add(new Paragraph(nextStrat));
            }

            printEmpty(2);
            root.add(new Paragraph("References: ", labelFont));
            root.add(new Paragraph("-------------------"));

            for (ContentType nextRef : nextObj.getReferences()) {
                printEmpty(1);
                root.add(new Paragraph(nextRef.toString().replaceAll("\\<.*?\\>", "")));
            }
        }
        try {
            doc.add(root);
            doc.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        return pdfFile;
    }

    private void printEmpty(int count) {
        for (int i = 0; i < count; i++) {
            root.add(new Paragraph(" "));
        }
    }

}
