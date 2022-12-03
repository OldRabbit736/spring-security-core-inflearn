package com.example.corespringsecurity.security.service;

import com.example.corespringsecurity.domain.entity.Account;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * TODO: FormAuthenticationProvider::authentication 메서드에 @Transactional 붙이고 여기서는 땠을 때
     * 이 메서드가 호출되면 왜 아래와 같은 예외가 발생하나?
     * org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.corespringsecurity.domain.entity.Account.userRoles, could not initialize proxy - no Session
     * Role 을 가지고 오려면 세션이 필요한데 없다고 한다... 이 메서드를 호출하는 FormAuthenticationProvider::authentication 메서드에 @Transactional
     * 붙어 있기만 하면 세션이 생성되는 것 아니었나?... 뭔가 트랜잭션 전파(?)와 같은 개념과 관련 있을 듯 하다. 알아보자.
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }
        Set<Role> userRoles = account.getUserRoles();
        List<GrantedAuthority> roles = userRoles
                .stream()
                .map(Role::getRoleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new AccountContext(account, roles);
    }
}
