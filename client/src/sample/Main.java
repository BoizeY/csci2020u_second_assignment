package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }


    public static void main(String[] args) {

        // if argument is less than 3, return error message and end program
        if (args.length == 2) {

                Controller.COMPUTERNAME  = args[0];
                Controller.LOCALFOLDER = args[1] + "/";

                File file = new File(Controller.LOCALFOLDER);

                //Check to see if file path is valid
                if(!file.exists()){
                    System.out.println("Filepath does not exist. \n");
                    System.exit(0);
                }

        }
        else {
            System.out.println("Invalid command. Try \"computerName\"  \"file/path/here/\" \n");
            System.exit(0);
        }

        launch(args);

    }
}
