package com.example.corespringsecurity.controller.admin;

import com.example.corespringsecurity.domain.entity.Account;
import com.example.corespringsecurity.service.RoleService;
import com.example.corespringsecurity.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserManagerController {

    private final UserService userService;
    private final RoleService roleService;

    public UserManagerController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin/accounts")
    public String getUsers(Model model) {
        List<Account> accounts = userService.getUsers();
        model.addAttribute("accounts", accounts);
        return "admin/user/list";
    }
}
