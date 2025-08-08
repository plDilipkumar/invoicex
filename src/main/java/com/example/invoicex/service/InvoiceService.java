package com.example.invoicex.service;
import com.example.invoicex.dto.InvoiceDTO;
import com.example.invoicex.entity.Client;
import com.example.invoicex.entity.Invoice;
import com.example.invoicex.exception.ResourceNotFoundException;
import com.example.invoicex.repository.ClientRepository;
import com.example.invoicex.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import static com.example.invoicex.specification.InvoiceSpecification.*;


@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + dto.getClientId()));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(dto.getInvoiceNumber())
                .issueDate(dto.getIssueDate())
                .dueDate(dto.getDueDate())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .client(client)
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        dto.setId(saved.getId());
        return dto;
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(invoice -> InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .amount(invoice.getAmount())
                .status(invoice.getStatus())
                .clientId(invoice.getClient().getId())
                .build()
        ).collect(Collectors.toList());
    }

    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .amount(invoice.getAmount())
                .status(invoice.getStatus())
                .clientId(invoice.getClient().getId())
                .build();
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        invoiceRepository.delete(invoice);
    }

    public Page<Invoice> searchInvoices(String keyword, String status, Pageable pageable) {
        Specification<Invoice> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and(containsText(keyword));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }

        return invoiceRepository.findAll(spec, pageable);
    }
}
