package com.autobots.automanager.controles;

import com.autobots.automanager.componentes.EmpresaSelecionadora;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.servicos.EmpresaServico;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {

  @Autowired
  private EmpresaServico empresaServico;

  @Autowired
  private EmpresaSelecionadora selecionador;

  @GetMapping("/empresas")
  public ResponseEntity<List<Empresa>> pegarTodos() {
    List<Empresa> todos = empresaServico.pegarTodas();
    HttpStatus status = HttpStatus.CONFLICT;
    if (todos.isEmpty()) {
      status = HttpStatus.NOT_FOUND;
      return new ResponseEntity<List<Empresa>>(status);
    } else {
      status = HttpStatus.FOUND;
      ResponseEntity<List<Empresa>> resposta = new ResponseEntity<List<Empresa>>(
        todos,
        status
      );
      return resposta;
    }
  }

  @GetMapping("/empresas/{id}")
  public ResponseEntity<Empresa> pegarUsuarioEspecifico(@PathVariable Long id) {
    List<Empresa> todasEmpresas = empresaServico.pegarTodas();
    Empresa select = selecionador.selecionar(todasEmpresas, id);
    if (select == null) {
      return new ResponseEntity<Empresa>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Empresa>(select, HttpStatus.FOUND);
    }
  }

  @PostMapping("/cadastro")
  public ResponseEntity<?> cadastrarEmpresa(@RequestBody Empresa body) {
    body.setCadastro(new Date());
    empresaServico.Salvar(body);
    return new ResponseEntity<>(
      "Empresa: " + body.getNomeFantasia() + " cadastrada com sucesso",
      HttpStatus.CREATED
    );
  }

  @DeleteMapping("/deletar/{idEmpresa}")
  public void deletarEmpresa(@PathVariable Long idEmpresa) {
    empresaServico.deletar(idEmpresa);
  }

  @PutMapping("/atualizar/{id}")
  public ResponseEntity<?> atualizarUsuario(
    @PathVariable Long id,
    @RequestBody Empresa atualizador
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<Empresa> usuarios = empresaServico.pegarTodas();
    Empresa usuario = selecionador.selecionar(usuarios, id);
    atualizador.setId(id);
    if (usuario != null) {
    	empresaServico.update(atualizador);
    	empresaServico.Salvar(usuario);
      status = HttpStatus.OK;
      return new ResponseEntity<>("Atualizado com suscesso", status);
    }else {
        status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>("Empresa não encontrada", status);
    }
  }
}
