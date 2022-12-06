package com.example.corespringsecurity.service.impl;

import com.example.corespringsecurity.domain.entity.RoleHierarchy;
import com.example.corespringsecurity.repository.RoleHierarchyRepository;
import com.example.corespringsecurity.service.RoleHierarchyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    public RoleHierarchyServiceImpl(RoleHierarchyRepository roleHierarchyRepository) {
        this.roleHierarchyRepository = roleHierarchyRepository;
    }

    @Transactional
    @Override
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchies = roleHierarchyRepository.findAll();
        StringBuilder concatRoles = new StringBuilder();
        roleHierarchies.forEach(roleHierarchy -> {
            if (roleHierarchy.getParentName() != null) {
                concatRoles.append(roleHierarchy.getParentName().getChildName());
                concatRoles.append(" > ");
                concatRoles.append(roleHierarchy.getChildName());
                concatRoles.append("\n");
            }
        });
        return concatRoles.toString();
    }
}
