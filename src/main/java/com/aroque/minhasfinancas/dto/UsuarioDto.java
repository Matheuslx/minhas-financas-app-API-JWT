package com.aroque.minhasfinancas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

    private String nome;
    private String email;
    private String senha;
    private LocalDate dataCadastro = LocalDate.now();

}
