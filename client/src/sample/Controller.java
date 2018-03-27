package sample;

import java.io.*;
import java.net.*;
import java.util.*;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {


    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter clientOut = null;
    private BufferedReader clientIn = null;

    @FXML
    ListView serverFileList;
    List<String> dirList = new ArrayList<>();
    protected ListProperty<String> listProperty = new SimpleListProperty<>();



    public void initialize() {
      // getDir();

        

    }

    @FXML protected void upload(ActionEvent event) {
        System.out.println("uploaded");

    }
    @FXML protected void download(ActionEvent event) {
        System.out.println("downloaded");

    }

    private void getDir(){
        try {

            socket = new Socket("127.0.0.1",8080);
            clientOut = new PrintWriter(socket.getOutputStream(),true);
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //sent request
            clientOut.println("DIR");

            //process server input
            String input = "";
            input = clientIn.readLine();

            //convert input into a list
            String[]  dirArray = input.split(",");

            for (int i =0; i < dirArray.length; i++){

                dirList.add(dirArray[i]);
            }

            serverFileList.itemsProperty().bind(listProperty);

            listProperty.set(FXCollections.observableArrayList(dirList));

            //close socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
