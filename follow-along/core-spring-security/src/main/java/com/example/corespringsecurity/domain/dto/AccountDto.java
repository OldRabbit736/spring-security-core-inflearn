package com.example.corespringsecurity.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountDto {
    private String id;
    private String username;
    private String password;
    private String email;
    private String age;
    private List<String> roles;
}
