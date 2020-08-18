package Lucene_index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Date;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Index {
    String fname,lastMod,extension;
    private IndexWriter writerUpdate,writer;
    static int flag;
    public Index(String indexDir,String indexDir1) throws IOException
    {
          writerUpdate = new IndexWriter(FSDirectory.open(new File(indexDir)),         
                             new StandardAnalyzer(Version.LUCENE_30), true,
                             IndexWriter.MaxFieldLength.UNLIMITED);
         File f1 = new File("E:/NETWORKS/index");
         if(f1.exists())
         {
             writer = new IndexWriter(FSDirectory.open(new File(indexDir1)),          
                             new StandardAnalyzer(Version.LUCENE_30), false,
                             IndexWriter.MaxFieldLength.UNLIMITED);
         }
         else
         {
             writer = new IndexWriter(FSDirectory.open(new File(indexDir1)),          
                             new StandardAnalyzer(Version.LUCENE_30), true,
                             IndexWriter.MaxFieldLength.UNLIMITED);
         }

    }
    public void  Merge(String indexDir1,String indexDir) throws IOException
    {
         writer.addIndexes(IndexReader.open(FSDirectory.open(new File(indexDir))));
         writer.close();
    }
    
    static public void Initial() throws Exception
    {
        AppView.jTextArea1.setText("");
        String indexDir = "E:/NETWORKS/indexUpdate";         
        String indexDir1 = "E:/NETWORKS/index";
        String dataDir = AppView.jTextField1.getText();
        long start = System.currentTimeMillis();
        Index indexer = new Index(indexDir,indexDir1);
        int numIndexed = indexer.index(dataDir);
        indexer.close();
        if(flag==1);
            indexer.Merge(indexDir1,indexDir);
        long end = System.currentTimeMillis();
        AppView.jTextArea1.append("\nIndexing " + numIndexed + " files took "+ (end - start) + " milliseconds");
    }

    public void close() throws IOException {
        writerUpdate.close();                             
    }

    public int index(String dataDir) throws Exception
    {
    File[] files = new File(dataDir).listFiles();
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && acceptFile(f))
          {
                int whereDot = f.getName().lastIndexOf('.');
                if (0 < whereDot && whereDot <= f.getName().length() - 2 )
                {
                     fname = f.getName().substring(0, whereDot);
                     extension = f.getName().substring(whereDot+1);
                     Date date = new Date(f.lastModified());
                     lastMod = date.toString();
                }
                String indexDir1 = "E:/NETWORKS/index";               
                String str = f.getCanonicalPath();
                search(indexDir1,str,lastMod);

                if(flag==1)
                {
                    indexFile(f);
                }
                else if(flag==2)
                {
                    indexFile1(f);
                }
                else
                    AppView.jTextArea1.append("\nFile "+str+" is already indexed ");
          }
    }

    return writerUpdate.numDocs();
    }

    protected boolean acceptFile(File f) {                    
        boolean flag1=false;
        if(f.getName().endsWith(".txt") && AppView.jCheckBox1.isSelected())
            flag1=true;
        else if(f.getName().endsWith(".rtf") && AppView.jCheckBox2.isSelected())
            flag1=true;
        else if(f.getName().endsWith(".html") && AppView.jCheckBox3.isSelected())
            flag1=true;
        else if(f.getName().endsWith(".xls") && AppView.jCheckBox4.isSelected())
            flag1=true;
        else if(f.getName().endsWith(".doc") && AppView.jCheckBox5.isSelected())
            flag1=true;
        else if(f.getName().endsWith(".ppt") && AppView.jCheckBox6.isSelected())
            flag1=true;
        return flag1;
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new Field("contents", new FileReader(f)));      
        doc.add(new Field("filePath", f.getCanonicalPath(),     
                 Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("fname", fname,     
                 Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("extension", extension,     
                 Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("lastModified", lastMod,    
                 Field.Store.YES, Field.Index.NOT_ANALYZED));
        return doc;
     }

    private void indexFile(File f) throws Exception {
        AppView.jTextArea1.append("\nIndexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        if (doc != null) {
          writerUpdate.addDocument(doc);                              
        }
    }

     private void indexFile1(File f)  {
        try{
        Document doc = getDocument(f);
        writer.deleteDocuments(new Term("filePath",f.getCanonicalPath()));
        if (doc != null) {
          writerUpdate.addDocument(doc);                              
        }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public static void search(String indexDir1, String q, String lastMod)  throws Exception
    {
        IndexSearcher is = new IndexSearcher(FSDirectory.open(new File(indexDir1)));   
        Term t = new Term("filePath",q);
        Query query = new TermQuery(t);
        TopDocs hits = is.search(query, 5);
        //to check if file was modified after last index
        if(hits.scoreDocs.length!=0)
        {
              ScoreDoc scoreDoc = hits.scoreDocs[0];
              Document doc = is.doc(scoreDoc.doc);
              String lastM = doc.get("lastModified");
              if(lastM.compareTo(lastMod)==0)
              {
                  flag=0;
              }
              else
              {
                  flag=2;
                  AppView.jTextArea1.append("\nUpdating Index for "+doc.get("filePath"));
              }
        }
        else
            flag=1;
        is.close();                                
    }
}