package com.autobots.automanager.controles;

import com.autobots.automanager.componentes.EmpresaSelecionadora;
import com.autobots.automanager.componentes.ServicoSelecionador;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.servicos.EmpresaServico;
import com.autobots.automanager.servicos.ServicoServico;
import com.autobots.automanager.servicos.VendaServico;
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
@RequestMapping("/servico")
public class ServicoControle {

  @Autowired
  private ServicoServico servico;

  @Autowired
  private ServicoSelecionador selecionador;

  @Autowired
  private EmpresaSelecionadora empresaSelecionadora;

  @Autowired
  private EmpresaServico servicoEmpresa;

  @Autowired
  private VendaServico servicoVenda;

  @GetMapping("/servicos")
  public ResponseEntity<List<Servico>> pegarTodos() {
    List<Servico> todos = servico.pegarTodos();
    HttpStatus status = HttpStatus.CONFLICT;
    if (todos.isEmpty()) {
      status = HttpStatus.NOT_FOUND;
      return new ResponseEntity<List<Servico>>(status);
    } else {
      status = HttpStatus.FOUND;
      ResponseEntity<List<Servico>> resposta = new ResponseEntity<List<Servico>>(
        todos,
        status
      );
      return resposta;
    }
  }

  @GetMapping("/servicos/{id}")
  public ResponseEntity<Servico> pegarUsuarioEspecifico(@PathVariable Long id) {
    List<Servico> todasEmpresas = servico.pegarTodos();
    Servico select = selecionador.selecionar(todasEmpresas, id);
    if (select == null) {
      return new ResponseEntity<Servico>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Servico>(select, HttpStatus.FOUND);
    }
  }

  @PostMapping("/cadastro/{idEmpresa}")
  public ResponseEntity<?> cadastroServico(
    @PathVariable Long idEmpresa,
    @RequestBody Servico body
  ) {
    List<Empresa> todos = servicoEmpresa.pegarTodas();
    Empresa select = empresaSelecionadora.selecionar(todos, idEmpresa);
    if (select != null) {
      select.getServicos().add(body);
      servicoEmpresa.Salvar(select);
      return new ResponseEntity<>(
        "Serviço cadastrado na empresa" + select.getNomeFantasia(),
        HttpStatus.CREATED
      );
    } else {
      return new ResponseEntity<>(
        "Empresa não encontrado",
        HttpStatus.NOT_FOUND
      );
    }
  }

  @DeleteMapping("/deletar/{id}")
  public ResponseEntity<?> deletarServico(@PathVariable Long id) {
    List<Servico> lista = servico.pegarTodos();
    List<Venda> vendas = servicoVenda.pegarTodos();
    Servico select = selecionador.selecionar(lista, id);
    List<Empresa> empresas = servicoEmpresa.pegarTodas();
    if (select != null) {
      for (Empresa empresasServico : empresas) {
        for (Servico ServicoNaEmpresas : empresasServico.getServicos()) {
          if (ServicoNaEmpresas.getId().equals(select.getId())) {
            empresasServico.getServicos().remove(ServicoNaEmpresas);
          }
        }
      }
      for (Venda vendasServico : vendas) {
        for (Servico servicoNaVenda : vendasServico.getServicos()) {
          if (servicoNaVenda.getId().equals(select.getId())) {
            vendasServico.getServicos().remove(servicoNaVenda);
          }
        }
      }
      servico.deletar(id);
      return new ResponseEntity<>("Servico Deletado", HttpStatus.ACCEPTED);
    }
    return new ResponseEntity<>("Servico não encontrado", HttpStatus.NOT_FOUND);
  }

  @PutMapping("/atualizar/{id}")
  public ResponseEntity<?> atualizarUsuario(
    @PathVariable Long id,
    @RequestBody Servico atualizador
  ) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    List<Servico> usuarios = servico.pegarTodos();
    Servico usuario = selecionador.selecionar(usuarios, id);
    if (usuario != null) {
      atualizador.setId(id);
      servico.update(atualizador);
      status = HttpStatus.OK;
      return new ResponseEntity<>("Atualizado com sucesso", status);
    }
    return new ResponseEntity<>(status);
  }
}
