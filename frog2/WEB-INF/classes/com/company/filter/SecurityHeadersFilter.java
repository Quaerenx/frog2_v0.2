package com.company.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse res = (HttpServletResponse) response;
            HttpServletRequest req = (request instanceof HttpServletRequest) ? (HttpServletRequest) request : null;
            // 기본 보안 헤더
            res.setHeader("X-Content-Type-Options", "nosniff");
            res.setHeader("X-Frame-Options", "SAMEORIGIN");
            res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            // 근래 브라우저 대응: COOP/CORP, Permissions-Policy
            res.setHeader("Cross-Origin-Opener-Policy", "same-origin");
            res.setHeader("Cross-Origin-Resource-Policy", "same-origin");
            res.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            // HTTPS 환경에서만 HSTS 적용 (개발 환경 HTTP는 브라우저에서 무시되지만 명시적으로 가드)
            if (req != null && req.isSecure()) {
                res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            }
            // 인증 응답 캐싱 방지 (로그인 세션 존재 시)
            if (req != null) {
                HttpSession session = req.getSession(false);
                if (session != null && session.getAttribute("user") != null) {
                    res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    res.setHeader("Pragma", "no-cache");
                    res.setDateHeader("Expires", 0);
                }
            }
            // CSP: 현 상태에서 인라인/스타일 사용 중이므로 임시로 'unsafe-inline'을 허용.
            // 점진적으로 인라인 제거 후 'unsafe-inline'을 제외하는 것을 권장.
            String csp = String.join("; ",
                "default-src 'self'",
                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://code.jquery.com",
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com",
                "img-src 'self' data:",
                "font-src 'self' data: https://fonts.gstatic.com https://fonts.googleapis.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net",
                "connect-src 'self'",
                "object-src 'none'",
                "base-uri 'self'",
                "frame-ancestors 'self'",
                "form-action 'self'"
            );
            res.setHeader("Content-Security-Policy", csp);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}