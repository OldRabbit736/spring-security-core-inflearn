package com.example.corespringsecurity.security.config;

import com.example.corespringsecurity.security.factory.UrlResourcesMapFactoryBean;
import com.example.corespringsecurity.security.handler.FormAccessDeniedHandler;
import com.example.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.corespringsecurity.security.provider.FormAuthenticationProvider;
import com.example.corespringsecurity.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final PasswordEncoder passwordEncoder;
    private final SecurityResourceService securityResourceService;

    public SecurityConfig(UserDetailsService userDetailsService,
                          AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource,
                          @Qualifier("formAuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler,
                          @Qualifier("formAuthenticationFailureHandler") AuthenticationFailureHandler authenticationFailureHandler,
                          PasswordEncoder passwordEncoder,
                          SecurityResourceService securityResourceService) {
        this.userDetailsService = userDetailsService;
        this.authenticationDetailsSource = authenticationDetailsSource;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.passwordEncoder = passwordEncoder;
        this.securityResourceService = securityResourceService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        /*
        UsernamePasswordAuthenticationFilter 등에서 토큰(username, password 만 담은)을 담는 것 까지는 자동으로 해 주고
        그 이후의 AuthenticationProvider 나 UserDetailsService 는 사용자가 정의해서 사용 가능하다.
         */
        //auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }

    public AuthenticationProvider authenticationProvider() {
        return new FormAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * 기본 FilterSecurityInterceptor 가 사용할 ExpressionBasedFilterInvocationSecurityMetadataSource 에 저장될 정보들
         * customFilterSecurityInterceptor 을 사용하게 되면 아래 정보들은 사용하지 않게 되므로 주석 처리
         */
       /* http
                .authorizeRequests()
                .antMatchers("/", "/users", "/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated();*/

        http
                .formLogin()
                .loginPage("/login")    // LoginUrlAuthenticationEntryPoint 지정
                .loginProcessingUrl("/login_proc")  // UsernamePasswordAuthenticationFilter 의 url matcher 지정
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);
        //.permitAll();

        http
                // FilterSecurityInterceptor 는 Request 에 "FILTER_APPLIED" attribute set 하여 한번만 처리 되도록 조치를 취한다.
                // 따라서 2개의 FilterSecurityInterceptor 가 FilterChain 에 있다고 해도, 먼저 호출되는 Filter 만 Request 를 처리하게 된다.
                // 즉 antMatcher 를 통해 설정된 SecurityMetadataSource 를 사용하는 기본 FilterSecurityInterceptor 는 없는 것과 마찬가지가 된다.
                // authorizeRequests 관련된 모든 삭제하면 (antMatcher 부분 뿐만 아니라 formLogin 에서 permitAll 삭제)
                // 기본 FilterSecurityInterceptor 가 아예 생성되지 않는다.
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);

        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    }

    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler formAccessDeniedHandler = new FormAccessDeniedHandler();
        formAccessDeniedHandler.setErrorPage("/denied");
        return formAccessDeniedHandler;
    }


    /**
     * CSS, IMAGES 등 리소스 파일은 보안 필터를 거치지 않도록 한다.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
        return filterSecurityInterceptor;
    }

    @Bean
    public AccessDecisionManager affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        return List.of(new RoleVoter());
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject());
    }

    public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        return new UrlResourcesMapFactoryBean(securityResourceService);
    }
}
