package com.example.invoicex.repository;

import com.example.invoicex.entity.Invoice;
import com.example.invoicex.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    List<Invoice> findByUser(User user);
    Optional<Invoice> findByIdAndUser(Long id, User user);
}
