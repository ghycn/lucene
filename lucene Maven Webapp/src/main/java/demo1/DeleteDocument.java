package demo1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DeleteDocument {
	  public static void main(String[] args) throws ParseException {
		    // 删除title中含有关键词“美国”的文档
		    deleteDoc("INDEX_ID", "166");
		}
		public static void deleteDoc(String field, String key) throws ParseException {
			String indexDir = "F:\\Lucene\\indexDir";
			
		    Analyzer analyzer = new StandardAnalyzer();
		    IndexWriterConfig icw = new IndexWriterConfig(analyzer);
		    Path indexPath = Paths.get(indexDir);
		    Directory directory;
		    try {
		        directory = FSDirectory.open(indexPath);
		        IndexWriter indexWriter = new IndexWriter(directory, icw);
		    	QueryParser qp = new QueryParser(field, analyzer);
				Query query = qp.parse(key);
		        indexWriter.deleteDocuments(query);
		        indexWriter.commit();
		        indexWriter.close();
		        System.out.println("删除完成!");
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
}
