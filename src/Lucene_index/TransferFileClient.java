package Lucene_index;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

class TransferFileClient extends Thread
{
Socket ClientSoc;
String file;
DataInputStream din;
DataOutputStream dout;

TransferFileClient(Socket soc, String file)
{
    try
    {
	ClientSoc=soc;
	din=new DataInputStream(ClientSoc.getInputStream());
	dout=new DataOutputStream(ClientSoc.getOutputStream());
        this.file = file;
        start();
    }
    catch(Exception ex)
    {
    }
}

void ReceiveFile(String filename1) throws Exception
{
    String fpath,name1;
    dout.writeUTF("GET");
    dout.writeUTF(filename1);
    String msgFromServer=din.readUTF();
    if(msgFromServer.compareTo("File Not Found")==0)
    {
        JOptionPane.showMessageDialog(null, "File Not Found on Server!", "Download", JOptionPane.ERROR_MESSAGE);
        return;
    }
    else if(msgFromServer.compareTo("READY")==0)
    {
        String name= din.readUTF();
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int val = jfc.showSaveDialog(null);
        if(val == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = jfc.getSelectedFile();
                fpath = file.getAbsolutePath();
                int i = name.lastIndexOf('.');
                name1 = fpath + name.substring(i);
                System.out.println(name1);
                dout.writeUTF("READY");
//                System.out.println("Receiving File ...");
                File f=new File(name1);
                
                FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);
                    }
                }while(ch!=-1);
            fout.close();
            System.out.println(din.readUTF());
            
            JOptionPane.showMessageDialog(null,"   File Download Complete!", "Download",JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e){};
     }
     else
     {
         dout.writeUTF("DISCONNECT");
     }
    }
}

@Override
public void run()
{
        try {
            ReceiveFile(file);
        } catch (Exception ex) {
            Logger.getLogger(TransferFileClient.class.getName()).log(Level.SEVERE, null, ex);
        }
}
}