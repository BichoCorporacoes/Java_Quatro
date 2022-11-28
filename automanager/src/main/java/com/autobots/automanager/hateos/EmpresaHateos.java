package com.autobots.automanager.hateos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.entitades.Empresa;

@Component
public class EmpresaHateos  implements Hateoas<Empresa>{

	@Override
	public void adicionarLink(List<Empresa> lista) {
		for (Empresa entidade: lista) {
			long id = entidade.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(EmpresaControle.class)
							.pegarEmpresaEspecifica(id))
					.withSelfRel();
			entidade.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(Empresa objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(EmpresaControle.class)
						.pegarTodos())
				.withRel("Empresas");
		objeto.add(linkProprio);
		Link atualizarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(EmpresaControle.class)
						.atualizarUsuario(objeto.getId(), objeto))
				.withRel("Atualizar");
		objeto.add(atualizarUser);
		Link deletarUser = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(EmpresaControle.class)
						.deletarEmpresa(objeto.getId()))
				.withRel("Deletar");
		objeto.add(deletarUser);
		
	}

}
