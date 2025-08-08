package com.example.invoicex.controller;

import com.example.invoicex.dto.InvoiceDTO;
import com.example.invoicex.entity.Invoice;
import com.example.invoicex.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController{

    @Autowired
    private InvoiceService invoiceService;

    // Create invoice
    @PostMapping
    public InvoiceDTO create(@RequestBody InvoiceDTO dto) {
        return invoiceService.createInvoice(dto);
    }

    // Get all invoices (no pagination or filters)
    @GetMapping
    public List<InvoiceDTO> getAll() {
        return invoiceService.getAllInvoices();
    }

    // Get invoice by ID
    @GetMapping("/{id}")
    public InvoiceDTO getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    // Delete invoice
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }

    // ✅ Search + Filter + Pagination endpoint
    @GetMapping("/search")
    public Page<Invoice> searchInvoices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return invoiceService.searchInvoices(keyword, status, pageable);
    }
}
