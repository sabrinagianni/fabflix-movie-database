/* package common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.util.ArrayList;

@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("common.LoginFilter: " + httpRequest.getRequestURI());

        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");

        allowedURIs.add("styles/login.css");
        allowedURIs.add("styles/styles.css");
    }

    public void destroy() {
    }
} */

package common;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("[LoginFilter] Checking access for: " + httpRequest.getRequestURI());

        if (isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = JwtUtil.getCookieValue(httpRequest, "jwtToken");
        Claims claims = JwtUtil.validateToken(jwt);

        if (claims != null) {
            // Save claims to request so other servlets can use it
            httpRequest.setAttribute("claims", claims);
            chain.doFilter(request, response);
        } else {
            System.out.println("[LoginFilter] No valid JWT, redirecting to login");
            httpResponse.sendRedirect("login.html");
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");

        allowedURIs.add("styles/login.css");
        allowedURIs.add("styles/styles.css");
    }

    public void destroy() {
    }
}

