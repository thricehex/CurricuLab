package com.viriumdev.curriculab;

import com.viriumdev.curriculab.api.*;
import com.viriumdev.curriculab.cc.*;
import com.viriumdev.curriculab.plan.Objective;
import com.viriumdev.curriculab.sharing.SaveWriter;
import java.net.URL;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

/**
 * Objective Creation pane controller class.
 *
 * @author Garrett T. Smith
 */
public class FXMLMainController implements Initializable {

    private final APIConnection dictionaryConn = new APIConnection(APIConnection.DICTIONARY);
    private final APIConnection seriesConn = new APIConnection(APIConnection.BRILLIANT_SERIES);
    private CCParser ccParser;

    List<String> blacklistedWords = new ArrayList<>(CLUtilities.getBlacklistedKeywords());

    private List<APIElement> listContents = new ArrayList<>();
    private List<Hyperlink> refItems = new ArrayList<>();

    private static final int MAX_RESPONSES = 10;

    @FXML
    private Parent root;
    @FXML
    private ListView<Hyperlink> ccPaneList;
    @FXML
    private ListView<Hyperlink> apiPaneList;
    @FXML
    private ListView<Hyperlink> referencePaneList;

    private TextArea kwEditor;
    private Button saveButton;

    @FXML
    private Pane keywordBox;

    @FXML
    private HTMLEditor editor;
    @FXML
    private ProgressIndicator topPaneIndicator;
    @FXML
    private ProgressIndicator bottomPaneIndicator;
    @FXML
    private Button addStrategy;
    @FXML
    private TabPane tabPane;

    @FXML
    private Hyperlink saveLabel;
    @FXML
    private Hyperlink exitLabel;
    @FXML
    private Hyperlink exSaveLabel;

    private KeywordEditor kwEdit;
    private int strategyCount = 2;

    private int subject;
    private String[] grade;

    private Stage stage;
    private Objective objective;
    private List<ContentType> references = new ArrayList<>();
    private List<ContentType> refInVitro = new ArrayList<>();
    private List<String> strategies = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        topPaneIndicator.setVisible(false);
        bottomPaneIndicator.setVisible(false);

        saveLabel.setOnAction(new onSaveHandler());
        exitLabel.setOnAction(new onSaveHandler());
        exSaveLabel.setOnAction(new onSaveHandler());

        addStrategy.setOnAction(new StrategyTabHandler());

        referencePaneList.setItems(FXCollections.observableArrayList(refItems));

