//Search class to process query from peers
//Similar as Search- does the same function
package Lucene_index;

import java.io.*;
import java.net.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

class TcpThread extends Thread
{
    Socket soc;
    TcpThread(Socket socket)
    {
	this.soc= socket;
        
    }
		
    @Override
    public void run()
	{
	String indexDir = "E:/NETWORKS/index";               
	try{
	DataInputStream din=new DataInputStream(soc.getInputStream());
	DataOutputStream dout=new DataOutputStream(soc.getOutputStream());
	String msg="";
//        System.out.println("Tcpthread");
        String q = din.readUTF();
        String opt = din.readUTF();
//        System.out.println("Tcpthread received: "+q+","+opt);
//        System.out.println(q);
//        System.out.println(opt);

        char a[]=new char[8];
        a=opt.toCharArray();
        
        IndexSearcher is = new IndexSearcher(FSDirectory.open(new File(indexDir)));   

        Query query;
        QueryParser parser;
        if(a[6]=='1')
        {
            parser = new QueryParser(Version.LUCENE_30,"contents", new StandardAnalyzer(Version.LUCENE_30)); //4
            query = parser.parse(q);
        }
        else
        {
            Term t = new Term("fname",q);
            query = new TermQuery(t);
//            parser = new QueryParser(Version.LUCENE_30,"fname", new StandardAnalyzer(Version.LUCENE_30)); //4
        }
        //Query query = parser.parse(q);              
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10); 
        long end = System.currentTimeMillis();
        String ext="";

        Boolean bol[] = new Boolean[6];
          if(a[0]=='1')
              bol[0]=true;
          else
              bol[0]=false;
          if(a[1]=='1')
              bol[1]=true;
          else
              bol[1]=false;
          if(a[2]=='1')
              bol[2]=true;
          else
              bol[2]=false;
          if(a[3]=='1')
              bol[3]=true;
          else
              bol[3]=false;
          if(a[4]=='1')
              bol[4]=true;
          else
              bol[4]=false;
          if(a[5]=='1')
              bol[5]=true;
          else
              bol[5]=false;
        int count=0;
        //Filtering based on opt (selections made at the peer)
        for(int i=0;i<hits.scoreDocs.length;i++)
        {
          ScoreDoc scoreDoc = hits.scoreDocs[i];
          Document doc = is.doc(scoreDoc.doc);
          ext = doc.get("extension");

//          System.out.println(ext.compareTo("txt"));
          
          if((ext.compareTo("txt")==0 && bol[0])||  (ext.compareTo("rtf")==0 && bol[1])
             ||  (ext.compareTo("html")==0 && bol[2]) ||  (ext.compareTo("xls")==0 && bol[3])
             ||  (ext.compareTo("doc")==0 && bol[4]) ||  (ext.compareTo("ppt")==0 && bol[5]))
              {
                  msg+="\n" +doc.get("filePath");
                  count++;
              }
        }
        if(count!=0)
        {
            msg="\nFound " + count +
                    " document(s) matching query '" + q + "'\n(Fetch Time: "+(end - start)+"ms)"+msg ;
        }
        else
        {
            msg = "Nothing";
        }
        dout.writeUTF(msg);
        is.close();
	dout.flush();
	dout.close();
	din.close();
	soc.close();
	}
        catch (ParseException ex) {
            System.out.print("96");
            Logger.getLogger(TcpThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException e)
	{
		e.printStackTrace();
	}
    }
}