package demo1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

public class FlieRead {

	public static void main(String[] args) {
		// String str =
		// FlieRead.getTextFromTXT("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt");
		// System.out.println(str);
		//FlieRead.getTextFromEXCEL(filePath)();
	}

	/**
	 * 读取txt文件的内容
	 * 
	 * @param file
	 *            想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String getTextFromTXT(String filePath) {
		StringBuffer result = new StringBuffer();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String str = "";
				while ((str = bufferedReader.readLine()) != null) {// 使用readLine方法，一次读一行
					result.append(str + System.lineSeparator());
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return result.toString();

	}

	/**
	 * 读取word内容
	 * 
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws XmlException
	 */
	public static String getTextFromWORD(String filePath) throws IOException,
			XmlException, OpenXML4JException {
		OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
		POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
		return extractor.getText();
	}

	/**
	 * 读取pdf内容
	 * 
	 * @param pdfFilePath
	 * @return
	 */
	public static String getTextFromPDF(String filePath) {
		String result = null;
		FileInputStream is = null;
		PDDocument document = null;
		try {
			is = new FileInputStream(filePath);
			PDFParser parser = new PDFParser(is);
			parser.parse();
			document = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			result = stripper.getText(document);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 读取excel文件
	 * 
	 */
	public static String getTextFromEXCEL(String filePath) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer result = new StringBuffer();
		try {
			// 同时支持Excel 2003、2007
			File excelFile = new File(filePath); // 创建文件对象
			FileInputStream is = new FileInputStream(excelFile); // 文件流
			Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel2003/2007/2010都是可以处理的
			int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
			// 遍历每个Sheet
			for (int s = 0; s < sheetCount; s++) {
				Sheet sheet = workbook.getSheetAt(s);
				int rowCount = sheet.getPhysicalNumberOfRows(); // 获取总行数
				// 遍历每一行
				for (int r = 0; r < rowCount; r++) {
					Row row = sheet.getRow(r);
					if(row!=null){
						int cellCount = row.getPhysicalNumberOfCells(); // 获取总列数
						// 遍历每一列
						for (int c = 0; c < cellCount; c++) {
							Cell cell = row.getCell(c);
							if (cell != null) {
								int cellType = cell.getCellType();
								String cellValue = null;
								switch (cellType) {
								case Cell.CELL_TYPE_STRING: // 文本
									cellValue = cell.getStringCellValue();
									break;
								case Cell.CELL_TYPE_NUMERIC: // 数字、日期
									if (DateUtil.isCellDateFormatted(cell)) {
										cellValue = fmt.format(cell
												.getDateCellValue()); // 日期型
									} else {
										cellValue = String.valueOf(cell
												.getNumericCellValue()); // 数字
									}
									break;
								case Cell.CELL_TYPE_BOOLEAN: // 布尔型
									cellValue = String.valueOf(cell
											.getBooleanCellValue());
									break;
								case Cell.CELL_TYPE_BLANK: // 空白
									cellValue = cell.getStringCellValue();
									break;
								case Cell.CELL_TYPE_ERROR: // 错误
									cellValue = "错误";
									break;
								case Cell.CELL_TYPE_FORMULA: // 公式
									cellValue = "错误";
									break;
								default:
									cellValue = "错误";
								}
								result.append(cellValue + "    ");
							} else {
								result.append(null + "    ");
							}
						}
					}
					result.append(System.lineSeparator());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(result.toString());
		return result.toString();
	}
}
