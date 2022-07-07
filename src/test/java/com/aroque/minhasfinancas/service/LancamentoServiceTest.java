package com.aroque.minhasfinancas.service;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.LancamentoModel;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.repositories.LancamentoRepository;
import com.aroque.minhasfinancas.repositories.LancamentoRepositoryTest;
import com.aroque.minhasfinancas.service.Impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

    // Da classe que estamos testando, pois precisamos dos metodos reais
    @SpyBean
    LancamentoServiceImpl service;

    // Simular o comportamento do repository
    @MockBean
    LancamentoRepository repository;

    private LancamentoModel criarLancamento(){
        return LancamentoModel.builder().ano(2022).mes(1).descricao("Lançamento qualquer")
                .valor(BigDecimal.valueOf(10)).tipo(TipoLancamentoEnum.RECEITA)
                .status(StatusLancamentoEnum.PENDENTE).dataCadastro(LocalDate.now())
                .build();
    }


    @Test
    public void deveSalvarUmLancamento(){
        // Cénario
        LancamentoModel lancamentoASalvar = criarLancamento();
        // Fazendo isso não vai lançar erro quando chamar o metodo de validar
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        // Simulando como seria um lançamento salvo para comparar no final
        LancamentoModel lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamentoEnum.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        // Ação
        LancamentoModel lancamento = service.salvar(lancamentoASalvar);

        // Verificação
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamentoEnum.PENDENTE);
    }


    @Test
    public void naoDeveSalvarLancamentoQuandoHouverErroDeValidacao(){
        //Cénario
        LancamentoModel lancamentoASalvar = criarLancamento();
        // Via lançar uma regra de negocio exeption, quando o service chamar o validar
        Mockito.doThrow(RegraNegocioExecption.class).when(service).validar(lancamentoASalvar);

        // Verificação
        Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioExecption.class);
        // Verificando que o repository nunca chamou o metodo save, pois com o erro de validação acima, não poderia salvar.
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }


    @Test
    public void deveAtualizarUmLancamento(){
        // Cénario
        LancamentoModel lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamentoEnum.PENDENTE);
        // Fazendo isso não vai lançar erro quando chamar o metodo de validar
        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        // Ação
        service.atualizar(lancamentoSalvo);

        // Verificação
        // Verificando se o repository chamou UMA vez o metodo salvar
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }


    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo(){
        //Cénario
        LancamentoModel lancamentoASalvar = criarLancamento();

        // Verificação
        Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
        // Verificando que o repository nunca chamou o metodo save, pois com o erro em atualizar acima, não poderia salvar.
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }


    @Test
    public void deveDeletarUmLancamento(){
        // Cénario
        LancamentoModel lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);

        // Ação
        service.deletar(lancamentoSalvo);

        // Verficação
        Mockito.verify(repository).delete(lancamentoSalvo);
    }


    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo(){
        // Cénario
        // Não é um lanacamento salvo pois não tem um Id
        LancamentoModel lancamento = criarLancamento();

        // Ação
        Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        // Verficação
        // Verficando que nunca chamou o metodo delete pois o lancamento acima não existia na base dedados
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }


    @Test
    public void deveFiltrarLancamentos(){
        // Cénario
        LancamentoModel lancamento =  criarLancamento();
        lancamento.setId(1L);

        List<LancamentoModel> lista = Arrays.asList(lancamento);
        // Chamando o repository findAll passando qualquer filtro (MOCKITO.ANY(EXAMPLE.CLASS))
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        //Ação
        List<LancamentoModel> resultado = service.buscar(lancamento);

        //Verificação
        Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
    }


    @Test
    public void deveAtualizarOStatusDeUmLancamento(){
        // Cénario
        LancamentoModel lancamento =  criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamentoEnum.PENDENTE);

        StatusLancamentoEnum novoStatus = StatusLancamentoEnum.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        // Ação
        // Esse metodo atualizarStatus foi defino que precisa de dois parametros
        service.atualizarStatus(lancamento, novoStatus);

        // Verficação
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }


    @Test
    public void deveObterUmLancamentoPorId(){
        // Cénario
        Long id = 1L;

        LancamentoModel lancamento = criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        //Ação
        Optional<LancamentoModel> resultado = service.obterPorId(id);

        //Verficação
        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste(){
        // Cénario
        Long id = 1L;

        LancamentoModel lancamento = criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //Ação
        Optional<LancamentoModel> resultado = service.obterPorId(id);

        //Verficação
        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento(){
        LancamentoModel lancamento = new LancamentoModel();

        // Testando erro de Descrição
        Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe uma Descrição válida.");
        // Para testar a proxima validação temos q setar o que faltava acima
        lancamento.setDescricao("");

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("Sálario");

        // Testando erro de Mês //OBS-> ESTOU REUTILIZANDO A VARIAVEL ERRO
        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(0);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(15);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(2);

        // Testando erro de Ano
        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(201);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(2022);

        // Testando erro de Usuário
        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Usuário.");

        lancamento.setUsuario(new UsuarioModel());

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Usuário.");

        lancamento.getUsuario().setId(1L);

        // Testando erro de Valor
        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        // Testando erro de Tipo de Lancamento
        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioExecption.class).hasMessage("Informe um Tipo de lançamento.");

    }



}
