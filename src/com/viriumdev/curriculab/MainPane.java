package com.viriumdev.curriculab;

import com.viriumdev.curriculab.plan.Objective;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main objective creating pane
 *
 * @author Garrett T. Smith
 */
public class MainPane extends Stage {

    private Objective objective;
    private FXMLMainController controller;

    public MainPane(Objective objective) throws Exception {
        this.objective = objective;

        URL resLocation = getClass().getResource("FXMLMain.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(resLocation);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = (Parent) loader.load(resLocation.openStream());

        Scene scene = new Scene(root);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);

        controller = loader.<FXMLMainController>getController();
    }

    public Objective getObjective() {
        return objective;
    }

    public FXMLMainController getController() {
        return controller;
    }
}
