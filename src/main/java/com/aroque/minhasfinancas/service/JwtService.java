package com.aroque.minhasfinancas.service;

import com.aroque.minhasfinancas.model.UsuarioModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {

    // Gerar token
    String gerarToken(UsuarioModel usuario);

    // Claims são todas as informações que tem no token
    Claims obterClaims(String token) throws ExpiredJwtException;

    // Verificar se o token esta valido
    boolean isTokenValido(String token);

    // Atraves do token vou conseguir retornar o login do usuario que tentou acessar a aplicação
    String obterLoginUsuario(String token);
}
