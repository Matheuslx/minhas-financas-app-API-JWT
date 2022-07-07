package com.aroque.minhasfinancas.service.Impl;

import com.aroque.minhasfinancas.exception.ErroAutentificacao;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.repositories.UsuarioRepository;
import com.aroque.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UsuarioRepository repository;

    // Não criei o construtor, caso dê errado, pode ser isso
    public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
        super();
        this.repository = repository;
        this.encoder = encoder;
    }

    private void criptografarSenha(UsuarioModel usuario){
        String senha = usuario.getSenha();
        String senhaCripto = encoder.encode(senha);
        usuario.setSenha(senhaCripto);
    }

    @Override
    public UsuarioModel autenticar(String email, String senha) {
        Optional<UsuarioModel> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()) {
            throw new ErroAutentificacao("Usuário não encontrado para o email informado.");
        }

        boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());

        if(!senhasBatem){
            throw new ErroAutentificacao("Senha Inválida.");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public UsuarioModel salvarUsuario(UsuarioModel usuario) {
        validarEmail(usuario.getEmail());
        criptografarSenha(usuario);
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe){
            throw new RegraNegocioExecption("Já existe um usário cadastrado com este email.");
        }

    }

    @Override
    public Optional<UsuarioModel> obterPorId(Long id) {
        return repository.findById(id);
    }
}
