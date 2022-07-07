package com.aroque.minhasfinancas.model;

import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@Table(name = "lancamento", schema = "financas")
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "ano")
    private Integer ano;


    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "data_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;

    @Column(name = "tipo")
    @Enumerated(value = EnumType.STRING)
    private TipoLancamentoEnum tipo;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private StatusLancamentoEnum status;



}