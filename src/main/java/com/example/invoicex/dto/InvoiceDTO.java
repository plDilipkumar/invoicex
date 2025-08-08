package com.example.invoicex.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String status;
    private Long clientId;
}
