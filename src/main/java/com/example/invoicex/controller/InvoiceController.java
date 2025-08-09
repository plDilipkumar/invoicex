package com.example.invoicex.controller;

import com.example.invoicex.dto.InvoiceDTO;
import com.example.invoicex.entity.Invoice;
import com.example.invoicex.service.InvoiceService;
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
    @PutMapping("/test-put")
    public ResponseEntity<String> testPut() {
        return ResponseEntity.ok("PUT works!");
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
}
