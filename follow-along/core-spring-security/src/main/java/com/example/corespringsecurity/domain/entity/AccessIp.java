package com.example.corespringsecurity.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class AccessIp {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    public AccessIp(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
