package demo;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 搜索索引 Lucene 3.0+
 * 
 * @author Administrator
 * 
 */
public class LuceneSearch {

	public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
		String fieldName = "contents";
		
		// 保存索引文件的地方
		String indexDir = "F:\\Lucene\\indexDir";
		
		//标准分词器
		Analyzer analyzer = new StandardAnalyzer();
		
		//索引文件夹
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		
		//读取索引
		IndexReader reader = DirectoryReader.open(dir);
		
		//基于索引搜索
		IndexSearcher is = new IndexSearcher(reader);
		
		//查询器
		QueryParser parser = new QueryParser(fieldName, analyzer);
		
		Query query = parser.parse("高铁");
		
		
		
		TopDocs topDocs = is.search(query, 1000);
		System.out.println("总共匹配多少个：" + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		// 应该与topDocs.totalHits相同
		System.out.println("多少条数据：" + hits.length);
		for (ScoreDoc scoreDoc : hits) {
			System.out.println("匹配得分：" + scoreDoc.score);
			System.out.println("文档索引ID：" + scoreDoc.doc);
			Document document = is.doc(scoreDoc.doc);
			
			String text = document.get(fieldName);
			TokenStream str = analyzer.tokenStream(fieldName, text);
			str.toString();
			
			displayHtmlHighlight(query, analyzer,
					fieldName, text, 200);
			System.out.println(document.get("contents"));
			System.out.println("fileName:"+document.get("filename"));
		}
		reader.close();
		dir.close();
	}
	
	/**
	 * 获取高亮显示结果的html代码
	 * 
	 * @param query
	 *            查询
	 * @param analyzer
	 *            分词器
	 * @param fieldName
	 *            域名
	 * @param fieldContent
	 *            域内容
	 * @param fragmentSize
	 *            结果的长度（不含html标签长度）
	 * @return 结果（一段html代码）
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	private static String displayHtmlHighlight(Query query, Analyzer analyzer,
			String fieldName, String fieldContent, int fragmentSize)
			throws IOException, InvalidTokenOffsetsException {
		// 创建一个高亮器 
		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(
				"<font color='red'>", "</font>"), new QueryScorer(query));
		Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
		highlighter.setTextFragmenter(fragmenter);
		return highlighter.getBestFragment(analyzer, fieldName, fieldContent);
	}
	
}
