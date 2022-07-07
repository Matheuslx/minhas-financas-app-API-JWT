package com.aroque.minhasfinancas.controller;

import com.aroque.minhasfinancas.dto.TokenDto;
import com.aroque.minhasfinancas.dto.UsuarioDto;
import com.aroque.minhasfinancas.exception.ErroAutentificacao;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.service.JwtService;
import com.aroque.minhasfinancas.service.LancamentoService;
import com.aroque.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @Autowired
    LancamentoService lancamentoService;

    @Autowired
    JwtService jwtService;

    public UsuarioController(UsuarioService service, LancamentoService lancamentoService, JwtService jwtService) {
        this.service = service;
        this.lancamentoService = lancamentoService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity salvar( @RequestBody UsuarioDto dto){
        UsuarioModel usuario = UsuarioModel.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
        usuario.setDataCadastro(dto.getDataCadastro());
        if(usuario.getNome() == null || usuario.getNome().trim().equals("")){
            return ResponseEntity.badRequest().body("Insira um nome.");
        }
        try{
            UsuarioModel usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        } catch(RegraNegocioExecption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDto dto){
        try {
            UsuarioModel usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());

            //Agora irá retornar o token do usuario autenticado e não mais o email e o id
            String token = jwtService.gerarToken(usuarioAutenticado);
            TokenDto tokenDto = new TokenDto( usuarioAutenticado.getNome(),token);

            return new ResponseEntity(tokenDto, HttpStatus.OK);
        } catch (ErroAutentificacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable(value = "id") Long id){
        Optional<UsuarioModel> usuario = service.obterPorId(id);
        if(!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
