package demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * 演示高亮搜索结果
 * 
 * @author hankcs
 * 
 */
public class HighLighterDemo {

	public static void main(String[] args) {
		// Lucene Document的主要域名
		String fieldName = "text";

		// 实例化Analyzer分词器
		Analyzer analyzer = new StandardAnalyzer();

		Directory directory = null;
		IndexWriter iwriter;
		IndexReader ireader = null;
		IndexSearcher isearcher;
		try {
			// 索引过程**********************************
			// 建立内存索引对象
			directory = new RAMDirectory();

			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
			iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			iwriter = new IndexWriter(directory, iwConfig);
			{
				// 加入一个文档
				Document doc = new Document();
				doc.add(new TextField(
						fieldName,
						"我白天是一名语言学习者，晚上是一名初级码农。空的时候喜欢看算法和应用数学书，也喜欢悬疑推理小说，ACG方面喜欢型月、轨迹。喜欢有思想深度的事物，讨厌急躁、拜金与安逸的人。目前在魔都某女校学习，这是我的个人博客。闻道有先后，术业有专攻，请多多关照。你喜欢写代码吗？",
						Field.Store.YES));
				doc.add(new TextField("title", "关于hankcs", Field.Store.YES));
				iwriter.addDocument(doc);
			}
			{
				// 再加入一个
				Document doc = new Document();
				doc.add(new TextField(fieldName, "程序员喜欢黑夜", Field.Store.YES));
				doc.add(new TextField("title", "关于程序员", Field.Store.YES));
				iwriter.addDocument(doc);
			}
			iwriter.close();

			// 搜索过程**********************************
			// 实例化搜索器
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
			
			String keyword = "喜欢";
			// 使用QueryParser查询分析器构造Query对象
			QueryParser qp = new QueryParser(fieldName, analyzer);
			Query query = qp.parse(keyword);
			System.out.println("Query = " + query);

			// 搜索相似度最高的5条记录
			TopDocs topDocs = isearcher.search(query, 5);
			System.out.println("命中：" + topDocs.totalHits);
			// 输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;

			for (int i = 0; i < Math.min(5, scoreDocs.length); ++i) {
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				System.out.print(targetDoc.getField("title").stringValue());
				System.out.println(" , " + scoreDocs[i].score);

				String text = targetDoc.get(fieldName);
				System.out.println(displayHtmlHighlight(query, analyzer,
						fieldName, text, 200));
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		} finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
