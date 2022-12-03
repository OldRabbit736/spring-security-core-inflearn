package com.example.corespringsecurity.controller.admin;

import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/admin/roles")
    public String getRoles(Model model) {
        List<Role> roles = roleService.getRoles();
        model.addAttribute("roles", roles);
        return "admin/role/list";
    }
}
