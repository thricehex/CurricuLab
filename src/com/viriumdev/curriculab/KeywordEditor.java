package com.viriumdev.curriculab;

import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Pane for editing keywords
 *
 * @author Garrett
 */
public class KeywordEditor extends Stage {

    private Label label;
    private TextArea kwEditor;
    private Scene scene;
    private Button save;
    private FlowPane flowPane;

    public KeywordEditor() {
        super(StageStyle.UTILITY);

        flowPane = new FlowPane();
        flowPane.setPadding(new Insets(5, 15, 5, 10));
        flowPane.setVgap(5);
        label = new Label("Enter or delete keywords (Separate with commas)");
        kwEditor = new TextArea();
        save = new Button("Save");
        ObservableList<Node> nodes = flowPane.getChildren();
        flowPane.setOrientation(Orientation.VERTICAL);
        nodes.add(label);
        nodes.add(kwEditor);
        nodes.add(save);

        scene = new Scene(flowPane);
        setMinWidth(600);
        setMinHeight(150);
        setScene(scene);

    }

    public TextArea getEditorPane() {
        return kwEditor;
    }

    public Button getSaveButton() {
        return save;
    }

}
