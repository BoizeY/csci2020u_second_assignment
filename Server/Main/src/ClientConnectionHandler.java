import java.io.*;
import java.net.Socket;

public class ClientConnectionHandler extends Thread
{
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
            out = new PrintWriter(clientSocket.getOutputStream());
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
        File sharedFolder = new File("SharedFolder");

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
    }

    public void handleDOWNLOAD(String _message)
    {

    }

    public void sendMessage(String _message)
    {
        //Send the message to the client through the output connection
        out.println(_message);
    }
}
