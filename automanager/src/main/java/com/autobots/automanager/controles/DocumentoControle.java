package com.autobots.automanager.controles;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.componentes.UsuariosSelecionador;
import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.servicos.UsuarioServico;


@RestController
@RequestMapping("/usuario")
public class DocumentoControle {

	@Autowired
	private UsuarioServico usuarioServico;
	@Autowired
	private UsuariosSelecionador selecionador;
	
	@GetMapping("/docs/{id}")
	public ResponseEntity<Set<Documento>> pegarDocumentos(@PathVariable Long id){
		List<Usuario> pegarTodosUsuarios = usuarioServico.pegarTodos();
		Usuario selecionado =  selecionador.selecionar(pegarTodosUsuarios, id);
		if(selecionado != null) {
			Set<Documento> docsUsuarios = selecionado.getDocumentos();
			return new ResponseEntity<>(docsUsuarios,HttpStatus.FOUND);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@PutMapping("/docs/{id}/atualizar/{idDoc}")
	public ResponseEntity<?> atualizarDocumento(
			@PathVariable Long id,
			@PathVariable Long idDoc,
			@RequestBody Documento atualizar){
		List<Usuario> pegarTodosUsuarios = usuarioServico.pegarTodos();
		Usuario selecionado =  selecionador.selecionar(pegarTodosUsuarios, id);
		if(selecionado != null) {
			atualizar.setId(idDoc);
			usuarioServico.updateDocumento(atualizar);
			return new ResponseEntity<>("Atualizado com sucesso",HttpStatus.FOUND);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
