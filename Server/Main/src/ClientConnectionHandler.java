import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sun.awt.dnd.SunDropTargetEvent;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnectionHandler extends Thread
{
    //--- Static Variables ---//
    private static final String SHARED_FOLDER = "./Main/SharedFolder/";



    //--- Private Variables ---//
    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;



    //--- Constructors ---//
    public ClientConnectionHandler(Socket _clientSocket)
    {
        //Run the parent class's constructor
        super();

        //Store the socket
        this.clientSocket = _clientSocket;

        //Establish the input / output connection
        try
        {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e)
        {
            System.err.println("Failed to establish IO connection with the client socket [" + _clientSocket.getInetAddress() + ":" + _clientSocket.getPort() + "]");
            e.printStackTrace();
        }
    }



    //--- Methods ---//
    public void run()
    {
        //Wait until a command is received from the client and then handle it
        String msg = null;

        while (msg == null)
        {
            try
            {
                //Try to read a message
                msg = in.readLine();

                //If there was actually a message, handle the command
                if (msg != null)
                {
                    System.out.println(msg);

                    //Handle the message type
                    if (msg.contains("DIR"))
                        handleDIR();
                    else if (msg.contains("UPLOAD"))
                        handleUPLOAD(msg);
                    else if (msg.contains("DOWNLOAD"))
                        handleDOWNLOAD(msg);
                }
            }
            catch (IOException e)
            {
                System.err.println("Error reading command from client! Terminating connection!");
                e.printStackTrace();
            }
        }

        //Clean up once this connection / conversation is complete. Each connection handles a single command
        try
        {
            clientSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("Failed to close the socket and clean up the connection thread!");
            e.printStackTrace();
        }
    }

    public void handleDIR()
    {
        //The message to be sent back to the client
        String returnMsg = "";

        //Grab the shared folder
        File sharedFolder = new File(SHARED_FOLDER);

        //Get the list of all of the files in the shared folder
        String[] files = sharedFolder.list();

        //If there are no files in the list, simply send EMPTY. Otherwise, send the list of files
        if (files == null)
        {
            returnMsg = "EMPTY";
        }
        else
        {
            //Put all of the file names into a single one
            for (int i = 0; i < files.length; i++)
            {
                //Append the file name
                returnMsg += files[i];

                //Append a comma to separate the file names but not after the last file
                if (i < files.length - 1)
                    returnMsg += ",";
            }

        }

        //Send the return message to the client
        sendMessage(returnMsg);
    }

    public void handleUPLOAD(String _message)
    {
        //Find the filename by removing the command and separating from the file contents
        int filenameStartIndex = _message.indexOf(" ") + 1;
        int filenameEndIndex = _message.indexOf("\\n");
        String fileName = _message.substring(filenameStartIndex, filenameEndIndex);

        //Get the file contents by grabbing everything passed the first new line
        String fileContents = _message.substring(filenameEndIndex + 2);

        //Find the file
        File file = new File(SHARED_FOLDER + fileName);

        //Write to the file
        try
        {
            //If the file doesn't exist, create it
            if (!file.exists())
            {
                file.createNewFile();
            }

            //Write to the file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileContents);

            //Close the file writer
            fileWriter.close();
        }
        catch (IOException e)
        {
            System.err.println("Could not create or update the file [" + file.getName() + "] the UPLOAD command!");
            e.printStackTrace();
        }
    }

    public void handleDOWNLOAD(String _message)
    {
        //Find the index of the end of the command. Everything else is the file name
        int commandEnd = _message.indexOf(' ');

        //Extract the file name
        String fileName = _message.substring(commandEnd + 1);

        //Open the file
        File file = new File(SHARED_FOLDER + fileName);

        try
        {
            //If the file doesn't exist, return INVALID to the client. Otherwise, return its contents
            if (!file.exists())
            {
                sendMessage("INVALID");
            }
            else
            {
                //Create a scanner to read the file
                Scanner scanner = new Scanner(file);
                String fileContents = "";

                //Read the file contents and store it as a single string
                while(scanner.hasNextLine())
                {
                    fileContents += (scanner.nextLine() + "\n");
                }

                //Send the file contents to the client
                sendMessage(fileContents);
            }
        }
        catch (IOException e)
        {
            System.err.println("Exception while trying to DOWNLOAD [" + fileName + "]! Terminating connection!");
            e.printStackTrace();
        }
    }

    public void sendMessage(String _message)
    {
        //Send the message to the client through the output connection
        out.println(_message);
    }
}
