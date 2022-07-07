package com.aroque.minhasfinancas.service;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.model.LancamentoModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    LancamentoModel salvar(LancamentoModel lancamento);

    LancamentoModel atualizar(LancamentoModel lancamento);

    void deletar(LancamentoModel lancamento);

    List<LancamentoModel> buscar(LancamentoModel lancamentoFiltro);

    void atualizarStatus(LancamentoModel lancamento, StatusLancamentoEnum status);

    void validar(LancamentoModel lancamento);

    Optional<LancamentoModel> obterPorId(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);
}
