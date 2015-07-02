/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viriumdev.curriculab.sharing;

import com.viriumdev.curriculab.CLUtilities;
import com.viriumdev.curriculab.ContentType;
import java.io.File;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import com.viriumdev.curriculab.plan.*;
import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * Class for loading saved lesson plans
 *
 * @author Garrett T. Smith
 */
public class SaveLoader {

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder builder;
    private Document document;

    private List<Objective> objectives = new ArrayList<>();
    private LessonPlan plan;

    public SaveLoader() {
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            builder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            CLUtilities.notify("Lesson Plan could not be loaded.");
            pce.printStackTrace();
        }
    }

    public LessonPlan loadLessonPlan(File planFile) {
        try {
            document = builder.parse(planFile);
            Element rootEle = document.getDocumentElement();
            NodeList nameNode = document.getElementsByTagName("Name");
            NodeList gradeNode = document.getElementsByTagName("Grade");
            NodeList subjectNode = document.getElementsByTagName("Subject");

            String name = ((Element) nameNode.item(0)).getTextContent();
            String grade_str = ((Element) gradeNode.item(0)).getTextContent();
            String subject = ((Element) subjectNode.item(0)).getTextContent();

            String[] grade = CLUtilities.getStringConstant(grade_str);
            plan = new LessonPlan(name, CLUtilities.getIntConstant(subject), grade, grade_str);

            NodeList objNodes = document.getElementsByTagName("Objective");

            for (int i = 0; i < objNodes.getLength(); i++) {
                Objective objective = new Objective("");
                List<String> strategies = new ArrayList<>();
                List<ContentType> references = new ArrayList<>();

                Element nextObj = (Element) objNodes.item(i);

                NodeList bodyNode = nextObj.getElementsByTagName("Body");
                String body = ((Element) bodyNode.item(0)).getTextContent();

                NodeList stratNodes = nextObj.getElementsByTagName("Strategy");
                NodeList refNodes = nextObj.getElementsByTagName("Reference");

                for (int n = 0; n < stratNodes.getLength(); n++) {
                    String strategy = ((Element) stratNodes.item(n)).getTextContent();
                    strategies.add(strategy);
                }

                for (int n = 0; n < refNodes.getLength(); n++) {
                    Element nextRef = (Element) refNodes.item(n);

                    NodeList parent = nextRef.getElementsByTagName("Parent");
                    String parentVal = ((Element) parent.item(0)).getTextContent();
                    NodeList child = nextRef.getElementsByTagName("Child");
                    String childVal = ((Element) child.item(0)).getTextContent();

                    references.add(new ContentType(parentVal, childVal));
                }

                objective.setBody(body);
                objective.setStrategies(strategies);
                objective.setReferences(references);

                plan.addObjective(objective);
            }

        } catch (SAXException | IOException ex) {
            ex.printStackTrace();
        }

        return plan;
    }

}
