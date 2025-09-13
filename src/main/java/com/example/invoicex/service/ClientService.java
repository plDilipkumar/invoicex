package com.example.invoicex.service;

import com.example.invoicex.dto.ClientDTO;
import com.example.invoicex.entity.Client;
import com.example.invoicex.entity.User;
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
    private final AuthService authService;

    public ClientDTO createClient(ClientDTO dto) {
        User current = authService.getCurrentUser();
        Client client = Client.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .company(dto.getCompany())
                .phone(dto.getPhone())
                .user(current)
                .build();
        Client saved = clientRepository.save(client);
        dto.setId(saved.getId());
        return dto;
    }

    public List<ClientDTO> getAllClients() {
        User current = authService.getCurrentUser();
        return clientRepository.findByUser(current)
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
        User current = authService.getCurrentUser();
        Client c = clientRepository.findByIdAndUser(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return ClientDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .company(c.getCompany())
                .phone(c.getPhone())
                .build();
    }

    public ClientDTO updateClient(Long id, ClientDTO dto) {
        User current = authService.getCurrentUser();
        Client client = clientRepository.findByIdAndUser(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setCompany(dto.getCompany());
        client.setPhone(dto.getPhone());
        clientRepository.save(client);
        dto.setId(client.getId());
        return dto;
    }

    public void deleteClient(Long id) {
        User current = authService.getCurrentUser();
        Client client = clientRepository.findByIdAndUser(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        clientRepository.delete(client);
    }
}
