package com.aroque.minhasfinancas.service;

import com.aroque.minhasfinancas.model.UsuarioModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface UsuarioService {

    UsuarioModel autenticar(String email, String senha);

    UsuarioModel salvarUsuario(UsuarioModel usuario);

    void validarEmail(String email);

    Optional<UsuarioModel> obterPorId(Long id);

}
