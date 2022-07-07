package com.aroque.minhasfinancas.repositories;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import com.aroque.minhasfinancas.model.LancamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<LancamentoModel, Long> {

    @Query(value = " SELECT sum(l.valor) FROM LancamentoModel l JOIN l.usuario u "
                 + " WHERE u.id = :idUsuario and l.tipo = :tipo and l.status = :status GROUP BY u ")
    BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
            @Param("idUsuario") Long idUsuario,
            @Param("tipo") TipoLancamentoEnum tipo,
            @Param("status")StatusLancamentoEnum status
            );

}
