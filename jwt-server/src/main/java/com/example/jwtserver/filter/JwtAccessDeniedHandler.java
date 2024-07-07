package com.example.jwtserver.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
  private final AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException)
      throws IOException, ServletException, IOException {
    accessDeniedHandler.handle(request, response, accessDeniedException);
  }
}