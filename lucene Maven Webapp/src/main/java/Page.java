import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Page {
	
	public static void main(String[] args) throws IOException {
		Page page = new Page();  
		page.searchPage("主机", 1, 10);
	}
	
	/**
	 * 根据页码和分页大小获取上一次最后一个ScoreDoc
	 * @param pageIndex
	 * @param pageSize
	 * @param query
	 * @param indexSearcher
	 * @return
	 * @throws IOException
	 */
	private ScoreDoc getLastScoreDoc(int pageIndex,int pageSize,Query query,IndexSearcher indexSearcher) throws IOException{
		if(pageIndex==1){//如果是第一页返回空
			return null;
	    }
		
		int num = pageSize*(pageIndex-1);//获取上一页的数量
		TopDocs tds = indexSearcher.search(query, num);
		return tds.scoreDocs[num-1];
	}
	
	public void searchPage(String query,int pageIndex,int pageSize) throws IOException{
		IndexSearcher indexSearcher = getSearcher();
        String[] fields = {"FILE_TEXT","FILE_NAME"};  

		QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
		Query q = null;
		try {
			q = parser.parse(query);
			//获取上一页的最后一个元素
			ScoreDoc lastScoreDoc = getLastScoreDoc(pageIndex, pageSize, q, indexSearcher);
			//通过最后一个元素搜索下页的pageSize个元素
			TopDocs topDocs = indexSearcher.searchAfter(lastScoreDoc,q,pageSize);
			System.out.println(topDocs.scoreDocs.length);
			for (ScoreDoc item : topDocs.scoreDocs) {
				Document doc = indexSearcher.doc(item.doc);
				//System.out.println(doc.get("FILE_TEXT"));
				System.out.println(doc.get("FILE_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IndexSearcher getSearcher() throws IOException {
		String indexDir = "F:\\Lucene\\indexDir";
		
        Directory directory = FSDirectory.open(Paths.get(indexDir));

        IndexReader indexReader  = DirectoryReader.open(directory);  

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  

		return indexSearcher;
	}
}
