package com.example.corespringsecurity.domain.dto;

import com.example.corespringsecurity.domain.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class ResourcesDto {
    private String id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet;
}
