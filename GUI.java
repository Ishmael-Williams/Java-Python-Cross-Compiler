import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javafx.scene.control.TextArea;


public class GUI extends Application {
    public GUI() throws FileNotFoundException {
        Interpreter interpreter = new Interpreter(GUI.this);
    }
    public static void main (String[] args){
        launch(args);
    }

    //Moved the text areas out of "start()" to make them visible outside this class
    TextArea EJ = new TextArea();
    TextArea EP = new TextArea();

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Java Cross-Compiler to Python");

        /***********Button Declarations*****************/
        Button LJ = new Button("Load Script");
        Button SJ = new Button("Save");
        Button compile = new Button("Compile");
        Button SP = new Button("Save");

        /***********TextArea Declarations***************/

        EJ.setPrefColumnCount(30);
        EJ.setPrefRowCount(25);

        EP.setPrefColumnCount(30);
        EP.setPrefRowCount(25);

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
        root.add(SP, 3,1);

        Scene scene = new Scene(root, 800, 650);
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
    }
    public TextArea getTextArea(){
        return EJ;
    }



}

