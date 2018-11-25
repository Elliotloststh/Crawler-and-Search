import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Index {
    public static void main(String[] args) {
        Index Indexer = new Index();
        String filePath="   ";//创建索引的存储目录
        Indexer.createIndex(filePath);//创建索引
    }

    public void createIndex(String filePath) {
        File f=new File(filePath);
        IndexWriter iwr=null;
        try {
            Directory dir= FSDirectory.open(f);
            Analyzer analyzer = new IKAnalyzer();

            IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
            iwr=new IndexWriter(dir,conf);
            File file = new File("  ");
            File file1 = new File("  ");
            File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中
            File[] fs1 = file1.listFiles();
            for(File dataFile:fs){              
                String fileName = dataFile.getName(); 
                if (!dataFile.isDirectory() && fileName.endsWith("txt")) {  
                    String field = fileName.replaceAll(".txt","");
                    getDocuments(field, dataFile, iwr);
                }
            }
            for(File dataFile:fs1){               
                String fileName = dataFile.getName();  
                if (!dataFile.isDirectory() && fileName.endsWith("txt")) {  
                    String field = fileName.replaceAll(".txt","");
                    getDocuments(field, dataFile, iwr);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            iwr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getDocuments(String field, File data, IndexWriter iwr){

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(data));
            StringBuilder url = new StringBuilder();
            StringBuilder title = new StringBuilder();
            StringBuilder answer = new StringBuilder();
            String tempString;
            boolean flag = true;
            int state = 0;      //0为url，1为title，2为answer，3为end
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if(tempString.equals("==========url==========")) {
                    if(flag) {
                        flag = false;
                        continue;
                    }
                    Document doc=new Document();
                    Field f0 = new TextField("field", field, Field.Store.YES);
                    Field f1 = new TextField("url", url.toString(), Field.Store.YES);
                    Field f2 = new TextField("title", title.toString().replaceAll("<br\\s*/?>", "\r\n"), Field.Store.YES);
                    Field f3 = new TextField("answer", answer.toString(), Field.Store.YES);
                    doc.add(f0);
                    doc.add(f1);
                    doc.add(f2);
                    doc.add(f3);
                    iwr.addDocument(doc);
                    url.delete(0, url.length());
                    state = 0;
                    continue;
                } else if(tempString.equals("==========title==========")) {
                    title.delete(0, title.length());
                    state = 1;
                    continue;
                } else if(tempString.equals("==========answer==========")) {
                    answer.delete(0, answer.length());
                    state = 2;
                    continue;
                } else if(tempString.equals("==========end==========")) {
                    state = 3;
                    continue;
                }
                switch (state) {
                    case 0: {
                        url.append(tempString + "\r\n");
                        break;
                    }
                    case 1: {
                        title.append(tempString + "\r\n");
                        break;
                    }
                    case 2: {
                        answer.append(tempString + "\r\n");
                        break;
                    }
                    default :
                        break;
                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

}
