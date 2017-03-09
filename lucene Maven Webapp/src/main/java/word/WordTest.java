package word;


import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class WordTest {
    public static void main(String args[]) throws Exception {  
        new WordTest().readByText();  
    }  
  
    public void readByText() throws Exception {  
        
        OPCPackage opcPackage = POIXMLDocument.openPackage("D:\\dateDir\\测试lucene.doc");
        POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
        String text2007 = extractor.getText();
        System.out.println(text2007);
        //WordExtractor extractor = new WordExtractor(in);  
        //System.out.println(extractor.getText());  
    } 
}
