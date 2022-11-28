package com.autobots.automanager.hateos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.entitades.Mercadoria;

@Component
public class MercadoriaHateos implements Hateoas<Mercadoria>{

	@Override
	public void adicionarLink(List<Mercadoria> lista) {
		for(Mercadoria mercadoria : lista) {
			long id = mercadoria.getId();
			Link linkpropio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class)
							.pegarMercadoriaEspecifica(id))
					.withSelfRel();
			mercadoria.add(linkpropio);
		}
		
	}

	@Override
	public void adicionarLink(Mercadoria objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.pegarTodos())
				.withRel("Mercadorias");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.atualizarMercadoria(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.deletarMercadoria(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
		
	}

}
