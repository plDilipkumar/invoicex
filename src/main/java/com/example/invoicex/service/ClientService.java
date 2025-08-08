package com.example.invoicex.service;
import com.example.invoicex.dto.ClientDTO;
import com.example.invoicex.entity.Client;
import com.example.invoicex.exception.ResourceNotFoundException;
import com.example.invoicex.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientDTO createClient(ClientDTO dto) {
        Client client = Client.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .company(dto.getCompany())
                .phone(dto.getPhone())
                .build();
        Client saved = clientRepository.save(client);
        dto.setId(saved.getId());
        return dto;
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(c -> ClientDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .email(c.getEmail())
                        .company(c.getCompany())
                        .phone(c.getPhone())
                        .build())
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return ClientDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .company(c.getCompany())
                .phone(c.getPhone())
                .build();
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found");
        }
        clientRepository.deleteById(id);
    }
}
