package com.aroque.minhasfinancas.exception;

public class ErroAutentificacao extends RuntimeException{

    public ErroAutentificacao(String mensagem){
        super(mensagem);
    }
}
