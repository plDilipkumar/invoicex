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
    public ClientDTO updateClient(Long id, ClientDTO dto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        // Update fields
        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());
        existingClient.setPhone(dto.getPhone());
        // Add other fields as needed

        Client updatedClient = clientRepository.save(existingClient);

        // Convert back to DTO and return
        return ClientDTO.builder()
                .id(updatedClient.getId())
                .name(updatedClient.getName())
                .email(updatedClient.getEmail())
                .phone(updatedClient.getPhone())
                // Add other fields here
                .build();
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
