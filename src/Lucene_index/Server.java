//Used for accepting requests from peers
//Launches threads for query from each peer - hence concurrency is established
package Lucene_index;

import java.net.*;
import java.io.*;

class  Server extends Thread
{
	private ServerSocket serverSocket;
        int port;
	Server(int port)
	{
        this.port=port;
	}

    @Override
    public void run()
    {
        try
	{
        serverSocket = new ServerSocket(port);
	System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());
	while(true)
	{
		Socket socket = serverSocket.accept();  
                System.out.println("got "+socket.getLocalPort());
		TcpThread t = new TcpThread(socket);
		t.start();
	}
        }
	catch (IOException e) {
		System.out.println("Exception on new ServerSocket: " + e);
	}
    }
}