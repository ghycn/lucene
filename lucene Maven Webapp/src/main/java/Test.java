import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;

public class Test {
	/** 
     * 对搜索返回的前n条结果进行分页显示 
     * @param keyWord       查询关键词 
     * @param pageSize      每页显示记录数 
     * @param currentPage   当前页  
     */  
    public static void paginationQuery(String keyWord,int pageSize,int currentPage) throws ParseException, CorruptIndexException, IOException {  
        String[] fields = {"FILE_TEXT","FILE_NAME"};  
		Analyzer analyzer = new StandardAnalyzer();

		String indexDir = "F:\\Lucene\\indexDir";

        QueryParser queryParser = new MultiFieldQueryParser(fields,analyzer);  
        Query query = queryParser.parse(keyWord);  
        Directory directory = FSDirectory.open(Paths.get(indexDir));

        IndexReader indexReader  = DirectoryReader.open(directory);  
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
           
        //TopDocs 搜索返回的结果  
        TopDocs topDocs = indexSearcher.search(query,200);//只返回前100条记录  
        int totalCount = topDocs.totalHits; // 搜索结果总数量  
        ScoreDoc[] scoreDocs = topDocs.scoreDocs; // 搜索返回的结果集合  
           
        //查询起始记录位置  
        int begin = pageSize * (currentPage - 1) ;  
        //查询终止记录位置  
        int end = Math.min(begin + pageSize, scoreDocs.length);  
        System.out.println("end:"+end);
           
        //进行分页查询  
        for(int i=begin;i<end;i++) {  
            int docID = scoreDocs[i].doc;  
            Document doc = indexSearcher.doc(docID);  
            String FILE_TEXT = doc.get("FILE_TEXT");  
            String FILE_NAME = doc.get("FILE_NAME");  
            System.out.println("FILE_TEXT is : "+FILE_TEXT);  
            System.out.println("FILE_NAME is : "+FILE_NAME);  
        }     
    } 
    
    public static void main(String[] args) throws CorruptIndexException, ParseException, IOException {
    	paginationQuery("主机",10,1);
	}
}
