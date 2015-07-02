package com.viriumdev.curriculab.plan;

import com.viriumdev.curriculab.ContentType;
import java.util.*;
import javafx.scene.control.Label;

/**
 * Lesson Plan Objective class
 *
 * @author Garrett T. Smith
 */
public class Objective {

    private String body;
    private List<String> strategies;
    private List<ContentType> references;

    public Objective(String body) {
        this.body = body;
    }

    public Objective() {
    }

    public void setStrategies(List<String> strategies) {
        this.strategies = new ArrayList<>();
        this.strategies.addAll(strategies);
        System.out.println(this.strategies);
    }

    public List<String> getStrategies() {
        return strategies;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setReferences(List<? extends ContentType> references) {
        this.references = new ArrayList<>();
        this.references.addAll(references);
        System.out.println(this.references);
    }

    public List<ContentType> getReferences() {
        return references;
    }

}
