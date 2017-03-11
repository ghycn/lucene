package demo1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;

public class CreateIndex {
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
		Directory directory = null;
		IndexWriter iwriter;

		//保存索引文件的地方。
		String indexDir = "F:\\Lucene\\indexDir";
		//将要搜索word文件的地方
		String dateDir = "F:\\Lucene\\dateDir";
		// Lucene Document的主要域名
		String fieldName = "text";
		
		// 实例化Analyzer分词器
		Analyzer analyzer = new StandardAnalyzer();
		
		directory = FSDirectory.open(Paths.get(indexDir));
		
		IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
		iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		iwriter = new IndexWriter(directory, iwConfig);
		
		File[] files = new File(dateDir).listFiles();
		Document doc = new Document();
		for (int i = 0; i < files.length; i++) {
			String absolutePath = files[i].getAbsolutePath();//文件路径
			String fileName = files[i].getName();
			String lowerCaseName = fileName.toLowerCase();
			if(lowerCaseName.endsWith(".doc")||lowerCaseName.endsWith(".docx")){
				String text = FlieRead.getTextFromWORD(absolutePath);
				//创建Field对象，并放入doc对象中 
				doc.add(new TextField(fieldName, text,Field.Store.YES)); 
				doc.add(new TextField("filename",files[i].getName(),Field.Store.YES));
				doc.add(new TextField("indexDate",DateTools.dateToString(new Date(), DateTools.Resolution.DAY),Field.Store.YES));
				//写入IndexWriter
				iwriter.addDocument(doc);
			}else if(lowerCaseName.endsWith(".pdf")){
				String text = FlieRead.getTextFromPDF(absolutePath);
				//创建Field对象，并放入doc对象中 
				doc.add(new TextField(fieldName, text,Field.Store.YES)); 
				doc.add(new TextField("filename",files[i].getName(),Field.Store.YES));
				doc.add(new TextField("indexDate",DateTools.dateToString(new Date(), DateTools.Resolution.DAY),Field.Store.YES));
				//写入IndexWriter
				iwriter.addDocument(doc);
			}else if(lowerCaseName.endsWith(".txt")){
				String text = FlieRead.getTextFromTXT(absolutePath);
				//创建Field对象，并放入doc对象中 
				doc.add(new TextField(fieldName, text,Field.Store.YES)); 
				doc.add(new TextField("filename",files[i].getName(),Field.Store.YES));
				doc.add(new TextField("indexDate",DateTools.dateToString(new Date(), DateTools.Resolution.DAY),Field.Store.YES));
				//写入IndexWriter
				iwriter.addDocument(doc);
			}else if(lowerCaseName.endsWith(".xls")||lowerCaseName.endsWith(".xlsx")){
				String text = FlieRead.getTextFromEXCEL(absolutePath);
				//创建Field对象，并放入doc对象中 
				doc.add(new TextField(fieldName, text,Field.Store.YES)); 
				doc.add(new TextField("filename",files[i].getName(),Field.Store.YES));
				doc.add(new TextField("indexDate",DateTools.dateToString(new Date(), DateTools.Resolution.DAY),Field.Store.YES));
				//写入IndexWriter
				iwriter.addDocument(doc);
			}
		}
		System.out.println("Docs:"+iwriter.numDocs());
		iwriter.close();
	}
	@Test
	public void test(){
		String str = "测试LUCENE.DOC";
		System.out.println(str.toLowerCase());

	}
	
}
