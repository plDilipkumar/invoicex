package com.example.invoicex.service;

import com.example.invoicex.entity.Invoice;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Invoice", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" \n"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

            table.addCell("Invoice #");
            table.addCell(String.valueOf(invoice.getInvoiceNumber()));
            table.addCell("Issue Date");
            table.addCell(invoice.getIssueDate() == null ? "" : df.format(invoice.getIssueDate()));
            table.addCell("Due Date");
            table.addCell(invoice.getDueDate() == null ? "" : df.format(invoice.getDueDate()));
            table.addCell("Amount");
            table.addCell(invoice.getAmount() == null ? "" : invoice.getAmount().toString());
            table.addCell("Status");
            table.addCell(invoice.getStatus() == null ? "" : invoice.getStatus());
            table.addCell("Client");
            table.addCell(invoice.getClient() == null ? "" : invoice.getClient().getName());

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}


