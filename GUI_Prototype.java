import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.awt.*;
import javafx.scene.control.TextArea;


public class GUI_Prototype extends Application {
    public static void main (String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Java Cross-Compiler to Python");

        /***********Button Declarations*****************/
        Button LJ = new Button("Load Script");
        Button SJ = new Button("Save");
        Button compile = new Button("Compile");
        Button SP = new Button("Save");

        /***********TextArea Declarations***************/
        TextArea EJ = new TextArea();
        EJ.setPrefColumnCount(20);
        EJ.setPrefRowCount(25);
        TextArea EP = new TextArea();
        EP.setPrefColumnCount(20);
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

        Scene scene = new Scene(root, 600, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
