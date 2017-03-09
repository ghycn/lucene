package demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
/**
 * 创建索引 Lucene 3.0+
 * @author Administrator
 *
 */
public class LuceneTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
        //
		Analyzer a = new StandardAnalyzer();
		//保存索引文件的地方
		String indexDir = "F:\\Lucene\\indexDir";
		//将要搜索word文件的地方
		String dateDir = "F:\\Lucene\\dateDir";
		//创建Directory对象
        Directory dir = FSDirectory.open(Paths.get(indexDir));

        IndexWriterConfig iwc = new IndexWriterConfig(a);
        IndexWriter iw = new IndexWriter(dir, iwc);
		File[] files = new File(dateDir).listFiles();
		for (int i = 0; i < files.length; i++) {
			Document doc = new Document();
	        OPCPackage opcPackage = POIXMLDocument.openPackage(files[i].getAbsolutePath());
	        POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
			//创建Field对象，并放入doc对象中 
			doc.add(new TextField("contents", extractor.getText(),Field.Store.YES)); 
			doc.add(new TextField("filename", files[i].getName(),Field.Store.YES));
			doc.add(new TextField("indexDate",DateTools.dateToString(new Date(), DateTools.Resolution.DAY),Field.Store.YES));
			//写入IndexWriter
			iw.addDocument(doc);
		}
		//查看IndexWriter里面有多少个索引
		System.out.println("numDocs"+iw.numDocs());
		iw.close();
		
	}

}
