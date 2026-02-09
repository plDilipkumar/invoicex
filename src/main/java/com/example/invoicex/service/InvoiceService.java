package com.example.invoicex.service;

import com.example.invoicex.dto.InvoiceDTO;
import com.example.invoicex.entity.Client;
import com.example.invoicex.entity.Invoice;
import com.example.invoicex.entity.User;
import com.example.invoicex.exception.ResourceNotFoundException;
import com.example.invoicex.repository.ClientRepository;
import com.example.invoicex.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.invoicex.specification.InvoiceSpecification.*;

@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final AuthService authService;
    private final PdfService pdfService;
    private final MailService mailService;

    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        User current = authService.getCurrentUser();
        Client client = clientRepository.findByIdAndUser(dto.getClientId(), current)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + dto.getClientId()));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(dto.getInvoiceNumber())
                .issueDate(dto.getIssueDate())
                .dueDate(dto.getDueDate())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .client(client)
                .user(current)
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        // Send PDF to client email by default if email exists
        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            byte[] pdf = pdfService.generateInvoicePdf(saved);
            try {
                mailService.sendInvoice(client.getEmail(),
                        "Your Invoice " + saved.getInvoiceNumber(),
                        "Please find attached your invoice.",
                        pdf,
                        "invoice-" + saved.getInvoiceNumber() + ".pdf");
            } catch (Exception ignored) {
                // Non-blocking
            }
        }

        dto.setId(saved.getId());
        return dto;
    }

    public List<InvoiceDTO> getAllInvoices() {
        User current = authService.getCurrentUser();
        return invoiceRepository.findByUser(current).stream().map(invoice -> InvoiceDTO.builder()
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
        User current = authService.getCurrentUser();
        Invoice invoice = invoiceRepository.findByIdAndUser(id, current)
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

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO dto) {
        User current = authService.getCurrentUser();
        Invoice invoice = invoiceRepository.findByIdAndUser(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        Client client = clientRepository.findByIdAndUser(dto.getClientId(), current)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + dto.getClientId()));

        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setAmount(dto.getAmount());
        invoice.setStatus(dto.getStatus());
        invoice.setClient(client);

        Invoice updated = invoiceRepository.save(invoice);

        return InvoiceDTO.builder()
                .id(updated.getId())
                .invoiceNumber(updated.getInvoiceNumber())
                .issueDate(updated.getIssueDate())
                .dueDate(updated.getDueDate())
                .amount(updated.getAmount())
                .status(updated.getStatus())
                .clientId(updated.getClient().getId())
                .build();
    }

    public void deleteInvoice(Long id) {
        User current = authService.getCurrentUser();
        Invoice invoice = invoiceRepository.findByIdAndUser(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        invoiceRepository.delete(invoice);
    }

    public Page<Invoice> searchInvoices(String keyword, String status, Pageable pageable) {
        User current = authService.getCurrentUser();
        Specification<Invoice> spec = Specification.where(belongsToUser(current));

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and(containsText(keyword));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }

        return invoiceRepository.findAll(spec, pageable);
    }
}
