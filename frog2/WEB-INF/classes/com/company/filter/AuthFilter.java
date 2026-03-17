package com.company.filter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilter implements Filter {
    private static final Set<String> OPEN_PATHS = Set.of(
        "/login", "/logout", "/login.jsp", "/favicon.ico"
    );
    private static final Pattern STATIC_RESOURCES = Pattern.compile("^/(resources|images|css|js|webjars)/.*");
    private static final Pattern ERROR_PAGES = Pattern.compile("^/error/.*");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        boolean open = OPEN_PATHS.contains(path)
                || STATIC_RESOURCES.matcher(path).matches()
                || ERROR_PAGES.matcher(path).matches();

        if (open) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean authenticated = (session != null && session.getAttribute("user") != null);

        if (authenticated) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // no-op
    }
}
