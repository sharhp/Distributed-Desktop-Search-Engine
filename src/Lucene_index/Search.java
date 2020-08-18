//Used for searching local directory for queried file
//Also launches thread for remote query
package Lucene_index;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {
    public static void Initial() throws Exception
    {
          String indexDir = "E:/NETWORKS/index";  
          String opt="";
          AppView.jTextArea2.setText("");
          String q = AppView.jTextField2.getText();
          if(AppView.jRadioButton2.isSelected() &&
              AppView.jRadioButton1.isSelected())          {
              AppView.jTextArea2.setText("\n\n\t\tPlease select only one search option");
              return;
          }
          if(AppView.jRadioButton1.isSelected() )
          {
              search(indexDir, q);
          }
          if(AppView.jRadioButton2.isSelected())
          {
              search1(indexDir, q);
          }
          
/*          if(!AppView.jRadioButton2.isSelected() &&
                  !AppView.jRadioButton1.isSelected())
          {
              AppView.jTextArea2.setText("\n\n\t\tPlease select a search option");
              return;
          }*/
          if(AppView.jCheckBox7.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jCheckBox8.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jCheckBox9.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jCheckBox10.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jCheckBox11.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jCheckBox12.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jRadioButton1.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          if(AppView.jRadioButton2.isSelected())
              opt = opt +"1";
          else
              opt = opt +"0";
          System.out.println("Creating thread");

          createThread(q,opt);
    }
    
    public static void createThread(String q,String opt)
    {
        if(AppView.jTextField7.getText().compareTo("")!=0)
        {
//            System.out.println("Entered creation");
        String str = AppView.jTextField7.getText();
        System.out.println("Passing: " + q +","+opt+","+str);
        SearchThread t = new SearchThread(q,opt,str,2001);
        System.out.println("Created thread");
            
        t.start();
        }
        else
        {
        SubnetUtils sbnet = new SubnetUtils(AppView.jTextField3.getText(),AppView.jTextField4.getText());
        SubnetUtils.SubnetInfo info = sbnet.getInfo();
        String addr[] = info.getAllAddresses();
        String self="";
        int num = addr.length;
            try {
                self = Inet4Address.getLocalHost().getHostAddress();
                System.out.println(self);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        SearchThread t[]=new SearchThread[num];
        for(int i=0;i<num;i++)
        {
            if(addr[i].compareTo(self) == 0)
                continue;
            System.out.println("Entered creation1- IP: "+addr[i]);
            t[i] = new SearchThread(q,opt,addr[i],2001);
            t[i].start();
        }
        }
}

    public static void search(String indexDir, String q)  throws Exception
    {
        IndexSearcher is = new IndexSearcher(FSDirectory.open(new File(indexDir)));   
        QueryParser parser = new QueryParser(Version.LUCENE_30,"contents", new StandardAnalyzer(Version.LUCENE_30)); //4
        Query query = parser.parse(q);              
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10); 
        long end = System.currentTimeMillis(); 

        String ext,result="";
        int count=0;
        for(int i=0;i<hits.scoreDocs.length;i++) {
          ScoreDoc scoreDoc = hits.scoreDocs[i];
          Document doc = is.doc(scoreDoc.doc);               
          ext = doc.get("extension");
          if((ext.compareTo("txt")==0 && AppView.jCheckBox7.isSelected())
             ||  (ext.compareTo("rtf")==0 && AppView.jCheckBox8.isSelected())
             ||  (ext.compareTo("html")==0 && AppView.jCheckBox9.isSelected())
             ||  (ext.compareTo("xls")==0 && AppView.jCheckBox10.isSelected())
             ||  (ext.compareTo("doc")==0 && AppView.jCheckBox11.isSelected())
             ||     (ext.compareTo("ppt")==0 && AppView.jCheckBox12.isSelected())

            )
          {
              result = result+"\n" +doc.get("filePath");
              count++;
          }
        }

        AppView.jTextArea2.append("\n------------------------------------------------------------------");
        AppView.jTextArea2.append("\n         Search Result for Content Search (Local Directory)");
        AppView.jTextArea2.append("\n------------------------------------------------------------------\n");
        AppView.jTextArea2.append("\nFound " + count +
                    " document(s) matching query '" + q + "'\n" +
                    "(Fetch Time: " + (end - start) +" ms)");
        AppView.jTextArea2.append(result+"\n");
        is.close();                                
    }
    public static void search1(String indexDir, String q) throws Exception
    {
        IndexSearcher is = new IndexSearcher(FSDirectory.open(new File(indexDir)));   
//        QueryParser parser = new QueryParser(Version.LUCENE_30,"fname", new StandardAnalyzer(Version.LUCENE_30)); //4
        Term t = new Term("fname",q);
        Query query = new TermQuery(t);
//        Query query = parser.parse(q);              
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10); 
        long end = System.currentTimeMillis();

        String ext,result="";
        int count=0;
        for(int i=0;i<hits.scoreDocs.length;i++) {
          ScoreDoc scoreDoc = hits.scoreDocs[i];
          Document doc = is.doc(scoreDoc.doc);               
          ext = doc.get("extension");
          if((ext.compareTo("txt")==0 && AppView.jCheckBox7.isSelected())
             ||  (ext.compareTo("rtf")==0 && AppView.jCheckBox8.isSelected())
             ||  (ext.compareTo("html")==0 && AppView.jCheckBox9.isSelected())
             ||  (ext.compareTo("xls")==0 && AppView.jCheckBox10.isSelected())
             ||  (ext.compareTo("doc")==0 && AppView.jCheckBox11.isSelected())
             ||  (ext.compareTo("rtf")==0 && AppView.jCheckBox12.isSelected())

            )
          {
              result = result+"\n" +doc.get("filePath");
              count++;
          }
        }
        AppView.jTextArea2.append("\n------------------------------------------------------------------");
        AppView.jTextArea2.append("\n          Search Result for File Name (Local Directory)");
        AppView.jTextArea2.append("\n------------------------------------------------------------------\n");

        AppView.jTextArea2.append("\nFound " + count +
                    " document(s) matching query '" + q + "'\n" +
                    "(Fetch Time: " + (end - start) +" ms)");
        AppView.jTextArea2.append(result+"\n");
       is.close();
    }
}