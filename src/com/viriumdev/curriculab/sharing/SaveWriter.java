/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viriumdev.curriculab.sharing;

import com.viriumdev.curriculab.CLUtilities;
import com.viriumdev.curriculab.ContentType;
import java.io.File;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import com.viriumdev.curriculab.plan.*;
import java.io.IOException;

/**
 * Class for saving a Lesson Plan
 * 
 * @author Garrett T. Smith
 */
public class SaveWriter {

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private Element plan;

    public SaveWriter() {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();

            document = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            CLUtilities.notify("Failed to save lesson plan.");
            pce.printStackTrace();
        }
    }

    public void writeLessonPlan(LessonPlan lesson) {
        plan = document.createElement("LessonPlan");
        document.appendChild(plan);

        Element name = document.createElement("Name");
        name.appendChild(document.createTextNode(lesson.getName()));
        plan.appendChild(name);

        Element grade = document.createElement("Grade");
        grade.appendChild(document.createTextNode(lesson.getGradeLevel()));
        plan.appendChild(grade);

        Element subjectCode = document.createElement("SubjectCode");
        subjectCode.appendChild(document.createTextNode(String.valueOf(lesson.getSubjectType())));
        plan.appendChild(subjectCode);

        Element subject = document.createElement("Subject");
        subject.appendChild(document.createTextNode(lesson.getSubjectAsString()));
        plan.appendChild(subject);

        for (Objective nextObj : lesson.getAllObjectives()) {
            writeObjective(nextObj);
        }

        finalize(lesson.getName());
    }

    protected void writeObjective(Objective objective) {
        Element objectiveEle = document.createElement("Objective");
        plan.appendChild(objectiveEle);

        Element body = document.createElement("Body");
        body.appendChild(document.createTextNode(objective.getBody()));
        objectiveEle.appendChild(body);

        int index = 0;
        for (String strategy : objective.getStrategies()) {
            Element strategyEle = document.createElement("Strategy");
            //Attr attr = document.createAttribute("index");
            // attr.setValue(String.valueOf(index));
            //strategyEle.setAttributeNode(attr);
            strategyEle.appendChild(document.createTextNode(strategy));
            objectiveEle.appendChild(strategyEle);
        }

        for (ContentType ref : objective.getReferences()) {
            String parent = ref.getParentValue();
            String child = ref.getChildValue();

            Element refEle = document.createElement("Reference");
            objectiveEle.appendChild(refEle);

            Element refParent = document.createElement("Parent");
            refParent.appendChild(document.createTextNode(ref.getParentValue()));
            refEle.appendChild(refParent);

            Element refChild = document.createElement("Child");
            refChild.appendChild(document.createTextNode(ref.getChildValue()));
            refEle.appendChild(refChild);

        }
    }

    protected void finalize(String filename) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource dom = new DOMSource(document);
            File saveFile = new File("CLResources/plans/" + filename + ".clp");
            StreamResult result = new StreamResult(saveFile);
            transformer.transform(dom, result);
            
            System.out.println("Lesson Plan saved to : " + saveFile.getAbsolutePath());

        } catch (TransformerException ex) {
            CLUtilities.notify("Could not save lesson plan.");
            ex.printStackTrace();
        }
    }

}
