import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * A class for saving Java input and Python output.
 */
public class SaveWindow extends Application{

    private GUI rootGUI;
    @Override
    public void start(Stage stage){
        /*Node Declaration*/
        BorderPane bp = new BorderPane();
        CheckBox javaData = new CheckBox();
        CheckBox pythonData  = new CheckBox();
        Text java = new Text("Java Input");
        Text python = new Text("Python Output");
        Button saveFiles = new Button("Save File(s)");
        Button cancel = new Button("Cancel");

        /*Event Handling*/
        saveFiles.setOnAction(e ->{
            if(javaData.isSelected()){
                FileChooser fc = new FileChooser();
                fc.setTitle("Save Java File");
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java Files", ".java"));
                File javaFile = fc.showSaveDialog(stage);

                if(javaFile != null){
                    saveData(rootGUI.getJavaText(), javaFile);
                }
            }

            if(pythonData.isSelected()){
                FileChooser fc = new FileChooser();
                fc.setTitle("Save Python File");
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Python Files", ".py"));
                File pythonFile = fc.showSaveDialog(stage);

                if(pythonFile != null){
                    saveData(rootGUI.getPythonText(), pythonFile);
                }
            }

        });

        cancel.setOnAction(e ->{
            //Need to add warning here.
            stage.close();
        });

        /*Node organization/adjustments*/
        HBox saveOrCancel = new HBox(saveFiles, cancel);
        saveOrCancel.setSpacing(150);

        HBox javaContext = new HBox(javaData, java);
        HBox pythonContext = new HBox(pythonData, python);

        VBox selections = new VBox(javaContext, pythonContext);

        bp.setCenter(selections);
        bp.setMargin(selections, new Insets(12,12,12,12));

        bp.setBottom(saveOrCancel);
        bp.setAlignment(saveOrCancel, Pos.BOTTOM_CENTER);
        bp.setMargin(saveOrCancel, new Insets(12,12,12,12));


        Scene scene = new Scene(bp, 300, 100);
        stage.setTitle("Save Files");
        stage.setScene(scene);
        //scene.getStylesheets().add("dark_mode.css");
        stage.show();
    }

    public void saveData(String data, File file){
        try{
            PrintWriter pw = new PrintWriter(file);
            pw.println(data);
            pw.close();

        }catch(FileNotFoundException FNE){
            ErrorWindow ew = new ErrorWindow();
            ew.display("FILE NOT FOUND",
                    "The file could not be found.");
        }
    }

    public void display(GUI root){
        rootGUI = root;
        start(new Stage());
    }
}
