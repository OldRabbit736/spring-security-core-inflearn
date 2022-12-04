package com.example.corespringsecurity.service.impl;

import com.example.corespringsecurity.domain.AccountDto;
import com.example.corespringsecurity.domain.entity.Account;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.repository.RoleRepository;
import com.example.corespringsecurity.repository.UserRepository;
import com.example.corespringsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(Account account) {
        //userRepository.save(account);
    }

    @Override
    public void modifyUser(AccountDto accountDto) {

    }

    @Override
    public List<Account> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public AccountDto getUser(Long id) {
        Account account = userRepository.findById(id).orElse(new Account());
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        Set<Role> userRoles = account.getUserRoles();
        List<String> roleNames = userRoles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        accountDto.setRoles(roleNames);
        return accountDto;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
