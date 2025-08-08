package com.example.invoicex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicex.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
