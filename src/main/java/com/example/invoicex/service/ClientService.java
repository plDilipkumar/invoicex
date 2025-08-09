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
                .address(dto.getAddress())
                .build();
        Client saved = clientRepository.save(client);
        return mapToDTO(saved);
    }

    public ClientDTO updateClient(Long id, ClientDTO dto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());
        existingClient.setCompany(dto.getCompany()); // ✅ Added this
        existingClient.setPhone(dto.getPhone());
        existingClient.setAddress(dto.getAddress());

        Client updated = clientRepository.save(existingClient);
        return mapToDTO(updated);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return mapToDTO(c);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    private ClientDTO mapToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .company(client.getCompany())
                .phone(client.getPhone())
                .address(client.getAddress())
                .build();
    }
}
