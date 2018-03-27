import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FileServer
{
    //--- Constants ---//
    private static final int SERVER_PORT = 8080;
    private static final int MAX_CLIENTS = 25;



    //--- Private Variables ---//
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private ClientConnectionHandler[] threads = null;
    private int numClients = 0;
    private Vector messages = new Vector();



    //--- Constructors ---//
    public FileServer()
    {
        try
        {
            //Set up the server socket and threads
            serverSocket = new ServerSocket(SERVER_PORT);
            threads = new ClientConnectionHandler[MAX_CLIENTS];
            System.out.println("Server started successfully ... ");

            //Handle incoming requests
            while (true)
            {
                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");
                threads[numClients] = new ClientConnectionHandler(clientSocket);
                threads[numClients].start();
                numClients++;
            }
        }
        catch (IOException e) {
            System.err.println("IOException while trying to create the file server!");
            e.printStackTrace();
        }
    }
}
