package com.example.corespringsecurity.security.listener;

import com.example.corespringsecurity.domain.entity.AccessIp;
import com.example.corespringsecurity.domain.entity.Account;
import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.domain.entity.RoleHierarchy;
import com.example.corespringsecurity.repository.AccessIpRepository;
import com.example.corespringsecurity.repository.ResourcesRepository;
import com.example.corespringsecurity.repository.RoleHierarchyRepository;
import com.example.corespringsecurity.repository.RoleRepository;
import com.example.corespringsecurity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourcesRepository resourcesRepository;
    private static final AtomicInteger count = new AtomicInteger(0);
    private final PasswordEncoder passwordEncoder;
    private final RoleHierarchyRepository roleHierarchyRepository;
    private final AccessIpRepository accessIpRepository;

    public SetupDataLoader(UserRepository userRepository,
                           RoleRepository roleRepository,
                           ResourcesRepository resourcesRepository,
                           PasswordEncoder passwordEncoder,
                           RoleHierarchyRepository roleHierarchyRepository,
                           AccessIpRepository accessIpRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.resourcesRepository = resourcesRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchyRepository = roleHierarchyRepository;
        this.accessIpRepository = accessIpRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("ContextRefreshedEvent occurred!");
        if (alreadySetup) return;
        log.info("Security resources set");
        setupSecurityResources();
        setupAccessIpData();
        alreadySetup = true;
    }

    private void setupAccessIpData() {
        AccessIp byIpAddress = accessIpRepository.findByIpAddress("0:0:0:0:0:0:0:1");
        if (byIpAddress == null) {
            AccessIp accessIp = new AccessIp("0:0:0:0:0:0:0:1");
            accessIpRepository.save(accessIp);
        }
    }

    private void setupSecurityResources() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        Resources resources = createResourceIfNotFound("/admin/**", "", roles, "url");
        Account account = createUserIfNotFound("admin", "pass", "admin@gmail.com", 10, roles);

        Role userRole = createRoleIfNotFound("ROLE_USER", "유저");
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        createRoleHierarchyIfNotFound(managerRole, adminRole);
        createRoleHierarchyIfNotFound(userRole, managerRole);
    }

    @Transactional
    public void createRoleHierarchyIfNotFound(Role childRole, Role parentRole) {
        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder().childName(parentRole.getRoleName()).build();
        }
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder().childName(childRole.getRoleName()).build();
        }
        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);
    }

    @Transactional
    public Account createUserIfNotFound(String username, String password, String email, int age, Set<Role> roleSet) {
        Account account = userRepository.findByUsername(username);
        if (account != null) return account;
        Account newAccount = Account.builder()
                .username(username)
                .email(email)
                .age(age)
                .password(passwordEncoder.encode(password))
                .userRoles(roleSet)
                .build();
        return userRepository.save(newAccount);
    }

    @Transactional
    public Resources createResourceIfNotFound(String resourceName, String httpMethod, Set<Role> roleSet, String resourceType) {
        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);
        if (resources != null) return resources;
        Resources newResources = Resources.builder()
                .resourceName(resourceName)
                .httpMethod(httpMethod)
                .roleSet(roleSet)
                .resourceType(resourceType)
                .orderNum(count.incrementAndGet())
                .build();
        return resourcesRepository.save(newResources);
    }

    @Transactional
    public Role createRoleIfNotFound(String roleName, String roleDesc) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role != null) return role;
        Role newRole = Role.builder()
                .roleName(roleName)
                .roleDesc(roleDesc)
                .build();
        return roleRepository.save(newRole);
    }
}
