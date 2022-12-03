package com.example.corespringsecurity.service.impl;

import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.repository.ResourcesRepository;
import com.example.corespringsecurity.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;

    @Override
    public Resources getResources(long id) {
        return resourcesRepository.findById(id).orElse(new Resources());
    }

    @Override
    public List<Resources> getResources() {
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Override
    public void createResources(Resources resources) {
        resourcesRepository.save(resources);
    }

    @Override
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }
}
