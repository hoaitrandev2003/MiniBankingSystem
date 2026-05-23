package com.cybersoft.minibank.strategy;

import com.cybersoft.minibank.entity.TransactionEntity;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class PdfExportStrategy implements ExportStrategy {


    @Override
    public String getFormat() {
        return "pdf";
    }

    @Override
    public void export(List<?> data, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("TRANSACTION LOG"));

        PdfPTable table = new PdfPTable(3);
        table.addCell("Date");
        table.addCell("Description");
        table.addCell("Status");
        table.addCell("Amount");

        for (Object obj : data) {
            TransactionEntity transactionEntity = (TransactionEntity) obj;
            table.addCell(String.valueOf(transactionEntity.getCreatedAt()));
            table.addCell(transactionEntity.getDescription());
            table.addCell(transactionEntity.getStatus());
            table.addCell(String.valueOf(transactionEntity.getAmount()));
        }

        document.add(table);
        document.close();
    }
}
