import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SearchEngine {
    public static void main(String[] args) {
        SearchEngine se = new SearchEngine();
        String filePath="  ";//创建索引的存储目录
        System.out.println("新浪爱问-理工学科 搜索引擎 v0.0.1");

        System.out.println("搜索问题，输入\":q\"（英文字符）退出");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("搜索关键词：");
            String s = in.nextLine();
            if(s.equals(":q"))
                break;
            se.searrh(filePath, s);
        }
        in.close();
    }

    public void searrh(String filePath, String queryStr){
        File f=new File(filePath);
        try {
            IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(FSDirectory.open(f)));
            Analyzer analyzer = new IKAnalyzer();
            //指定field为“name”，Lucene会按照关键词搜索每个doc中的name。
            QueryParser parser = new QueryParser(Version.LUCENE_4_10_0,"title", analyzer);
            Query query=parser.parse(queryStr);
            TopDocs hits=searcher.search(query,5);//前面几行代码也是固定套路，使用时直接改field和关键词即可
            for(ScoreDoc doc:hits.scoreDocs){
                Document d = searcher.doc(doc.doc);
                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.println("问题: " + d.get("title"));
                System.out.println(d.get("answer"));
                System.out.println("领域: " + d.get("field") + "， url: " + d.get("url"));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
