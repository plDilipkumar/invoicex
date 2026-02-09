package com.example.invoicex.specification;

import com.example.invoicex.entity.Invoice;
import com.example.invoicex.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

    public static Specification<Invoice> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Invoice> containsText(String keyword) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("invoiceNumber")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("client").get("name")), "%" + keyword.toLowerCase() + "%")
        );
    }

    public static Specification<Invoice> belongsToUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}
