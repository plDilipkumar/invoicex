package com.example.invoicex.repository;

import com.example.invoicex.entity.Client;
import com.example.invoicex.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByUser(User user);
    Optional<Client> findByIdAndUser(Long id, User user);
}
