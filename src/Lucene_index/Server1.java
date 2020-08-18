//Used for accepting requests for file downloads from peers
//Launches threads to handle each request
package Lucene_index;

import java.net.*;
import java.io.*;

class  Server1 extends Thread
{
	private ServerSocket serverSocket;
        int port;
	Server1(int port)
	{
        this.port=port;
	}

    @Override
    public void run()
    {
        try
		{
                ServerSocket soc=new ServerSocket(port);
		while(true)
		{
			System.out.println("Waiting for Connection ...");
			TransferFile t=new TransferFile(soc.accept());
		}
		}
		catch (IOException e) {
			System.out.println("Exception on new ServerSocket: " + e);
		}
    }
}