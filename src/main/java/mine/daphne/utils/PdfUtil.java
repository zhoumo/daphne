package mine.daphne.utils;

import java.util.Map;

import mine.daphne.model.entity.ScrumStory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfUtil {

	public static void createPdfTable(PdfPTable mainTable, ScrumStory story, Map<String, String> userNamesMap) throws Exception {
		PdfPTable table = new PdfPTable(1);
		table.addCell(createPdfCell(story.getJiraKey(), 25f, BaseColor.LIGHT_GRAY));
		table.addCell(createPdfCell(story.getSummary(), 120f, BaseColor.WHITE));
		table.addCell(createPdfCell("TDR □    Coding □    Testing □", 20f, BaseColor.WHITE));
		table.addCell(createPdfCell("研发: " + userNamesMap.get(story.getCodeTaker()) + ",  测试: " + userNamesMap.get(story.getTestTaker()), 25f, BaseColor.LIGHT_GRAY));
		mainTable.addCell(table);
	}

	public static PdfPCell createPdfCell(String text, float height, BaseColor color) throws Exception {
		BaseFont baseFont = BaseFont.createFont(new PropertiesUtil().getProperty("FONT_PATH"), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(baseFont, 15f, Font.NORMAL)));
		cell.setFixedHeight(height);
		cell.setBackgroundColor(color);
		return cell;
	}
}
