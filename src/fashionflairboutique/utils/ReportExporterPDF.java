/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
/**
 *
 * @author Chethiya
 */
public class ReportExporterPDF {
    public static void exportToPDF(JTable table, File destinationFile) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(destinationFile));
        document.open();

        // 1. Header Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph title = new Paragraph("Fashion Flair Boutique - Sales Report\n\n", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // 2. Setup Table Matrix
        TableModel model = table.getModel();
        PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
        pdfTable.setWidthPercentage(100);

        // 3. Render Headers
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (int col = 0; col < model.getColumnCount(); col++) {
            PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(col), headFont));
            cell.setBackgroundColor(Color.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            pdfTable.addCell(cell);
        }

        // 4. Render Data Rows
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object val = model.getValueAt(row, col);
                PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", bodyFont));
                cell.setPadding(4);
                pdfTable.addCell(cell);
            }
        }

        document.add(pdfTable);
        document.close();
    }
}
