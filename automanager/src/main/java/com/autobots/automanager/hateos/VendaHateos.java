package com.autobots.automanager.hateos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.VendaControle;
import com.autobots.automanager.entitades.Venda;

@Component
public class VendaHateos implements Hateoas<Venda>{

	@Override
	public void adicionarLink(List<Venda> lista) {
		for (Venda entidade: lista) {
			long id = entidade.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(VendaControle.class)
							.pegarUsuarioEspecifico(id))
					.withSelfRel();
			entidade.add(linkProprio);
		}
		
	}

	@Override
	public void adicionarLink(Venda objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VendaControle.class)
						.pegarTodos())
				.withRel("Vendas");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VendaControle.class)
						.atualizarUsuario(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VendaControle.class)
						.deletarVendas(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
		
	}

}
