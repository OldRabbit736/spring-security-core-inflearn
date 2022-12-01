package com.example.corespringsecurity.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UsernamePasswordAuthenticationFilter 에서 Authentication 성공 후 실행되는 AuthenticationSuccessHandler
 * 기본 Handler 인 SavedRequestAwareAuthenticationSuccessHandler 가 자동으로 세팅되므로, saved request 로 redirect 해 주는 동작
 * 외의 동작을 하고 싶은 것이 아니라면 굳이 사용자 정의 핸들러를 만들 필요는 없는 거 같다.
 */
@Component
public class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 이전 Request 에서 AuthorizationException 이 발생했다면
        // ExceptionTranslationFilter 가 해당 Request 를 Session 에 저장해 두었을 것이다.
        // 해당 Request 를 복구한다.

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String target = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, target);
        } else {
            // 복구할 Request 가 없는 경우
            setDefaultTargetUrl("/");
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
