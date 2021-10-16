import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;


public class GUI extends Application {
    public GUI() throws FileNotFoundException {
        Interpreter interpreter = new Interpreter(GUI.this);
    }
    public static void main (String[] args) throws IOException {
//        args = new String[2];
//        args[0] = "--module-path C:/Program Files/javafx-sdk-11.0.2/lib";
//        args[1] = "--add-modules=javafx.controls,javafx.fxml";
        launch(args);
    }

    //Moved the text areas out of "start()" to make them visible outside this class
    TextArea EJ = new TextArea();
    TextArea EP = new TextArea();

    //A flag for keeping track of unsaved changes in the editor.
    boolean isEdited = false;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Java Cross-Compiler to Python");

        /***********Button Declarations*****************/
        Button LJ = new Button("Load Script");
        Button SJ = new Button("Save");
        Button compile = new Button("Compile");
        //Button SP = new Button("Save");
        Button settings = new Button("Settings");


        /***********TextArea Declarations***************/

        EJ.setPrefColumnCount(30);
        EJ.setPrefRowCount(25);
        EJ.setPromptText("Enter Java code here." +
                " You may also load existing Java files.");

        EP.setPrefColumnCount(30);
        EP.setPrefRowCount(25);
        EP.setPromptText("Python code is printed here as output.");

        /***********GridPane Declaration*****************/
        /***********Define the architecture of the pane**/
        GridPane root = new GridPane();
        root.setHgap(30);
        root.setVgap(10);
        root.setPadding(new Insets(10,10,10,10));

        /***********Add Elements*****************/
        root.add(EJ, 0, 0);
        root.add(EP,3,0 );
        root.add(LJ, 0,1);
        root.add(SJ, 0, 2);
        root.add(compile, 0,3);
        //root.add(SP, 3,1);
        root.add(settings, 0, 4);

        Scene scene = new Scene(root, 800, 650);

        //Statement to change current theme of application
        scene.getStylesheets().add("dark_mode.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        /***********Event Handling*****************/
        //Load script button
        LJ.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    File file = new File("Test 1.txt");
                    Scanner scan = new Scanner(file);
                    String currentLine;
                    while(scan.hasNextLine()) {
                        currentLine = scan.nextLine();
                        System.out.println(currentLine);
                        EJ.appendText(currentLine + "\n");
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        compile.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                try {
                    Interpreter.runInterpreter();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        SJ.setOnAction(e -> {
            if (isEdited) {
                Alert saveAlert = new Alert(Alert.AlertType.WARNING);
                saveAlert.setTitle("Save Changes?");
                ButtonType yes = new ButtonType("Yes");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                saveAlert.getButtonTypes().setAll(yes, cancel);
                saveAlert.setContentText("Are you sure you want to save your changes? " +
                        "This may overwrite your data.");
                Optional<ButtonType> result = saveAlert.showAndWait();

                if (result.get() == yes) {
                    SaveWindow sw = new SaveWindow();
                    sw.display(this);
                    isEdited = false; //File is up to date.
                }
            }
            else{
                SaveWindow sw = new SaveWindow();
                sw.display(this);
                isEdited = false; //File is up to date.
            }
        });

        //Set flag whenever data in text areas is changed.
     EJ.textProperty().addListener((obs,old,niu) ->{
         isEdited = true;
     });

     EP.textProperty().addListener((obs,old,niu) ->{
         isEdited = true;
     });
    }
    public TextArea getTextArea(){
        return EJ;
    }

    //Added these methods to return text data outside of GUI class (for saving)
    public String getJavaText(){return EJ.getText();}
    public String getPythonText(){return EP.getText(); }
}

