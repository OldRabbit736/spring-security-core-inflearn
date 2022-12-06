package com.example.corespringsecurity.security.init;

import com.example.corespringsecurity.service.RoleHierarchyService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

@Component
public class SecurityInitializer implements ApplicationRunner {

    private final RoleHierarchyService roleHierarchyService;
    private final RoleHierarchyImpl roleHierarchy;

    public SecurityInitializer(RoleHierarchyService roleHierarchyService, RoleHierarchyImpl roleHierarchy) {
        this.roleHierarchyService = roleHierarchyService;
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public void run(ApplicationArguments args) {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }
}
