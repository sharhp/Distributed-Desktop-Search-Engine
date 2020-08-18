//Used for transferring the requested file to the corresponding peer
package Lucene_index;

import java.io.*;
import java.net.*;

class TransferFile extends Thread
{
    Socket ClientSoc;
    DataInputStream din;
    DataOutputStream dout;
    TransferFile(Socket soc)
    {
    	try
    	{
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
//            System.out.println("FTP Client Connected ...");
            start();
        }
	catch(Exception ex)
	{
	}
    }

void SendFile() throws Exception
{
    String filename=din.readUTF();
    File f=new File(filename);
    if(!f.exists())
    {
    	dout.writeUTF("File Not Found");
    	return;
    }
    else
    {
	dout.writeUTF("READY");
	dout.writeUTF(f.getName());
        String reply = din.readUTF();
        if(reply.compareTo("DISCONNECT")==0)
            return;
	FileInputStream fin=new FileInputStream(f);
	int ch;
	do
	{
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
	}
	while(ch!=-1);
        fin.close();
    	dout.writeUTF("File Received Successfully");
    }
}

@Override
public void run()
{
    while(true)
    {
	try
	{
//            System.out.println("Waiting for Command ...");
            String Command=din.readUTF();
            if(Command.compareTo("GET")==0)
            {
  //          	System.out.println("\tGET Command Received ...");
		SendFile();
		continue;
            }
            else if(Command.compareTo("DISCONNECT")==0)
            {
//		System.out.println("\tDisconnect Command Received ...");
		System.exit(1);
            }
	}
	catch(Exception ex)
	{
	}
    }
}
}