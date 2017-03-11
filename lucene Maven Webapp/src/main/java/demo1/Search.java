package demo1;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public class Search {
	public static void main(String[] args) throws IOException, XmlException,
			OpenXML4JException, InvalidTokenOffsetsException, ParseException {
		// 保存索引文件的地方
		String indexDir = "F:\\Lucene\\indexDir";
		// Lucene Document的主要域名
		String fieldName = "FILE_TEXT";

		// 实例化Analyzer分词器
		Analyzer analyzer = new StandardAnalyzer();

		Directory directory = null;
		IndexReader ireader = null;
		IndexSearcher isearcher;

		directory = FSDirectory.open(Paths.get(indexDir));

		// 搜索过程**********************************
		// 实例化搜索器
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);

		String keyword = "主机";
		// 使用QueryParser查询分析器构造Query对象
		QueryParser qp = new QueryParser(fieldName, analyzer);
		Query query = qp.parse(keyword);
		System.out.println("Query = " + query);

		// 搜索相似度最高的5条记录
		TopDocs topDocs = isearcher.search(query, 100);
		System.out.println("命中：" + topDocs.totalHits);
		// 输出结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;

		for (int i = 0; i < Math.min(5, scoreDocs.length); ++i) {
			Document document = isearcher.doc(scoreDocs[i].doc);
			System.out.println(document.getField("FILE_NAME").stringValue());
		//	System.out.println(document.getField("INDEX_ID").stringValue());
			System.out.println(document.getField("INDEX_DATE").stringValue());
		//	System.out.println(document.getField("FILE_PATH").stringValue());
			System.out.println(" , " + scoreDocs[i].score);
			String text = document.get(fieldName);
			System.out.println(displayHtmlHighlight(query, analyzer, fieldName,
					text, 200));
		}

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
