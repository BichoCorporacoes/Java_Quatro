package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.componentes.UsuariosSelecionador;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioDocumento;
import com.autobots.automanager.repositorios.RepositorioEmail;
import com.autobots.automanager.repositorios.RepositorioTelefone;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UsuarioServico {

	@Autowired
	private RepositorioUsuario repositorio;
	
	@Autowired
	private RepositorioEmail repositorioEmail;
	
	@Autowired
	private RepositorioDocumento repositorioDocumento;
	
	@Autowired
	private RepositorioCredencialUsuarioSenha repositorioCredencialUsuarioSenha;
	
	@Autowired
	private RepositorioTelefone repositorioTelefone;
	
	@Autowired
	private MercadoriaServico servicoMercadoria;
	
	@Autowired
	private VendaServico servicoVenda;
	
	@Autowired
	private VeiculoServico servicoVeiculo;
	
	public List<Usuario> pegarTodos() {
		List<Usuario> pegarTodos = repositorio.findAll();
		return pegarTodos;
	}
	
	public Usuario pegarPeloId(Long id) {
		Usuario achar = repositorio.getById(id);
		return achar;
	}
	
	public void salvarUsuario(Usuario usuario) {
		repositorio.save(usuario);
	}
	
	public Usuario update(Usuario obj) {
		Usuario newObj = pegarPeloId(obj.getId());
		updateData(newObj, obj);
		for(Documento docs: obj.getDocumentos()) {
			docs.setId(docs.getId());
			updateDocumento(docs);
		}
		for(Email emails: obj.getEmails()) {
			emails.setId(emails.getId());
			updateDocumento(emails);
		}
		for(Telefone telefons : obj.getTelefones()) {
			telefons.setId(telefons.getId());
			updateTelefone(telefons);
		}
		return repositorio.save(newObj);
	}
	
	private void updateData(Usuario newObj, Usuario obj) {
		newObj.setNome(obj.getNome());
		newObj.setNomeSocial(obj.getNomeSocial());
		newObj.getEndereco().setBairro(obj.getEndereco().getBairro());
		newObj.getEndereco().setNumero(obj.getEndereco().getNumero());
		newObj.getEndereco().setRua(obj.getEndereco().getRua());
		newObj.getEndereco().setCidade(obj.getEndereco().getCidade());
		newObj.getEndereco().setCodigoPostal(obj.getEndereco().getCodigoPostal());
		newObj.getEndereco().setInformacoesAdicionais(obj.getEndereco().getInformacoesAdicionais());
		newObj.getEndereco().setEstado(obj.getEndereco().getEstado());
	}
	
	public void deletar(Long obj) {
		repositorio.deleteById(obj);
	}
	
	// Emails
	
	public List<Email> pegarEmails(){
		List<Email> emails = repositorioEmail.findAll();
		return emails;
	}
	
	public Email pegarPeloIdEmail(Long id) {
		Email achar = repositorioEmail.getById(id);
		return achar;
	}
	
	public void salvarEmail(Email email) {
		repositorioEmail.save(email);
	}
	
	
	public Email updateDocumento(Email obj) {
		Email newObj = pegarPeloIdEmail(obj.getId());
		updateDataEmail(newObj, obj);
		return repositorioEmail.save(newObj);
	}
	
	private void updateDataEmail(Email newObj, Email obj) {
		newObj.setEndereco(obj.getEndereco());
	}
	
	// Telefone
	
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
	
	// Documentos
	
	public List<Documento> pegarDocumentos(){
		List<Documento> documentos = repositorioDocumento.findAll();
		return documentos;
	}
	
	public Documento pegarPeloIdDoc(Long id) {
		Documento achar = repositorioDocumento.getById(id);
		return achar;
	}
	
	public void salvarDocumento(Documento documento) {
		repositorioDocumento.save(documento);
	}
	
	
	public Documento updateDocumento(Documento obj) {
		Documento newObj = pegarPeloIdDoc(obj.getId());
		updateDataDocumento(newObj, obj);
		return repositorioDocumento.save(newObj);
	}
	
	private void updateDataDocumento(Documento newObj, Documento obj) {
		newObj.setNumero(obj.getNumero());
		newObj.setDataEmissao(new Date());
	}
	
	// Usuario Senha
	
	public List<CredencialUsuarioSenha> credencial() {
		return repositorioCredencialUsuarioSenha.findAll();
	}
	
	// Mercadoria
	public void deletarMercadoria(Long idCliente, Long idMercadoria) {
		List<Usuario> todos = pegarTodos();
		UsuariosSelecionador select = new UsuariosSelecionador();
		Usuario selecionado = select.selecionar(todos, idCliente);
		Mercadoria mercadoria = servicoMercadoria.pegarPeloId(idMercadoria); 
		if(selecionado.getId() == idCliente) {
			selecionado.getMercadorias().remove(mercadoria);
		}
	}
	public void deletarVendas(Long idCliente, Long idMercadoria) {
		List<Usuario> todos = pegarTodos();
		UsuariosSelecionador select = new UsuariosSelecionador();
		Usuario selecionado = select.selecionar(todos, idCliente);
		Venda mercadoria = servicoVenda.pegarPeloId(idMercadoria); 
		if(selecionado.getId() == idCliente) {
			selecionado.getVendas().remove(mercadoria);
		}
	}
	public void deletarVeiculos(Long idCliente, Long idMercadoria) {
		List<Usuario> todos = pegarTodos();
		UsuariosSelecionador select = new UsuariosSelecionador();
		Usuario selecionado = select.selecionar(todos, idCliente);
		Veiculo mercadoria = servicoVeiculo.pegarPeloId(idMercadoria); 
		if(selecionado.getId() == idCliente) {
			selecionado.getVeiculos().remove(mercadoria);
		}
	}
	
	
}
