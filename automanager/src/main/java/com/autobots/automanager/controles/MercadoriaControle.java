package com.autobots.automanager.controles;

import com.autobots.automanager.componentes.MercadoriaSelecionador;
import com.autobots.automanager.componentes.UsuariosSelecionador;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.servicos.EmpresaServico;
import com.autobots.automanager.servicos.MercadoriaServico;
import com.autobots.automanager.servicos.UsuarioServico;
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
@RequestMapping("/mercadoria")
public class MercadoriaControle {

  @Autowired
  private MercadoriaServico servico;

  @Autowired
  private EmpresaServico servicoEmpresa;

  @Autowired
  private UsuarioServico servicoUsuario;

  @Autowired
  private VendaServico servicoVenda;
  

  @Autowired
  private MercadoriaSelecionador selecionadora;

  @Autowired
  private UsuariosSelecionador usuarioSelecionador;

  @Autowired
  private MercadoriaSelecionador selecionador;

  @GetMapping("/mercadorias")
  public ResponseEntity<List<Mercadoria>> pegarTodos() {
    List<Mercadoria> todos = servico.pegarTodos();
    HttpStatus status = HttpStatus.CONFLICT;
    if (todos.isEmpty()) {
      status = HttpStatus.NOT_FOUND;
      return new ResponseEntity<List<Mercadoria>>(status);
    } else {
      status = HttpStatus.FOUND;
      ResponseEntity<List<Mercadoria>> resposta = new ResponseEntity<List<Mercadoria>>(
        todos,
        status
      );
      return resposta;
    }
  }

  @GetMapping("/mercadorias/{id}")
  public ResponseEntity<Mercadoria> pegarMercadoriaEspecifica(
    @PathVariable Long id
  ) {
    List<Mercadoria> todasEmpresas = servico.pegarTodos();
    Mercadoria select = selecionador.selecionar(todasEmpresas, id);
    if (select == null) {
      return new ResponseEntity<Mercadoria>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Mercadoria>(select, HttpStatus.FOUND);
    }
  }

  @PutMapping("/atualizar/{id}")
  public ResponseEntity<?> atualizarMercadoria(
    @PathVariable Long id,
    @RequestBody Mercadoria atualizador
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<Mercadoria> usuarios = servico.pegarTodos();
    atualizador.setId(id);
    Mercadoria usuario = selecionador.selecionar(usuarios, id);
    if (usuario != null) {
      atualizador.setId(id);
      servico.update(atualizador);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(status);
  }

  @PostMapping("/cadastro/{idCliente}")
  public ResponseEntity<?> cadastroMercadorias(
    @RequestBody Mercadoria cadastro,
    @PathVariable Long idCliente
  ) {
    servico.salvar(cadastro);
    List<Usuario> getAllUsuarios = servicoUsuario.pegarTodos();
    Usuario select = usuarioSelecionador.selecionar(getAllUsuarios, idCliente);
    if (select == null) {
      return new ResponseEntity<>(
        "Usuario não encontrado",
        HttpStatus.NOT_FOUND
      );
    } else {
    	for(Empresa empresas : servicoEmpresa.pegarTodas()) {
    		for(Usuario usuarios : empresas.getUsuarios()) {
    			if(usuarios.getId().equals(select.getId())) {
    				empresas.getMercadorias().add(cadastro);
    			}
    		}
    	}
      select.getMercadorias().add(cadastro);
      servicoUsuario.salvarUsuario(select);
      return new ResponseEntity<>(
        "Mercadoria Cadastrada com sucesso",
        HttpStatus.CREATED
      );
    }
  }

  @DeleteMapping("/deletar/{idMercadoria}")
  public ResponseEntity<?> deletarMercadoria(@PathVariable Long idMercadoria) {
    List<Mercadoria> mercadorias = servico.pegarTodos();
    List<Usuario> usuarios = servicoUsuario.pegarTodos();
    List<Empresa> empresas = servicoEmpresa.pegarTodas();
    List<Venda> vendas = servicoVenda.pegarTodos();
    Mercadoria selecionado = selecionadora.selecionar(
      mercadorias,
      idMercadoria
    );
    if (selecionado == null) {
      return new ResponseEntity<>(
        "Não existe essa mercadoria, digite outro ID",
        HttpStatus.NOT_FOUND
      );
    } else {
      for (Usuario mercadoriaUsuario : usuarios) {
        for (Mercadoria userMercadoria : mercadoriaUsuario.getMercadorias()) {
          if (userMercadoria.getId() == idMercadoria) {
            servicoUsuario.deletarMercadoria(
              mercadoriaUsuario.getId(),
              idMercadoria
            );
          }
        }
      }
      for (Empresa mercadoriaEmpresa : empresas) {
        for (Mercadoria empresaMercadoria : mercadoriaEmpresa.getMercadorias()) {
          if (empresaMercadoria.getId() == idMercadoria) {
            servicoEmpresa.deletarMercadoria(
              mercadoriaEmpresa.getId(),
              idMercadoria
            );
          }
        }
      }
      for (Venda mercadoriaVenda : vendas) {
        for (Mercadoria vendaMercadoria : mercadoriaVenda.getMercadorias()) {
          if (vendaMercadoria.getId() == idMercadoria) {
            servicoVenda.deletarMercadoria(
              mercadoriaVenda.getId(),
              idMercadoria
            );
          }
        }
      }
      servico.delete(idMercadoria);
      return new ResponseEntity<>("Deletado com sucesso", HttpStatus.OK);
    }
  }
}
