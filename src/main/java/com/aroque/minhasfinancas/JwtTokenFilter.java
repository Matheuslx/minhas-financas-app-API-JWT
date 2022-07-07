package com.aroque.minhasfinancas;

import com.aroque.minhasfinancas.service.Impl.SecurityUserDetailsService;
import com.aroque.minhasfinancas.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;
    @Autowired
    SecurityUserDetailsService userDetailsService;

    public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Interceptando a requisição, para decodificar o token e jogar o usuario que está dentro do token dentro
    // do contexto do spring security, para poder validar
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Bearer tokenGerado...
        if(authorization != null && authorization.startsWith("Bearer")){
            //Separando o token
            String token = authorization.split(" ")[1];
            boolean isTokenValid = jwtService.isTokenValido(token);

            if(isTokenValid){
                String login = jwtService.obterLoginUsuario(token);
                // Carregar o usuario do banco de acordo com o token
                UserDetails usuarioAutenticado = userDetailsService.loadUserByUsername(login);
                UsernamePasswordAuthenticationToken user =
                        new UsernamePasswordAuthenticationToken(
                                usuarioAutenticado,null, usuarioAutenticado.getAuthorities());
                user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Estou pegando o contexto do spring security e jogando a autenticação
                SecurityContextHolder.getContext().setAuthentication(user);
            }
        }
            // Dando continuidade a requisição
                    filterChain.doFilter(request, response);

    }
}
