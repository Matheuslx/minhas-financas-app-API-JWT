package com.aroque.minhasfinancas.repositories;

import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.UsuarioModel;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test") // Mudando para usar o application-"test".properties
@DataJpaTest // Ao encerrar um teste os dados são apagados para não incluenciar os outros testes
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    // Teste de Integração
    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        // Cenário
        // Criando um usuario e salvando na base de dados
        UsuarioModel usuario = UsuarioModel.builder().nome("Teste").email("teste@gmail.com").build();
        entityManager.persist(usuario);

        //Ação / Execução
        // Fazendo a verificação se existe ou não
        boolean result = repository.existsByEmail("teste@gmail.com");

        // Verificação
        // Verificando se o resultado do metodo anterior é verdadeiro ou falso
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail(){
        //Cenário
        // Apagando os dados da base, pois deve não deve ter nenhum usuario cadastrado
//        repository.deleteAll();

        //Ação
        boolean result = repository.existsByEmail("teste@gmail.com");

        //Verificação
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        //Cenário
        UsuarioModel usuario = UsuarioModel.builder().nome("teste").email("teste@gmail.com").senha("senha").build();

        // Ação
        UsuarioModel usuarioSalvo = repository.save(usuario);

        // Verificação
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        //Cenário
        UsuarioModel usuario = UsuarioModel.builder().nome("Teste").email("teste@gamil.com").senha("senha").build();
        entityManager.persist(usuario);

        // Verificação
        Optional<UsuarioModel> result = repository.findByEmail("teste@gamil.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase(){
        //Cenário
        //Não deve criar um usuário

        // Verificação
        Optional<UsuarioModel> result = repository.findByEmail("teste@gamil.com");

        Assertions.assertThat(result.isPresent()).isFalse();
    }


}
