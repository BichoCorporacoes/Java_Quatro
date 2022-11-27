package com.autobots.automanager.componentes;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entitades.Empresa;

@Component
public class EmpresaSelecionadora implements Selecionador<Empresa, Long>{
	
	@Override
	public Empresa selecionar(List<Empresa> empresas, Long id) {
		Empresa selecionado = null;
		for(Empresa selecionador: empresas) {
			if(selecionador.getId().longValue() == id.longValue()) {
				selecionado = selecionador;
				break;
			}
		}
		return selecionado;
	}

}
