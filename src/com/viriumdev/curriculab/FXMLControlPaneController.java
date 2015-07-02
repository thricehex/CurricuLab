package com.viriumdev.curriculab;

import com.viriumdev.curriculab.plan.LessonPlan;
import com.viriumdev.curriculab.plan.Objective;
import com.viriumdev.curriculab.sharing.PDFWriter;
import com.viriumdev.curriculab.sharing.SaveLoader;
import com.viriumdev.curriculab.sharing.SaveWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

/**
 * Control Pane Controller class
 *
 * @author Garrett T. Smith
 */
public class FXMLControlPaneController implements Initializable {

    @FXML
    private Hyperlink newLP;
    @FXML
    private Hyperlink saveLP;
    @FXML
    private Hyperlink shareLP;
    @FXML
    private Hyperlink exit;
    @FXML
    private Button newObj;
    @FXML
    private Button openObj;
    @FXML
    private ListView<String> options;
    @FXML
    private VBox lpBox;
    @FXML
    private ListView objectiveList;
    @FXML
    private Label lpTitle;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label gradeLabel;

    private List<LessonPlan> activePlans = new ArrayList<>();
    private List<Objective> loadedObjectives = new ArrayList<>();
    private LessonPlan loadedPlan;
    private MainPane loadedObjPane;
    private boolean objectiveOpenFlag = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        CLUtilities.setCPController(this);

        newLP.setOnAction(new NewPlanHandler());
        newObj.setOnAction(new ObjectiveHandler());
        openObj.setOnAction(new ObjectiveHandler());
        saveLP.setOnAction(new SaveHandler());

        exit.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                Platform.exit();
            }
        });

        shareLP.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                if (loadedPlan != null) {
                    PDFWriter pdfWriter = new PDFWriter(loadedPlan);
                    File rendering = pdfWriter.renderLessonPlan();
                    try {
                        Desktop.getDesktop().open(rendering);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else {
                    CLUtilities.notify("No lesson plan selected!");
                }

            }
        });

        refresh();
    }

    public class ObjectiveHandler<ActionEvent> implements EventHandler {

        @Override
        public void handle(Event event) {
            if (event.getSource().equals(openObj)) {
                int objIndex = objectiveList.getSelectionModel().getSelectedIndex();
                
                if(objIndex == -1) 
                    CLUtilities.notify("Please select an objective.");
                
                constructObjectivePane(loadedObjectives.get(objIndex));
            } else if (event.getSource().equals(newObj)) {
                Objective objective = new Objective("");
                loadedPlan.addObjective(objective);
                constructObjectivePane(objective);
            }
        }

        public void constructObjectivePane(Objective objective) {
            try {
                if (!objectiveOpenFlag) {
                    objectiveOpenFlag = true;
                    Stage stage = new MainPane(objective);
                    stage.show();
                    loadedObjPane = (MainPane) stage;
                    FXMLMainController controller = loadedObjPane.getController();
                    controller.setObjective(loadedObjPane.getObjective());
                    controller.setSubject(CLUtilities.getIntConstant(loadedPlan.getSubjectAsString()));
                    controller.setGradeLevel(CLUtilities.getStringConstant(loadedPlan.getGradeLevel()));
                    controller.loadContent();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void setPlanLabels(String name, String subject, String grade) {
        lpTitle.setText(name);
        subjectLabel.setText("Subject : " + subject);
        gradeLabel.setText("Grade : " + grade);
    }

    public class NewPlanHandler<ActionEvent> implements EventHandler {

        @Override
        public void handle(Event event) {
            final Stage lpPane = new CreatePlanPane();
            lpPane.show();
            final CreatePlanPane pane = (CreatePlanPane) lpPane;
            pane.getCreateButton().setOnAction(new EventHandler() {

                @Override
                public void handle(Event event) {

                    String lpName = pane.getNameBox().getText();

                    ComboBox subjBox = pane.getSubjectSelector();
                    String subject = (String) subjBox.getSelectionModel().getSelectedItem();

                    ComboBox gradeBox = pane.getGradeSelector();
                    String grade = (String) gradeBox.getSelectionModel().getSelectedItem();

                    if (subject == null || grade == null || lpName == null) {
                        CLUtilities.notify("Please make a selection for all fields before continuing");
                    }

                    int subjectType = CLUtilities.getIntConstant(subject);
                    String[] gradeLevel = CLUtilities.getStringConstant(grade);

                    LessonPlan plan = new LessonPlan(lpName, subjectType, gradeLevel, grade);

                    loadedPlan = plan;
                    setPlanLabels(plan.getName(), plan.getSubjectAsString(), plan.getGradeLevel());

                    lpPane.close();
                    SaveWriter writer = new SaveWriter();
                    writer.writeLessonPlan(plan);
                    refresh();
                }

            });
        }

    }

    public class SaveHandler<ActionEvent> implements EventHandler {

        @Override
        public void handle(Event event) {

            SaveWriter writer = new SaveWriter();
            writer.writeLessonPlan(loadedPlan);
        }
    }

    public class PlanLoadHandler<ActionEvent> implements EventHandler {

        private LessonPlan plan;

        public PlanLoadHandler(LessonPlan plan) {
            this.plan = plan;
        }

        @Override
        public void handle(Event event) {
            loadedPlan = plan;
            setPlanLabels(plan.getName(), plan.getSubjectAsString(), plan.getGradeLevel());

            List<Objective> objectives = new ArrayList<>();
            objectives = plan.getAllObjectives();

            loadedObjectives.clear();
            objectiveList.getSelectionModel().clearSelection();
            if (objectives != null) {
                List<Label> objLabels = new ArrayList<>();
                loadedObjectives.addAll(objectives);
                for (int i = 0; i < objectives.size(); i++) {
                    objLabels.add(new Label("Objective " + (i + 1)));
                }
                objectiveList.setItems(FXCollections.observableArrayList(objLabels));
            } else {
                objectiveList.setItems(FXCollections.observableArrayList());
            }

        }
    }

    public void refresh() {
        lpBox.getChildren().clear();
        objectiveList.getItems().clear();

        SaveLoader loader = new SaveLoader();
        File directory = new File("CLResources/plans");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File[] saveFiles = directory.listFiles();

        if (saveFiles.length > 0) {
            newObj.arm();
            for (File next : saveFiles) {
                if (next.getName().endsWith(".clp")) {
                    LessonPlan nextPlan = loader.loadLessonPlan(next);

                    Hyperlink planLink = new Hyperlink(nextPlan.getName() + "\n Subject : "
                            + nextPlan.getSubjectAsString() + " - Grade : " + nextPlan.getGradeLevel());
                    planLink.setStyle("-fx-text-fill: #2b2a2a");
                    planLink.setOnAction(new PlanLoadHandler(nextPlan));

                    lpBox.getChildren().add(planLink);
                }
            }
        }
    }

    public void closeObjectivePane() {
        if (loadedObjPane != null) {
            loadedObjPane.close();
            objectiveOpenFlag = false;
        }

    }

    public LessonPlan getLoadedPlan() {
        return loadedPlan;
    }

}
