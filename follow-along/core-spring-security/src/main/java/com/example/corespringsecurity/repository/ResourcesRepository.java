package com.example.corespringsecurity.repository;

import com.example.corespringsecurity.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {
    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);
}
