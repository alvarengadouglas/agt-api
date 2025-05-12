package com.betmotion.agentsmanagement.security;

import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.betmotion.agentsmanagement.service.SecurityService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

  SecurityService securityService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    final String header = request.getHeader(AUTHORIZATION);
    if (isEmpty(header) || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    final String token = header.split(" ")[1].trim();

    Optional<UserToken> validToken = securityService.validToken(token);
    if (!validToken.isPresent()) {
      chain.doFilter(request, response);
      return;
    }

    UserDetails userDetails = convertTokenToUserDetails(validToken.get());

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(request, response);
  }

  private UserDetails convertTokenToUserDetails(UserToken userToken) {
    return new AppUserDetails(userToken.getUsername(), "****",
        userToken.getAuthorities()
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(toSet()), userToken.getUserId(), userToken.getId());
  }

}
