package com.aroque.minhasfinancas.service;

import com.aroque.minhasfinancas.exception.ErroAutentificacao;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.repositories.UsuarioRepository;
import com.aroque.minhasfinancas.service.Impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)// Para criar o contexto de injeção de dependencia
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioService service;
    @MockBean
    UsuarioRepository repository;

    //Criação do mock para injeção de dependência
    // Será substituida, adicionando essa instancia do mock dentro do contexto springFramework
    // para quando precisarmos injetar ele cria a instancia mocada e injetar onde precisar
///   @Before
///   public void setUp(){
        // Substituido pela annotation @MockBean
        //repository = Mockito.mock(UsuarioRepository.class);

        // Criando um SPY
//        service = Mockito.spy(UsuarioServiceImpl.class);
//        service = new UsuarioServiceImpl(repository);
//    }


    @Test(expected = Test.None.class)
    public void deveSalvarUsuarioComSucesso(){
        //Cénario
        // O SPY age de forma diferente, primeiro chamamos o service e depois os seus metodos
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        UsuarioModel usuarioCriado = UsuarioModel.builder().id(1L).nome("Teste").email("teste@gmail.com").senha("senha").build();
        Mockito.when(repository.save(Mockito.any(UsuarioModel.class))).thenReturn(usuarioCriado);

        //Ação
        UsuarioModel usuarioSalvo = service.salvarUsuario(new UsuarioModel());

        // Verificação
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Teste");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("teste@gmail.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test(expected = RegraNegocioExecption.class)
    public void aoDeveSalvarUsuarioComEmailJaCadastrado(){
        //Cénario
        UsuarioModel usuarioCriado = UsuarioModel.builder().email("teste@gmail.com").build();
        Mockito.doThrow(RegraNegocioExecption.class).when(service).validarEmail("teste@gmail.com");

        //Ação
        service.salvarUsuario(usuarioCriado);

        // Verificação
        Mockito.verify(repository, Mockito.never()).save(usuarioCriado);
    }



    @Test(expected = Test.None.class)
    public void deveAutenticarUsuarioComSucesso(){
        // Cenário, usuário já criado
        String email = "teste@gmail.com";
        String senha = "senha";

        UsuarioModel usuarioCriado = UsuarioModel.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuarioCriado)); // Retorna esse usuario q acabamos de criar no cenário

        // Ação
        UsuarioModel result = service.autenticar(email, senha);

        // Verificação, verificando se autenticou e retornou uma instancia de usuario
        Assertions.assertThat(result).isNotNull();
    }


    // Quando usamos o catchThrowable não precisamos do (expected)
    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){
        // Cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // Ação
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("testeErrado@gmail.com", "senha"));
        // Verificção
        Assertions.assertThat(exception).isInstanceOf(ErroAutentificacao.class).hasMessage("Usuário não encontrado para o email informado.");
    }


    // Quando usamos o catchThrowable não precisamos do (expected)
    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        //Cenário, criando um usuário
        UsuarioModel usuarioCriado = UsuarioModel.builder().email("teste@gmail.com").senha("senha").id(1L).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuarioCriado));

        //Ação, (Mostrando como esperar uma exeção)COMO SERIA +- NA VERSÃO MAIS RECENTE
        Throwable exception = Assertions.catchThrowable( () -> service.autenticar("teste@gmail.com", "senhaErrada"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutentificacao.class).hasMessage("Senha Inválida.");
    }


    @Test(expected = Test.None.class)
    public void deveValidarEmail(){
        // cenario
        // Simulando quando chamar o metodo existByEmail passando qualquer String como parametro ele vai retornar falso
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //Ação
        service.validarEmail("teste@gmail.com");
    }


    @Test(expected = RegraNegocioExecption.class)
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        // Ação
        service.validarEmail("teste@gmail.com");
    }

}
