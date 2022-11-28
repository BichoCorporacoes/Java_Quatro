package com.autobots.automanager.hateos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entitades.Usuario;



@Component
public class UsuarioHateos implements Hateoas<Usuario> {

	@Override
	public void adicionarLink(List<Usuario> lista) {
		for (Usuario cliente : lista) {
			long id = cliente.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(UsuarioControle.class)
							.pegarUsuarioEspecifico(id))
					.withSelfRel();
			cliente.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(Usuario objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(UsuarioControle.class)
						.pegarTodos())
				.withRel("Clientes");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(UsuarioControle.class)
						.atualizarUsuario(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(UsuarioControle.class)
						.DeletarUser(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
	}

}
