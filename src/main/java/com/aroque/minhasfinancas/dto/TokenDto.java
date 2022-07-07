package com.aroque.minhasfinancas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {

    private String nome;
    private String token;
}
