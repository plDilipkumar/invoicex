package com.example.invoicex.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String company;
    private String phone;
    private String address; // Add this to match entity fields
}
