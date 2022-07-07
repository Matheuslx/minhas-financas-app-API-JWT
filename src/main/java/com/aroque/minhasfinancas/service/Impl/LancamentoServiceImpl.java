package com.aroque.minhasfinancas.service.Impl;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.LancamentoModel;
import com.aroque.minhasfinancas.repositories.LancamentoRepository;
import com.aroque.minhasfinancas.service.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    @Autowired
    LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }



    @Override
    @Transactional
    public LancamentoModel salvar(LancamentoModel lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamentoEnum.PENDENTE);
        return repository.save(lancamento);
    }


    @Override
    @Transactional
    public LancamentoModel atualizar(LancamentoModel lancamento) {
        // Garantindo que vai passar um lancamento com ID
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }


    @Override
    @Transactional
    public void deletar(LancamentoModel lancamento) {
        // Garantindo que vai passar um lancamento com ID
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }


    @Override
    @Transactional(readOnly = true)
    public List<LancamentoModel> buscar(LancamentoModel lancamentoFiltro) {
        // Vai pegar as propriedades que estão populadas dentro de lancamento filtro
        // e vai fazer uma configuração para que quando chamarmios o metodo de buscar
        // ele leva em consideração somente as propriedades que foram preenchidas (ano, mes, descrição, etc)
        Example example = Example.of(lancamentoFiltro,
                // Aq serve para fazermos algumas configurações na busca
                // ignorar se a letra estar maiuscula ou minuscula e se contem determinada letra na palavra buscada
                ExampleMatcher.matching()
                              .withIgnoreCase()
                              .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }


    @Override
    public void atualizarStatus(LancamentoModel lancamento, StatusLancamentoEnum status) {
        lancamento.setStatus(status);
        atualizar(lancamento);
    }

    @Override
    public void validar(LancamentoModel lancamento) {

        // Se o campo de descrição for vazio ou tiver apenas espaços será lançada uma exeção
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioExecption("Informe uma Descrição válida.");
        }

        // Se o mês de lançamento for vazio ou mês inexistente lança uma exeção
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioExecption("Informe um Mês válido.");
        }

        // Se o ano de lançamento for vazio ou não tiver 4 digitos lança uma exeção
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioExecption("Informe um Ano válido.");
        }

        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioExecption("Informe um Usuário.");
        }

        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioExecption("Informe um Valor válido.");
        }

        if(lancamento.getTipo() == null){
            throw new RegraNegocioExecption("Informe um Tipo de lançamento.");
        }

    }

    @Override
    public Optional<LancamentoModel> obterPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {

        BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamentoEnum.RECEITA, StatusLancamentoEnum.EFETIVADO);
        BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamentoEnum.DESPESA, StatusLancamentoEnum.EFETIVADO);

        if(receitas == null){
            receitas = BigDecimal.ZERO;
        }
        if(despesas == null){
            despesas = BigDecimal.ZERO;
        }

        return receitas.subtract(despesas);
    }
}
