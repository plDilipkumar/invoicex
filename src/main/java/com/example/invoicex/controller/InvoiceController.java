package com.example.invoicex.controller;

import com.example.invoicex.dto.InvoiceDTO;
import com.example.invoicex.entity.Invoice;
import com.example.invoicex.service.InvoiceService;
import com.example.invoicex.service.PdfService;
import com.example.invoicex.service.MailService;
import com.example.invoicex.repository.InvoiceRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MailService mailService;

    // CREATE Invoice
    @PostMapping
    public ResponseEntity<InvoiceDTO> create(@RequestBody InvoiceDTO dto) {
        return ResponseEntity.ok(invoiceService.createInvoice(dto));
    }

    // READ All Invoices
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAll() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // READ Single Invoice
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // UPDATE Invoice
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> update(@PathVariable Long id, @RequestBody InvoiceDTO dto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, dto));
    }

    // DELETE Invoice
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    // SEARCH + FILTER + PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Page<Invoice>> searchInvoices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(invoiceService.searchInvoices(keyword, status, pageable));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        byte[] pdf = pdfService.generateInvoicePdf(invoice);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoice.getInvoiceNumber() + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    // Test email configuration
    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            mailService.sendText(email, "Test Email from InvoiceX", "This is a test email to verify email configuration.");
            return ResponseEntity.ok("Test email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }
}
