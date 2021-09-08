import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

/**
 * A reusable class to display error messages to the user of the application
 */
public class ErrorWindow extends Application {
   private String errorMessage = "EXAMPLE ERROR MESSAGE";
   private String errorName = "EXAMPLE ERROR NAME";

    public void setError(String errorName, String errorMessage){
        this.errorName = errorName;
        this.errorMessage = errorMessage;
    }

    @Override
    public void start(Stage stage){
        stage.setTitle("ERROR");

        BorderPane ePane = new BorderPane();

        Button okButton = new Button("Continue"); //Close the window after reading error
        okButton.setOnAction(e -> stage.close());

        ePane.setCenter(new Text(this.errorName + ": " + this.errorMessage));
        ePane.setBottom(okButton);
        ePane.setAlignment(okButton, Pos.BOTTOM_CENTER);
        ePane.setMargin(okButton, new Insets(12,12,12,12));

        Scene scene = new Scene(ePane, 300, 100);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param errorName Name of the error to present to the user
     * @param errorMessage Context of the error being presented
     */
    public void display(String errorName, String errorMessage){
        setError(errorName, errorMessage);
        start(new Stage());
    }
}
