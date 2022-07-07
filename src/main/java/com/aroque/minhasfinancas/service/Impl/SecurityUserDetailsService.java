package com.aroque.minhasfinancas.service.Impl;

import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public SecurityUserDetailsService(UsuarioRepository usuarioRepository ) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Codigo para pegar o usuário
        // Qualquer tipo de codigo de usuario pode estar carregando aq
        UsuarioModel usuarioEncontrado = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado"));

        return User.builder()
                .username(usuarioEncontrado.getEmail())
                .password(usuarioEncontrado.getSenha())
                .roles("USER")
                .build();
    }
}
