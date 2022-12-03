package com.example.corespringsecurity.security.service;

import com.example.corespringsecurity.domain.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class AccountContext extends User {

    /**
     * AuthenticationProvider::authenticate 에서 AuthenticationToken 의 principal 로서 사용하기 위해 생성한 필드
     */
    private final Account account;

    public AccountContext(Account account, List<GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
