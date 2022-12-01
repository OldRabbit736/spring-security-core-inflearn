package com.example.corespringsecurity.security.config;

import com.example.corespringsecurity.security.common.AjaxLoginAuthenticationEntryPoint;
import com.example.corespringsecurity.security.filter.AjaxLoginProcessingFilter;
import com.example.corespringsecurity.security.handler.AjaxAccessDeniedHandler;
import com.example.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(0)
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public AjaxSecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                              @Qualifier("ajaxAuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler,
                              @Qualifier("ajaxAuthenticationFailureHandler") AuthenticationFailureHandler authenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/messages").hasRole("MANAGER")
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())  // bean 으로 주입받아도 되지만 딱히 bean 등록이 필요치 않을 땐 이렇게 그냥 넣어줘도 된다.
                .accessDeniedHandler(new AjaxAccessDeniedHandler())

                .and()
                .csrf().disable()

        ;
    }

    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return ajaxLoginProcessingFilter;
    }
}
