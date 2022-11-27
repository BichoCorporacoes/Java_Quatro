package com.autobots.automanager.servicos;

import com.autobots.automanager.componentes.EmpresaSelecionadora;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioEndereco;
import com.autobots.automanager.repositorios.RepositorioTelefone;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServico {

  @Autowired
  private RepositorioEmpresa repositorio;

  @Autowired
  private EmpresaSelecionadora select;

  @Autowired
  private RepositorioTelefone repositorioTelefone;

  @Autowired
  private MercadoriaServico servicoMercadoria;

  @Autowired
  private VendaServico servicoVenda;

  @Autowired
  private UsuarioServico servicoUsuario;

  @Autowired
  private VeiculoServico veiculoServicos;

  @Autowired
  private ServicoServico servicoServico;
  
  @Autowired
  private RepositorioEndereco repositorioEndereco;

  public List<Empresa> pegarTodas() {
    List<Empresa> pegarTodas = repositorio.findAll();
    return pegarTodas;
  }

  public void Salvar(Empresa salvar) {
    repositorio.save(salvar);
  }

  public Empresa pegarPeloId(Long id) {
    Empresa achar = repositorio.getById(id);
    return achar;
  }

  public Empresa update(Empresa obj) {
    Empresa newObj = pegarPeloId(obj.getId());
    updateData(newObj, obj);
	for(Telefone telefons : obj.getTelefones()) {
		telefons.setId(telefons.getId());
		updateTelefone(telefons);
	}
    return repositorio.save(newObj);
  }

  private void updateData(Empresa newObj, Empresa obj) {
    newObj.setNomeFantasia(obj.getNomeFantasia());
    newObj.setRazaoSocial(obj.getRazaoSocial());
	newObj.getEndereco().setBairro(obj.getEndereco().getBairro());
	newObj.getEndereco().setNumero(obj.getEndereco().getNumero());
	newObj.getEndereco().setRua(obj.getEndereco().getRua());
	newObj.getEndereco().setCidade(obj.getEndereco().getCidade());
	newObj.getEndereco().setCodigoPostal(obj.getEndereco().getCodigoPostal());
	newObj.getEndereco().setInformacoesAdicionais(obj.getEndereco().getInformacoesAdicionais());
	newObj.getEndereco().setEstado(obj.getEndereco().getEstado());
  }

  public void deletar(Long id) {
    List<Empresa> todas = pegarTodas();
    Empresa selecionada = select.selecionar(todas, id);
    if (selecionada != null) {
      for (Mercadoria mercadoriaEmpresa : selecionada.getMercadorias()) {
        servicoMercadoria.delete(mercadoriaEmpresa.getId());
      }
      for (Venda vendaEmpresa : selecionada.getVendas()) {
        vendaEmpresa.setFuncionario(null);
        vendaEmpresa.setCliente(null);
        vendaEmpresa.setVeiculo(null);
        Set<Servico> servicos = vendaEmpresa.getServicos();
        Set<Mercadoria> mercadorias = vendaEmpresa.getMercadorias();
        vendaEmpresa.getMercadorias().removeAll(mercadorias);
        vendaEmpresa.getServicos().removeAll(servicos);
        servicoVenda.deletar(vendaEmpresa.getId());
      }
      for (Servico servicoEmpresa : selecionada.getServicos()) {
        servicoServico.deletar(servicoEmpresa.getId());
      }
      for (Usuario usuarioEmpresa : selecionada.getUsuarios()) {
        for (Veiculo veiculoUser : usuarioEmpresa.getVeiculos()) {
          veiculoUser.getVendas().clear();
          Set<Venda> vendas = veiculoUser.getVendas();
          veiculoUser.setProprietario(null);
          veiculoUser.getVendas().removeAll(vendas);
          usuarioEmpresa.setVeiculos(null);
          veiculoServicos.deletar(veiculoUser.getId());
        }
        servicoUsuario.deletar(usuarioEmpresa.getId());
      }
      repositorio.deleteById(id);
    }
  }

  public void deletarMercadoria(Long idEmpresa, Long idMercadoria) {
    List<Empresa> todos = pegarTodas();
    Empresa selecionado = select.selecionar(todos, idMercadoria);
    Mercadoria mercadoria = servicoMercadoria.pegarPeloId(idMercadoria);
    if (selecionado.getId() == idEmpresa) {
      selecionado.getMercadorias().remove(mercadoria);
    }
  }

  public void deletarVendas(Long idEmpresa, Long idMercadoria) {
    List<Empresa> todos = pegarTodas();
    Empresa selecionado = select.selecionar(todos, idMercadoria);
    Venda venda = servicoVenda.pegarPeloId(idMercadoria);
    if (selecionado.getId() == idEmpresa) {
      selecionado.getVendas().remove(venda);
    }
  }

  public Telefone pegarTelfoneById(Long id) {
    Telefone achar = repositorioTelefone.getById(id);
    return achar;
  }

  public Telefone updateTelefone(Telefone obj) {
    Telefone newObj = pegarTelfoneById(obj.getId());
    updateTelefone(newObj, obj);
    return repositorioTelefone.save(newObj);
  }

  private void updateTelefone(Telefone newObj, Telefone obj) {
    newObj.setDdd(obj.getDdd());
    newObj.setNumero(obj.getNumero());
  }
  
  public Endereco pegarEndPorId(Long id) {
	  return repositorioEndereco.getById(id);
  }
  
  public Endereco updateEndereco(Endereco obj) {
	  Endereco newObj = pegarEndPorId(obj.getId());
	  updateEnderco(newObj, obj);
	    return repositorioEndereco.save(newObj);
  }
  
  private void updateEnderco(Endereco newObj, Endereco obj) {
	    newObj.setNumero(obj.getNumero());
	    newObj.setBairro(obj.getBairro());
	    newObj.setCidade(obj.getCidade());
	    newObj.setCodigoPostal(obj.getCodigoPostal());
	    newObj.setInformacoesAdicionais(obj.getInformacoesAdicionais());
	    newObj.setEstado(obj.getEstado());
	    newObj.setRua(obj.getRua());
  }
}