        kwEdit = new KeywordEditor();
        kwEditor = kwEdit.getEditorPane();
        saveButton = kwEdit.getSaveButton();
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                onClickUpdateHandler(event);
                kwEdit.close();
            }
        });
    }

    public void loadContent() {
        ccParser = new CCParser(subject, grade);
        
        String body = objective.getBody();
        editor.setHtmlText(body);


        strategies = objective.getStrategies();

        references = objective.getReferences();

        if (references != null) {
            for (ContentType nextRef : references) {
                new HyperlinkHandler(nextRef).handle(null);
            }
        }

        int index = 1;

        if (strategies != null) {
            if (strategies.size() > 0) {
                tabPane.getTabs().clear();
            }
            for (String strategy : strategies) {
                new StrategyTabHandler(strategy, index).handle(null);
                index++;
            }
        }

    }

    public void onClickUpdateHandler(ActionEvent event) {

        Button source = (Button) event.getSource();
        List<String> keywords = new ArrayList<>();
        if (source.getText().equals("Update Content")) {
            keywords = getUpdatedKeywords();
        } else if (source.getText().equals("Save")) {

            String contents = kwEditor.getText();
            String[] newKw = contents.replaceAll("\\s", "").split(",");

            keywords = Arrays.asList(newKw);
            System.out.println(keywords);

        }

        topPaneIndicator.setVisible(true);
        bottomPaneIndicator.setVisible(true);

        apiPaneList.getItems().clear();
        ccPaneList.getItems().clear();

        List<? extends ContentType> responseList = new ArrayList<>();
        String[] keywordsArr = keywords.toArray(new String[0]);

        updateTagCloud(keywordsArr);

        APIResponse dictResponse = dictionaryConn.executeQuery(APIConnection.SEARCH, keywordsArr);
        responseList = dictResponse.getAllParsedElements();
        updateContentPanel(responseList, apiPaneList);

        APIResponse seriesResponse = seriesConn.executeQuery(APIConnection.CONTENT, keywordsArr);
        responseList = seriesResponse.getAllParsedElements();
        updateContentPanel(responseList, apiPaneList);

        responseList = ccParser.getStandardsByKeyword(keywordsArr);
        updateContentPanel(responseList, ccPaneList);

    }

    public void onClickHandler(ActionEvent event) {

        Button source = (Button) event.getSource();

        if (source.getText().equals("Add/Remove Keywords")) {
            StringBuilder strBuilder = new StringBuilder();

            ObservableList<Node> nodes = keywordBox.getChildren();

            for (int i = 0; i < nodes.size(); i++) {
                Node nextItem = nodes.get(i);

                strBuilder.append(((Label) nextItem).getText());
                if (i != nodes.size() - 1) {
                    strBuilder.append(",");
                }

            }
            kwEdit.show();
            kwEditor.setText(strBuilder.toString());
        } else if (source.getText().equals("Remove")) {

        }

    }

    public List<String> getUpdatedKeywords() {
        List<String> results = new LinkedList<>();
        objective.setBody(editor.getHtmlText());
        results.addAll(Arrays.asList(editor.getHtmlText().replaceAll("\\<.*?\\>", "").split(" ")));


        for (int i = 0; i < results.size(); i++) {
            String nextWord = results.get(i);
            if (nextWord.length() < 4) {
                results.remove(i);
            }
        }

        List<String> blacklistedWords;
        blacklistedWords = new ArrayList<>(CLUtilities.getBlacklistedKeywords());
        for (int i = 0; i < results.size(); i++) {

            String nextWord = results.get(i);

            for (String blWord : blacklistedWords) {
                if (blWord.equals(nextWord)) {
                    results.remove(i);
                }

            }
        }

        return results;
    }
    
    public void removeReference(ContentType reference, Hyperlink refLink) {
        
        refItems.remove(refLink);
        refInVitro.remove(reference);
        referencePaneList.setItems(FXCollections.observableArrayList(refItems));
        
    }

    public void updateContentPanel(List<? extends ContentType> elements, ListView<Hyperlink> listview) {

        Iterator<? extends ContentType> iterator = elements.iterator();
        int subIndex = 0;
        while (iterator.hasNext() && subIndex <= MAX_RESPONSES) {
            ContentType element = iterator.next();
            Hyperlink item = new Hyperlink(element.toString());
            item.setOnAction(new HyperlinkHandler(element));
            listview.getItems().add(item);
            subIndex++;
        }

        topPaneIndicator.setVisible(false);
        bottomPaneIndicator.setVisible(false);
    }

    public void updateTagCloud(String[] keywords) {
        keywordBox.getChildren().clear();
        List<Label> cloudLabels = new ArrayList<>();
        cloudLabels = CLUtilities.generateTagLabels(keywords);
        keywordBox.getChildren().addAll(cloudLabels);

    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public class HyperlinkHandler<ActionEvent> implements EventHandler {

        ContentType referenceObj;

        public HyperlinkHandler(ContentType ref) {
            referenceObj = ref;
        }

        @Override
        public void handle(Event event) {
            Hyperlink nextRef = new Hyperlink(referenceObj.getParentValue());
            nextRef.setOnAction(new DisplayViewHandler(referenceObj, nextRef));
            refItems.add(nextRef);
            refInVitro.add(referenceObj);
            referencePaneList.setItems(FXCollections.observableArrayList(refItems));
        }
    }

    public class DisplayViewHandler<ActionEvent> implements EventHandler {

        ContentType referenceObj;
        Hyperlink refLink;

        public DisplayViewHandler(ContentType ref, Hyperlink refLink) {
            referenceObj = ref;
            this.refLink = refLink;
            
            referenceObj.setParentController(FXMLMainController.this);
            referenceObj.setRefLink(refLink);
        }

        @Override
        public void handle(Event event) {

            Stage objViewer = referenceObj.getViewingPane();
            objViewer.show();
        }

    }

    public class onSaveHandler<ActionEvent> implements EventHandler {

        @Override
        public void handle(Event event) {
            FXMLControlPaneController cpControl = CLUtilities.getCPController();
            SaveWriter writer = new SaveWriter();

            if (event.getSource().equals(saveLabel)) {

                doSave();
                writer.writeLessonPlan(cpControl.getLoadedPlan());

            } else if (event.getSource().equals(exSaveLabel)) {
                doSave();
                writer.writeLessonPlan(cpControl.getLoadedPlan());
                cpControl.refresh();
                cpControl.closeObjectivePane();

            } else if (event.getSource().equals(exitLabel)) {
                cpControl.closeObjectivePane();
            }

        }
    }

    public class StrategyTabHandler<ActionEvent> implements EventHandler {

        private String strategy = "";
        private boolean forLoad = false;
        private int count = 0;

        public StrategyTabHandler() {

        }

        public StrategyTabHandler(String strategy, int count) {
            this.strategy = strategy;
            forLoad = true;
            this.count = count;
        }

        @Override
        public void handle(Event event) {
            Tab tab;
            if (!forLoad) {
                tab = new Tab("Strategy " + strategyCount);
                strategyCount++;
            } else {
                tab = new Tab("Strategy " + count);
            }
            TextArea textArea = new TextArea();
            textArea.setEditable(true);
            textArea.setWrapText(true);
            textArea.setText(strategy);
            tab.setContent(textArea);

            tabPane.getTabs().add(tab);
        }

    }

    public void doSave() {
        String htmlText = editor.getHtmlText();
        objective.setBody(htmlText);

        ObservableList<Tab> tabs = tabPane.getTabs();
        List<String> currentStrategies = new ArrayList<>();

        for (Tab tab : tabs) {
            TextArea textArea = (TextArea) tab.getContent();
            String strategy = textArea.getText();
            System.out.println(strategy);
            currentStrategies.add(strategy);
        }

        objective.setStrategies(currentStrategies);
        System.out.println(objective.getStrategies());

        references = new ArrayList<>();
        references.addAll(refInVitro);
        objective.setReferences(references);
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public void setGradeLevel(String[] grade) {
        this.grade = grade;
    }
}
