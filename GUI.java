import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;


public class GUI extends Application {
    public GUI() throws FileNotFoundException {
        Interpreter interpreter = new Interpreter(GUI.this);
        Converter converter  = new Converter(GUI.this);

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

//    Label accuracy = new Label("Accuracy results: ");
    static File inputFile = new File("");
    //A flag for keeping track of unsaved changes in the editor.
    boolean isEdited = false;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Java Cross-Compiler to Python");
        /***********Label Declarations*****************/

//        accuracy.setFont(new Font("Serif", 30));
//        accuracy.setTextFill(Color.WHITE);
        /***********Button Declarations*****************/
        Button LJ = new Button("Load File");
        Button SJ = new Button("Save");
        Button compile = new Button("Compile");
        //Button SP = new Button("Save");
        Button settings = new Button("Settings");



        /***********TextArea Declarations***************/


        EJ.setPrefColumnCount(45);
        EJ.setPrefRowCount(25);
        EJ.setPromptText("Enter Java code here." +
                " You may also load existing Java files.");
//        Font font = new Font("Calibri", EJ.getFont().getSize() + 20);
//        EJ.setFont(font);
        EP.setPrefColumnCount(45);
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
//        root.add(accuracy, 0, 5);

//to allow dynamic window sizing. width-15 and height-80 is to allow the title bar to be displayed properly
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

//        Scene scene = new Scene(root, 1000, 750);
//        Scene scene = new Scene(root, 1550, 900);
          Scene scene = new Scene(root, width-15, height-80);
          //Statement to change current theme of application
          scene.getStylesheets().add("dark_mode.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        /***********Event Handling*****************/
        //Load script button
        LJ.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    EJ.setText("");
                    FileChooser fc = new FileChooser();
                    fc.setTitle("Load Java file");
                    fc.setInitialDirectory(new File("Test Programs"));
                    inputFile = fc.showOpenDialog(primaryStage);
                    Interpreter.file = inputFile;

                    Scanner scan = new Scanner(inputFile);


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
                    if(inputFile.toString() == "" && EJ.getText() == ""){
                        Alert noInputError = new Alert(Alert.AlertType.ERROR);
                        noInputError.setTitle("No Input");
                        noInputError.setContentText("No input has been provided \nEither enter java code manually or load a java file");
                        noInputError.showAndWait();

                    } else {
                        EP.setText("");
                        Interpreter.runInterpreter();
                    }
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

