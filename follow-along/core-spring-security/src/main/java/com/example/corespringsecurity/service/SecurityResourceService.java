package com.example.corespringsecurity.service;

import com.example.corespringsecurity.domain.entity.AccessIp;
import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.repository.AccessIpRepository;
import com.example.corespringsecurity.repository.ResourcesRepository;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;
    private final AccessIpRepository accessIpRepository;

    public SecurityResourceService(ResourcesRepository resourcesRepository, AccessIpRepository accessIpRepository) {
        this.resourcesRepository = resourcesRepository;
        this.accessIpRepository = accessIpRepository;
    }

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllResources();
        resourcesList.forEach(re -> {
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            re.getRoleSet().forEach(role -> {
                configAttributes.add(new SecurityConfig(role.getRoleName()));
            });
            RequestMatcher matcher = new AntPathRequestMatcher(re.getResourceName(), re.getHttpMethod());
            result.put(matcher, configAttributes);
        });
        return result;
    }

    public List<String> getAccessIpList() {
        return accessIpRepository.findAll()
                .stream().map(AccessIp::getIpAddress).collect(Collectors.toList());
    }
}
