package com.autobots.automanager.adaptadores;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Credencial;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UserDetailsServiceImpl {
	@Autowired
	private RepositorioUsuario repositorio;

	public Usuario selecionar(List<Usuario> objetos, String identificador) {
		Usuario usuario = null;
		for (Usuario objeto : objetos) {
			Set<Credencial> credencial = objeto.getCredenciais();
			String nomeUsuario = ((CredencialUsuarioSenha) credencial).getNomeUsuario();
			if (nomeUsuario.trim().equals(identificador.trim())) {
				usuario = objeto;
				break;
			}
		}
		return usuario;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Usuario> usuarios = repositorio.findAll();
		Usuario usuario = selecionar(usuarios, username);
		if (usuario == null) {
			throw new UsernameNotFoundException(username);
		}
		Set<Credencial> credencial = usuario.getCredenciais();
		return new UserDetailsImpl(((CredencialUsuarioSenha) credencial).getNomeUsuario(), ((CredencialUsuarioSenha) credencial).getSenha(), usuario.getNivelDeAcesso());
	}
}
