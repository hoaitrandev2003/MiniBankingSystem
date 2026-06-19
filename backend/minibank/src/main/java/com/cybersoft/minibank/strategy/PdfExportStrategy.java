package com.cybersoft.minibank.strategy;

import com.cybersoft.minibank.entity.TransactionEntity;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PdfExportStrategy implements ExportStrategy {


    @Override
    public String getFormat() {
        return "pdf";
    }

    @Override
    public void export(List<?> data, HttpServletResponse response) throws IOException {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph title = new Paragraph("TRANSACTION LOG");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Date");
        table.addCell("Description");
        table.addCell("Status");
        table.addCell("Amount");

        for (Object obj : data) {
            TransactionEntity transactionEntity = (TransactionEntity) obj;

            if (transactionEntity.getCreatedAt() != null) {
                String formattedDate = transactionEntity.getCreatedAt().format(formatter);
                table.addCell(formattedDate);
            } else {
                table.addCell("");
            }

            table.addCell(transactionEntity.getDescription() != null ? transactionEntity.getDescription() : "");
            table.addCell(transactionEntity.getStatus() != null ? transactionEntity.getStatus() : "");
            table.addCell(transactionEntity.getAmount() != null ? transactionEntity.getAmount().toString() : "0");
        }

        document.add(table);
        document.close();
    }
}
