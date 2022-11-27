package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.componentes.VeiculoSelecionador;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.repositorios.RepositorioVeiculo;

@Service
public class VeiculoServico {

	@Autowired
	private RepositorioVeiculo repositorio;
	
	@Autowired
	private VeiculoSelecionador select;
	
	@Autowired
	private VendaServico servicoVenda;
	
	public List<Veiculo> pegarTodos(){
		List<Veiculo> pegarTodas = repositorio.findAll();
		return pegarTodas;
	}
	public void adicionar(Veiculo obj) {
		repositorio.save(obj);
	}
	
	public void deletar(Long obj) {
		repositorio.deleteById(obj);
	}
	
	public Veiculo pegarPeloId(Long id) {
		Veiculo achar = repositorio.getById(id);
		return achar;
	}
	
	public Veiculo update(Veiculo obj) {
		Veiculo newObj = pegarPeloId(obj.getId());
		updateData(newObj, obj);
		return repositorio.save(newObj);
	}
	
	private void updateData(Veiculo newObj, Veiculo obj) {
		newObj.setModelo(obj.getModelo());
		newObj.setPlaca(obj.getPlaca());
		newObj.setTipo(obj.getTipo());		
	}
	
	public void deletarVendas(Long idEmpresa, Long idMercadoria) {
		List<Veiculo> todos = pegarTodos();
		Veiculo selecionado = select.selecionar(todos, idMercadoria);
		Venda mercadoria = servicoVenda.pegarPeloId(idMercadoria); 
		if(selecionado.getId() == idEmpresa) {
			selecionado.getVendas().remove(mercadoria);
		}
	}
}
