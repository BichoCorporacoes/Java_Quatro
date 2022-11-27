package com.autobots.automanager.controles;

import com.autobots.automanager.componentes.EmpresaSelecionadora;
import com.autobots.automanager.componentes.VendaSelecionador;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.servicos.EmpresaServico;
import com.autobots.automanager.servicos.UsuarioServico;
import com.autobots.automanager.servicos.VeiculoServico;
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
@RequestMapping("/venda")
public class VendaControle {

  @Autowired
  private VendaServico servico;

  @Autowired
  private VendaSelecionador selecionador;

  @Autowired
  private UsuarioServico servicoUsuario;

  @Autowired
  private VeiculoServico servicoVeiculo;

  @Autowired
  private EmpresaServico servicoEmpresa;

  @Autowired
  private EmpresaSelecionadora selecionadorEmpresa;

  @GetMapping("/vendas")
  public ResponseEntity<List<Venda>> pegarTodos() {
    List<Venda> todos = servico.pegarTodos();
    HttpStatus status = HttpStatus.CONFLICT;
    if (todos.isEmpty()) {
      status = HttpStatus.NOT_FOUND;
      return new ResponseEntity<List<Venda>>(status);
    } else {
      status = HttpStatus.FOUND;
      ResponseEntity<List<Venda>> resposta = new ResponseEntity<List<Venda>>(
        todos,
        status
      );
      return resposta;
    }
  }

  @GetMapping("/vendas/{id}")
  public ResponseEntity<Venda> pegarUsuarioEspecifico(@PathVariable Long id) {
    List<Venda> todasEmpresas = servico.pegarTodos();
    Venda select = selecionador.selecionar(todasEmpresas, id);
    if (select == null) {
      return new ResponseEntity<Venda>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Venda>(select, HttpStatus.FOUND);
    }
  }

  @PutMapping("/atualizar/{id}")
  public ResponseEntity<?> atualizarUsuario(
    @PathVariable Long id,
    @RequestBody Venda atualizador
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<Venda> vendas = servico.pegarTodos();
    Venda venda = selecionador.selecionar(vendas, id);
    if (venda != null) {
      atualizador.setId(id);
      servico.update(atualizador);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(status);
  }

  @PostMapping("/cadastro/{idEmpresa}")
  public ResponseEntity<?> cadastroVenda(
    @RequestBody Venda vendas,
    @PathVariable Long idEmpresa
  ) {
    List<Empresa> selecionarEmpresa = servicoEmpresa.pegarTodas();
    Empresa selecionada = selecionadorEmpresa.selecionar(
      selecionarEmpresa,
      idEmpresa
    );
    if (selecionada != null) {
      Usuario clienteSelecionado = servicoUsuario.pegarPeloId(
        vendas.getCliente().getId()
      );
      Usuario funcionarioSelecionado = servicoUsuario.pegarPeloId(
        vendas.getFuncionario().getId()
      );
      Veiculo veiculoSelecionador = servicoVeiculo.pegarPeloId(
        vendas.getVeiculo().getId()
      );
      for (Mercadoria bodyMercadoria : vendas.getMercadorias()) {
        vendas.getMercadorias().clear();
        Mercadoria novaMercadoria = new Mercadoria();
        novaMercadoria.setDescricao(bodyMercadoria.getDescricao());
        novaMercadoria.setCadastro(bodyMercadoria.getCadastro());
        novaMercadoria.setFabricao(bodyMercadoria.getFabricao());
        novaMercadoria.setNome(bodyMercadoria.getNome());
        novaMercadoria.setQuantidade(bodyMercadoria.getQuantidade());
        novaMercadoria.setValidade(bodyMercadoria.getValidade());
        novaMercadoria.setValor(bodyMercadoria.getValor());
        vendas.getMercadorias().add(novaMercadoria);
      }
      for (Servico bodyServico : vendas.getServicos()) {
        Servico novoServico = new Servico();
        novoServico.setDescricao(bodyServico.getDescricao());
        novoServico.setNome(bodyServico.getNome());
        novoServico.setValor(bodyServico.getValor());
        vendas.getServicos().add(novoServico);
      }
      funcionarioSelecionado.getVendas().add(vendas);
      vendas.setCliente(clienteSelecionado);
      vendas.setFuncionario(funcionarioSelecionado);
      vendas.setVeiculo(veiculoSelecionador);
      selecionada.getVendas().add(vendas);
      servicoEmpresa.Salvar(selecionada);
      return new ResponseEntity<>(
        "Serviço cadastrado na empresa: " + selecionada.getNomeFantasia(),
        HttpStatus.CREATED
      );
    } else {
      return new ResponseEntity<>(
        "Empresa não encontrada",
        HttpStatus.NOT_FOUND
      );
    }
  }

  @DeleteMapping("/deletar/{id}")
  public ResponseEntity<?> deletarVendas(@PathVariable Long id) {
    List<Empresa> empresas = servicoEmpresa.pegarTodas();
    List<Veiculo> veiculos = servicoVeiculo.pegarTodos();
    List<Usuario> usuarios = servicoUsuario.pegarTodos();
    for (Empresa mercadoriaEmpresa : empresas) {
      for (Venda empresaMercadoria : mercadoriaEmpresa.getVendas()) {
        if (empresaMercadoria.getId() == id) {
          servicoEmpresa.deletarVendas(mercadoriaEmpresa.getId(), id);
        }
      }
    }
    for (Veiculo mercadoriaEmpresa : veiculos) {
      for (Venda empresaMercadoria : mercadoriaEmpresa.getVendas()) {
        if (empresaMercadoria.getId() == id) {
        	empresaMercadoria.setVeiculo(null);
          servicoVeiculo.deletarVendas(mercadoriaEmpresa.getId(), id);
        }
      }
    }
    for (Usuario mercadoriaEmpresa : usuarios) {
      for (Venda empresaMercadoria : mercadoriaEmpresa.getVendas()) {
        if (empresaMercadoria.getId() == id) {
          servicoUsuario.deletarVendas(mercadoriaEmpresa.getId(), id);
        }
      }
    }
    servico.deletar(id);
    return null;
  }
}
