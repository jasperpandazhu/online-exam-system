package com.official.OfficialProject.filter;

import com.official.OfficialProject.service.CommonService;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ManagementLoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain Chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse) res;
        String url =  (request).getRequestURL().toString().toLowerCase();
        if (url.matches("(.*)html(.*)")){
            if (url.matches("(.*)login(.*)")) {
                Chain.doFilter(request, response);
            }
            else {
                if (CommonService.getCookie(request,"name") == null || CommonService.getCookieValue(request, "name") == null || CommonService.getCookieValue(request, "name").trim() == "" ){
                    response.sendRedirect("/ManagementLogin.html");
                    return;
                }
            }
        }
        Chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
