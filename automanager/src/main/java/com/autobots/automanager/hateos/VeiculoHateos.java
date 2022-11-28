package com.autobots.automanager.hateos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.VeiculoControle;
import com.autobots.automanager.entitades.Veiculo;

@Component
public class VeiculoHateos implements Hateoas<Veiculo>{

	@Override
	public void adicionarLink(List<Veiculo> lista) {
		for (Veiculo entidade: lista) {
			long id = entidade.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(VeiculoControle.class)
							.pegarVeiculoEspecifico(id))
					.withSelfRel();
			entidade.add(linkProprio);
		}
		
	}

	@Override
	public void adicionarLink(Veiculo objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VeiculoControle.class)
						.pegarTodos())
				.withRel("Veiculos");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VeiculoControle.class)
						.atualizarVeiculo(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VeiculoControle.class)
						.deletarVeiculo(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
		
	}

}
