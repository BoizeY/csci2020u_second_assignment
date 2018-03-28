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

    // Static variable
    private static final String LOCALFOLDER = "./src/LocalFolder/";

    // private variables
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter clientOut = null;
    private BufferedReader clientIn = null;

    //FMXL variables
    @FXML
    ListView serverFileList;
    List<String> serverDirList = new ArrayList<>();
    protected ListProperty<String> serverListProperty = new SimpleListProperty<>();
    @FXML
    ListView localFileList;
    List<String> localDirList = new ArrayList<>();
    protected ListProperty<String> localListProperty = new SimpleListProperty<>();


    public void initialize() {
        getDir();
        getLocalDir();
    }

    @FXML
    protected void upload(ActionEvent event) {

        System.out.println("uploaded");

        //get the selected file name
        String selectedFileName = localFileList.getSelectionModel().getSelectedItems().get(0).toString();


        File outPutFile = new File(LOCALFOLDER + selectedFileName);
        String outPutContents = "";

        //file stuff
        try {
            //if file exist sent to server otherwise return error
            if (!outPutFile.exists()) {
                System.out.println("file does not exist");
            }
            else {
                //scanner for scanning selected file
                Scanner scanner = new Scanner(outPutFile);

                //store selected file content into a string;
                while (scanner.hasNextLine()) {
                    outPutContents += (scanner.nextLine() + "\n");
                }

            }
        }
        catch (IOException e) {
            System.err.println("Exception while trying to DOWNLOAD [" + selectedFileName + "]! Terminating connection!");
            e.printStackTrace();
        }

        //output to server
        try {

            //open socket
            socket = new Socket("127.0.0.1", 8080);
            clientOut = new PrintWriter(socket.getOutputStream(), true);

            //System.out.println("UPLOAD " + selectedFileName + "\n" + outPutContents);
            //sent out command and file content
            clientOut.println("UPLOAD " + selectedFileName + "\\n" + outPutContents);

            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        getDir();
    }

    @FXML
    protected void download(ActionEvent event) {
        System.out.println("downloaded");

        //requested file
        String fileContents = "";

        //get the selected file name
        String selectedServerFileName = serverFileList.getSelectionModel().getSelectedItems().get(0).toString();


        try {
            //open socket
            socket = new Socket("127.0.0.1", 8080);
            clientOut = new PrintWriter(socket.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //sent download request
            clientOut.println("DOWNLOAD " + selectedServerFileName);

            //read in input
            fileContents = clientIn.readLine();

            //close socket
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


        //try to find if there is already a local copy
        File file = new File(LOCALFOLDER + selectedServerFileName);

        //copying to local
        try
        {
            //create the requested file is it doesn't exist locally
            if (!file.exists())
            {
                file.createNewFile();
            }

            //write to the file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileContents);

            //close file writer
            fileWriter.close();
        }
        catch (IOException e)
        {
            System.err.println("Could not create or update the file [" + file.getName() + "] the UPLOAD command!");
            e.printStackTrace();
        }
        getLocalDir();

    }


    private void getDir() {
        try {

            socket = new Socket("127.0.0.1", 8080);
            clientOut = new PrintWriter(socket.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            //sent request
            clientOut.println("DIR");

            //process server input
            String input = "";
            input = clientIn.readLine();

            //if there is no file in the server, close socket and exit function
            if (input == "EMPTY") {
                socket.close();
                return;
            }
            // if there is file in the server pull and update list
            else {

                //clear current list
                serverDirList.clear();

                //convert input into a list
                String[] dirArray = input.split(",");

                for (int i = 0; i < dirArray.length; i++) {
                    serverDirList.add(dirArray[i]);
                }

                //set list of server file to list view
                serverFileList.itemsProperty().bind(serverListProperty);
                serverListProperty.set(FXCollections.observableArrayList(serverDirList));
            }
            //close socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getLocalDir() {

        //get the local dir
        File localFolder = new File(LOCALFOLDER);

        //if there is no file exit function
        if (localFolder == null) {
            return;
        }

        //if there is files populate the list
        else {

            //clear current list
            localDirList.clear();

            String[] localFiles = localFolder.list();

            for (int i = 0; i < localFiles.length; i++) {
                localDirList.add(localFiles[i]);
            }

            localFileList.itemsProperty().bind(localListProperty);
            localListProperty.set(FXCollections.observableArrayList(localDirList));
        }

    }
}
