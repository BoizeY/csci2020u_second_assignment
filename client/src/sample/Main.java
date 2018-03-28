package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        if (args.length > 3) {

            Boolean isCN = false;
            Boolean isFP = false;

            //parse through argument
            for (int i = 0; i < args.length; i++) {

                // if -cn take the following inputs as computer name
                if (args[i] == "-cn") {
                    isCN = true;
                    isFP = false;
                    continue;

                    // if -fp, take the following inputs as file path name
                } else if (args[i] == "-fp") {
                    isFP = true;
                    isCN = false;
                    continue;
                }

                if (isCN) {

                    Controller.COMPUTERNAME += args[i] + " ";

                } else if (isFP) {

                    Controller.LOCALFOLDER = args[i];

                }

                //if there is not -cn or -fp command return error message and end program
                if(isCN == false && isFP == false){
                    System.out.println("Invalid commands. Try -cn computerName -fp file/path/here/ \n");
                    return;
                }
            }

        }
        else {
            System.out.println("Argument too short. Try -cn computerName -fp file/path/here/ \n");
            return;
        }

        launch(args);

    }
}
