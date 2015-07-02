package com.viriumdev.curriculab.plan;

import com.viriumdev.curriculab.cc.CCParser;
import java.lang.reflect.Field;

import java.util.*;

/**
 * Lesson Plan class
 *
 * @author Garrett T. Smith
 */
public class LessonPlan {

    private List<Objective> objectives = new ArrayList<>();
    private int subjectType;
    private String[] gradeLevel;
    private String name;
    private String grade_str;

    public LessonPlan(String name, int subjectType, String[] gradeLevel, String grade_str) {
        this.subjectType = subjectType;
        this.gradeLevel = gradeLevel;
        this.name = name;
        this.grade_str = grade_str;
    }

    public List<Objective> getAllObjectives() {
        return objectives;
    }

    public String getGradeLevel() {
        return grade_str;
    }

    public int getSubjectType() {
        return subjectType;
    }

    public String getSubjectAsString() {
        switch (subjectType) {
            case CCParser.MATH:
                return "Mathematics";
            case CCParser.LITERACY:
                return "ELA Literacy";
        }
        return null;
    }

    public void addObjective(Objective objective) {
        objectives.add(objective);
    }

    public String getName() {
        return name;
    }
}
