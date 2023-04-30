package com.nxpee.currency_exchanger.servlets;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter(urlPatterns = "/*")
public class EncodingFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        chain.doFilter(request, response);
    }
}
