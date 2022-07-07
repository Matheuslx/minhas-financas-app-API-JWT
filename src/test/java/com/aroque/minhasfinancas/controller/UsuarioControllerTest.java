package com.aroque.minhasfinancas.controller;

import com.aroque.minhasfinancas.dto.UsuarioDto;
import com.aroque.minhasfinancas.exception.ErroAutentificacao;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.service.LancamentoService;
import com.aroque.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;


    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        // Cénario
        String email = "teste@gmail.com";
        String senha = "123";

        UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();
        UsuarioModel usuario = UsuarioModel.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
        // Passando o DTO para Json
        String json = new ObjectMapper().writeValueAsString(dto);

        // Execução e verificação
        // Esse Mock serve para criar uma requisição
         MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                                .post(API.concat("/autenticar"))
                                                                .accept(MediaType.APPLICATION_JSON)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(json);

            mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                    .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

    }


    @Test
    public void deveRetornaBadRequestAoRetornarErroDeAutentificacao() throws Exception {
        // Cénario
        String email = "teste@gmail.com";
        String senha = "123";

        UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutentificacao.class);
        // Passando o DTO para Json
        String json = new ObjectMapper().writeValueAsString(dto);

        // Execução e verificação
        // Esse Mock serve para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void deveCriarUmNovoUsuario() throws Exception {
        // Cénario
        String email = "teste@gmai.com";
        String senha = "123";
        UsuarioDto dto = UsuarioDto.builder().email("teste@gmail.com").senha("123").build();
        UsuarioModel usuario = UsuarioModel.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(UsuarioModel.class))).thenReturn(usuario);
        // Passando o DTO para Json
        String json = new ObjectMapper().writeValueAsString(dto);

        // Execução e verificação
        // Esse Mock serve para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

    }

    @Test
    public void deveRetornaBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
        // Cénario
        String email = "teste@gmai.com";
        String senha = "123";
        UsuarioDto dto = UsuarioDto.builder().email("teste@gmail.com").senha("123").build();

        Mockito.when(service.salvarUsuario(Mockito.any(UsuarioModel.class))).thenThrow(RegraNegocioExecption.class);
        // Passando o DTO para Json
        String json = new ObjectMapper().writeValueAsString(dto);

        // Execução e verificação
        // Esse Mock serve para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


    }

}
