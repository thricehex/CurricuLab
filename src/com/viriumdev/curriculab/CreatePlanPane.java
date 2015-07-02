package com.viriumdev.curriculab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Pane for creating a new lesson plan.
 *
 * @author Garrett T. Smith
 */
public class CreatePlanPane extends Stage {

    private Scene scene;
    private Parent root;
    private FlowPane pane;
    private Label name;
    private Label grade;
    private Label subject;
    private TextField nameField;
    private ComboBox subjectCombo;
    private ComboBox gradeCombo;
    private Button create;
    private ObservableList<String> subjectItems;
    private ObservableList<String> gradeItems;

    public CreatePlanPane() {
        super(StageStyle.UTILITY);

        pane = new FlowPane();
        pane.setVgap(8);
        pane.setHgap(10);
        pane.setPadding(new Insets(10, 10, 60, 10));
        pane.setPrefWrapLength(850);

        name = new Label("Name");
        grade = new Label("Grade Level : ");
        subject = new Label("Subject : ");

        nameField = new TextField();
        nameField.setPrefWidth(200);

        subjectItems = FXCollections.observableArrayList(
                "Math",
                "ELA Literacy"
        );

        gradeItems = FXCollections.observableArrayList(
                "Kindergarten",
                "First",
                "Second",
                "Third",
                "Fourth",
                "Fifth",
                "Sixth",
                "Seventh",
                "Eighth",
                "Highschool",
                "Highschool(9th-10th)",
                "Highschool(11th-12th)"
        );

        subjectCombo = new ComboBox();
        subjectCombo.setItems(subjectItems);

        gradeCombo = new ComboBox();
        gradeCombo.setItems(gradeItems);

        create = new Button("Create");

        ObservableList<Node> nodes = pane.getChildren();
        nodes.add(name);
        nodes.add(nameField);
        nodes.add(grade);
        nodes.add(gradeCombo);
        nodes.add(subject);
        nodes.add(subjectCombo);
        nodes.add(create);

        scene = new Scene(pane);
        setScene(scene);

    }

    public Button getCreateButton() {
        return create;
    }

    public TextField getNameBox() {
        return nameField;
    }

    public ComboBox getSubjectSelector() {
        return subjectCombo;
    }

    public ComboBox getGradeSelector() {
        return gradeCombo;
    }
}
