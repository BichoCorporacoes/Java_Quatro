package com.autobots.automanager.componentes;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entitades.Venda;

@Component
public class VendaSelecionador implements Selecionador<Venda, Long> {
	  
		@Override
		public Venda selecionar(List<Venda> vendas, Long id) {
			Venda selecionado = null;
			for(Venda selecionador: vendas) {
				if(selecionador.getId().longValue() == id.longValue()) {
					selecionado = selecionador;
					break;
				}
			}
			return selecionado;
		}
}
