package com.example.corespringsecurity.controller.admin;

import com.example.corespringsecurity.domain.dto.ResourcesDto;
import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.repository.RoleRepository;
import com.example.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.corespringsecurity.service.ResourcesService;
import com.example.corespringsecurity.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    public ResourcesController(ResourcesService resourcesService, RoleRepository roleRepository, RoleService roleService, UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
        this.resourcesService = resourcesService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.filterInvocationSecurityMetadataSource = filterInvocationSecurityMetadataSource;
    }

    @GetMapping("/admin/resources")
    public String getResources(Model model) {
        List<Resources> resources = resourcesService.getResources();
        model.addAttribute("resources", resources);
        return "admin/resource/list";
    }

    @PostMapping("/admin/resources")
    public String createResources(ResourcesDto resourcesDto) {
        Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());
        Resources resources = modelMapper.map(resourcesDto, Resources.class);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        resources.setRoleSet(roles);
        resourcesService.createResources(resources);
        filterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/register")
    public String viewRoles(Model model) {
        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        ResourcesDto resources = new ResourcesDto();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        resources.setRoleSet(roleSet);
        model.addAttribute("resources", resources);
        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResources(@PathVariable Long id, Model model) {
        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        Resources resources = resourcesService.getResources(id);
        ResourcesDto resourcesDto = modelMapper.map(resources, ResourcesDto.class);
        model.addAttribute("resources", resourcesDto);
        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String removeResources(@PathVariable Long id) {
        resourcesService.deleteResources(id);
        filterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }
}
