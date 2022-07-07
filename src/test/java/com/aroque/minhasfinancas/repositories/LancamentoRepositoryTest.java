package com.aroque.minhasfinancas.repositories;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import com.aroque.minhasfinancas.model.LancamentoModel;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;


    // Para não ter que ficar criando um lançamento na mão toda hora
    private LancamentoModel criarLancamento(){
        return LancamentoModel.builder().ano(2022).mes(1).descricao("Lançamento qualquer")
                .valor(BigDecimal.valueOf(10)).tipo(TipoLancamentoEnum.RECEITA)
                .status(StatusLancamentoEnum.PENDENTE).dataCadastro(LocalDate.now())
                .build();
    }



    @Test
    public void deveSalvarUmLancamento(){
        // Cénario
        LancamentoModel lancamento = criarLancamento();
        // Ação
        repository.save(lancamento);

        // Se tiver Id quer dizer que foi salvo na base de dados
        // Verficação
        Assertions.assertThat(lancamento.getId()).isNotNull();
    }


    @Test
    public void deveDeletarUmLancamento(){
        // Cénario
        LancamentoModel lancamento = criarLancamento();
        entityManager.persist(lancamento);

        // Conferindo/procurando se foi salvo na base de dados
        lancamento = entityManager.find(LancamentoModel.class, lancamento.getId());

        //Ação
        repository.delete(lancamento);
        LancamentoModel lancamentoInexistente = entityManager.find(LancamentoModel.class, lancamento.getId());

        //Verificação
        Assertions.assertThat(lancamentoInexistente).isNull();
    }


    @Test
    public void deveAtualizarUmLancamento(){
        // Cánario
        // Criando um lancamento
        LancamentoModel lancamento = criarLancamento();
        entityManager.persist(lancamento);
        // Editando/Atualizando o lancamento
        lancamento.setAno(2023);
        lancamento.setDescricao("Teste atualizado");
        lancamento.setStatus(StatusLancamentoEnum.CANCELADO);
        repository.save(lancamento);

        //Ação
        LancamentoModel lancamentoAtualizado = entityManager.find(LancamentoModel.class, lancamento.getId());

        //Verificação
        Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2023);
        Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste atualizado");
        Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamentoEnum.CANCELADO);
    }


    @Test
    public void deveBuscarUmLancamentoPorId(){
        // Cénario
        LancamentoModel lancamento = criarLancamento();
        entityManager.persist(lancamento);

        //Ação
        // Optional pois pode encontrar ou não
        Optional<LancamentoModel> lancamentoEncontrado = repository.findById(lancamento.getId());

        // Verificação
        Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }
}
