package com.aroque.minhasfinancas.controller;

import com.aroque.minhasfinancas.dto.AtualizaStatusDto;
import com.aroque.minhasfinancas.dto.LancamentoDto;
import com.aroque.minhasfinancas.enums.StatusLancamentoEnum;
import com.aroque.minhasfinancas.enums.TipoLancamentoEnum;
import com.aroque.minhasfinancas.exception.RegraNegocioExecption;
import com.aroque.minhasfinancas.model.LancamentoModel;
import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.service.LancamentoService;
import com.aroque.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    @Autowired
    LancamentoService service;

    @Autowired
    UsuarioService usuarioService;

    public LancamentoController(LancamentoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    private LancamentoModel converter(LancamentoDto dto){
        LancamentoModel lancamento = new LancamentoModel();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        lancamento.setDataCadastro(dto.getDataCadatro());

        UsuarioModel usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioExecption("Uusuário não encontrado para o Id informado"));

        lancamento.setUsuario(usuario);
        if(dto.getTipo() != null){
            lancamento.setTipo(TipoLancamentoEnum.valueOf(dto.getTipo()));
        }

        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamentoEnum.valueOf(dto.getStatus()));
        }

        return lancamento;
    }


    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDto dto){
        try{
            LancamentoModel entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioExecption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar (@PathVariable(value = "id") Long id, @RequestBody LancamentoDto dto){
        return service.obterPorId(id).map( entity -> {
            try{
                LancamentoModel lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return new ResponseEntity(lancamento, HttpStatus.OK);
            } catch (RegraNegocioExecption e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST) );
    }

    @PutMapping("/{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDto dto){
        return service.obterPorId(id).map( entity -> {
            StatusLancamentoEnum statusSelecionado = StatusLancamentoEnum.valueOf(dto.getStatus());
            if(statusSelecionado == null){
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status de lançamento, envie um status válido.");
            }
            try{
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioExecption e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } ).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST) );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable(value = "id") Long id){
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity("Deletado com sucesso",HttpStatus.NO_CONTENT);
        } ).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "tipo", required = false) TipoLancamentoEnum tipo,
            @RequestParam("usuario") Long idUsuario
        ){
        LancamentoModel lancamentoFiltro = new LancamentoModel();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setTipo(tipo);

        Optional<UsuarioModel> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<LancamentoModel> lancamentos = service.buscar(lancamentoFiltro);
        return new ResponseEntity(lancamentos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity obterLancamento(@PathVariable(value = "id") Long id){
        return service.obterPorId(id).map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
                .orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    private LancamentoDto converter(LancamentoModel lancamento){
        return LancamentoDto.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }



}
