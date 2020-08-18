//Sends request to the peers to look for the queried file
package Lucene_index;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class SearchThread extends Thread {
    private int port;
    private String ip;
    private String q,opt;
    SearchThread(String q,String opt,String ip,int port)
    {
        this.port=port;
        this.ip=ip;
        this.q=q;
        this.opt=opt;
    }

    @Override
    public void run()
    {
        try {
//            System.out.println("Reached serachthread");
            Socket soc = new Socket(ip, port);
            DataInputStream din = new DataInputStream(soc.getInputStream());
            DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
//            System.out.println("writing: "+ q);
            dout.writeUTF(q);
            dout.writeUTF(opt);
            String msg= din.readUTF();
            if(msg.compareTo("Nothing")!=0)
            {
            String result = "\n----------------------------------------------------------------" +
                            "\n             Result from Remote Host " + ip         +
                            "\n----------------------------------------------------------------\n";
            result = result + msg;
            result = result +"\n";
            AppView.jTextArea2.append(result);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(SearchThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearchThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}