package com.example.corespringsecurity.controller.admin;

import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.repository.RoleRepository;
import com.example.corespringsecurity.service.ResourcesService;
import com.example.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @GetMapping("/admin/resources")
    public String getResources(Model model) {
        List<Resources> resources = resourcesService.getResources();
        model.addAttribute("resources", resources);
        return "admin/resource/list";
    }
}
