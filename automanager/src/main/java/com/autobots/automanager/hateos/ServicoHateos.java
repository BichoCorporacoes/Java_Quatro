package com.autobots.automanager.hateos;

import java.util.List;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.ServicoControle;
import com.autobots.automanager.entitades.Servico;

@Component
public class ServicoHateos implements Hateoas<Servico> {

	@Override
	public void adicionarLink(List<Servico> lista) {
		for (Servico entidade: lista) {
			long id = entidade.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(ServicoControle.class)
							.pegarUsuarioEspecifico(id))
					.withSelfRel();
			entidade.add(linkProprio);
		}
		
	}

	@Override
	public void adicionarLink(Servico objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(ServicoControle.class)
						.pegarTodos())
				.withRel("Serviços");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(ServicoControle.class)
						.atualizarUsuario(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(ServicoControle.class)
						.deletarServico(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
		
	}

}
