package com.autobots.automanager.controles;

import com.autobots.automanager.componentes.EmpresaSelecionadora;
import com.autobots.automanager.componentes.UsuariosSelecionador;
import com.autobots.automanager.entitades.Credencial;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.hateos.UsuarioHateos;
import com.autobots.automanager.servicos.EmpresaServico;
import com.autobots.automanager.servicos.UsuarioServico;
import com.autobots.automanager.servicos.VeiculoServico;
import com.autobots.automanager.servicos.VendaServico;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {

  @Autowired
  private UsuarioServico usuarioServico;

  @Autowired
  private EmpresaServico servicoEmpresa;
  
  @Autowired
  private VendaServico vendaServico;
  
  @Autowired
  private UsuarioHateos hateoas;

  @Autowired
  private VeiculoServico veiculoServico;
  @Autowired
  private UsuariosSelecionador selecionador;
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_VENDEDOR')")
  @GetMapping("/usuarios")
  public ResponseEntity<List<Usuario>> pegarTodos() {
    List<Usuario> todos = usuarioServico.pegarTodos();
    HttpStatus status = HttpStatus.CONFLICT;
    if (todos.isEmpty()) {
      status = HttpStatus.NOT_FOUND;
      return new ResponseEntity<List<Usuario>>(status);
    } else {
      status = HttpStatus.FOUND;
      hateoas.adicionarLink(todos);
      ResponseEntity<List<Usuario>> resposta = new ResponseEntity<List<Usuario>>(
        todos,
        status
      );
      return resposta;
    }
  }
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_CLIENTE','ROLE_VENDEDOR')")
  @GetMapping("/usuarios/{id}")
  public ResponseEntity<?> pegarUsuarioEspecifico(@PathVariable Long id) {
    List<Usuario> todosUsuarios = usuarioServico.pegarTodos();
    Usuario select = selecionador.selecionar(todosUsuarios, id);
    if (select == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
    	hateoas.adicionarLink(select);
      return new ResponseEntity<>(select, HttpStatus.FOUND);
    }
  }
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_VENDEDOR')")
  @PutMapping("/atualizar/{id}")
  public ResponseEntity<?> atualizarUsuario(
    @PathVariable Long id,
    @RequestBody Usuario atualizador
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<Usuario> usuarios = usuarioServico.pegarTodos();
    atualizador.setId(id);
    Usuario usuario = selecionador.selecionar(usuarios, id);
    if (usuario != null) {
      usuarioServico.update(atualizador);
      usuarioServico.salvarUsuario(usuario);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(status);
  }
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_VENDEDOR')")
  @DeleteMapping("/deletar/{idCli}")
  public ResponseEntity<?> DeletarUser(@PathVariable Long idCli){
	  List<Usuario> usuarios = usuarioServico.pegarTodos();
	  Usuario user = selecionador.selecionar(usuarios, idCli);
	  if(user != null) {
		  Set<Documento> documentos = user.getDocumentos();
		  Set<Telefone> telefones = user.getTelefones();
		  Set<Email> emails = user.getEmails();
		  Set<Credencial> credenciais = user.getCredenciais();
		  Set<Mercadoria> mercadorias = user.getMercadorias();
		  Set<Venda> vendas = user.getVendas();
		  for(Venda vendaExistentes : vendaServico.pegarTodos()) {
			  if(vendaExistentes.getFuncionario().getId() == idCli) {
				  vendaExistentes.setFuncionario(null);
			  }
			  if(vendaExistentes.getCliente().getId() == idCli) {
				  vendaExistentes.setCliente(null);
			  }
		  }
		  Set<Veiculo> veiculos = user.getVeiculos();
		  for(Veiculo veiculosExistente : veiculoServico.pegarTodos()) {
			  if(veiculosExistente.getProprietario().getId().equals(idCli)) {
				  veiculosExistente.setProprietario(null);
			  }
		  }
		  user.getDocumentos().removeAll(documentos);
		  user.getTelefones().removeAll(telefones);
		  user.getEmails().removeAll(emails);
		  user.getCredenciais().removeAll(credenciais);
		  user.getMercadorias().removeAll(mercadorias);
		  user.getVeiculos().removeAll(veiculos);
		  user.getVendas().removeAll(vendas);
		  user.setEndereco(null);
		  user.setNivelDeAcesso(null);
		  usuarioServico.deletar(idCli);
		  return new ResponseEntity<>("Deletado com sucesso", HttpStatus.ACCEPTED);
	  }else {
		  return new ResponseEntity<>("Não encontrado", HttpStatus.NOT_FOUND);
	  }
  }
  
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_CLIENTE','ROLE_VENDEDOR')")
  @PutMapping("/registro-credencial/{idClinte}")
  public ResponseEntity<?> registroCredencial(
    @RequestBody CredencialUsuarioSenha registroDeCredencial,
    @PathVariable Long idClinte
  ) {
    List<Usuario> todosUsuarios = usuarioServico.pegarTodos();
    Usuario select = selecionador.selecionar(todosUsuarios, idClinte);
    List<CredencialUsuarioSenha> credenciais = usuarioServico.credencial();
	BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
    if (select == null) {
      return new ResponseEntity<>(
        "Usuario não encontrado",
        HttpStatus.NOT_FOUND
      );
    } else {
      boolean erroBoolean = false;
      for (CredencialUsuarioSenha bodyCredencial : credenciais) {
        if (
          registroDeCredencial
            .getNomeUsuario()
            .equals(bodyCredencial.getNomeUsuario())
        ) {
          erroBoolean = true;
        }
      }
      if (erroBoolean == true) {
        return new ResponseEntity<>(
          "Credencial já existe",
          HttpStatus.CONFLICT
        );
      } else {
    	String senha = codificador.encode(registroDeCredencial.getSenha());
    	registroDeCredencial.setSenha(senha);
        select.getCredenciais().add(registroDeCredencial);
        usuarioServico.salvarUsuario(select);
        return new ResponseEntity<>(
          "Credencial cadastrada",
          HttpStatus.CREATED
        );
      }
    }
  }
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GERENTE','ROLE_VENDEDOR')")
  @PostMapping("/cadastro/{idEmpresa}")
  public ResponseEntity<?> cadastrarUsuario(
    @PathVariable Long idEmpresa,
    @RequestBody Usuario cadastro
  ) {
    List<Email> pegarEmails = usuarioServico.pegarEmails();
    List<Documento> pegarDocumentos = usuarioServico.pegarDocumentos();
    List<Empresa> empresas = servicoEmpresa.pegarTodas();
    EmpresaSelecionadora selecionadora = new EmpresaSelecionadora();
    Empresa main = selecionadora.selecionar(empresas, idEmpresa);
    if (main != null) {
      if (cadastro.getEmails().isEmpty()) {
        return new ResponseEntity<>(
          "Por Favor, coloque seu E-mail",
          HttpStatus.NOT_ACCEPTABLE
        );
      } else {
        if (cadastro.getDocumentos().isEmpty()) {
          return new ResponseEntity<>(
            "Por favor, Coloque um Documento",
            HttpStatus.NOT_ACCEPTABLE
          );
        } else {
          boolean docsErrorBoolean = false;
          Set<String> docsNumeroSet = new HashSet<>();
          String docError = " ";
          for (Documento docs : cadastro.getDocumentos()) {
            for (Documento todosDocs : pegarDocumentos) {
              if (todosDocs.getNumero().contains(docs.getNumero())) {
                docsErrorBoolean = true;
                docsNumeroSet.add(docs.getNumero());
              }
            }
          }
          if (docsErrorBoolean == true) {
            docError += docsNumeroSet + " Esse documento já está cadastrado";
            return new ResponseEntity<>(docError, HttpStatus.CONFLICT);
          } else {
            String erroLog = " ";
            List<String> listaDeEmails = new ArrayList<>();
            boolean EmailExistente = false;
            for (Email existentes : cadastro.getEmails()) {
              for (Email todosEmail : pegarEmails) {
                if (todosEmail.getEndereco().equals(existentes.getEndereco())) {
                  EmailExistente = true;
                  listaDeEmails.add(existentes.getEndereco());
                }
              }
            }
            if (EmailExistente == true) {
              erroLog += listaDeEmails + "Já existe";
              return new ResponseEntity<>(erroLog, HttpStatus.CONFLICT);
            } else {
              for (Documento docs : cadastro.getDocumentos()) {
                docs.setDataEmissao(new Date());
              }
              for (Telefone telefones : cadastro.getTelefones()) {
                cadastro.getTelefones().add(telefones);
              }
              if (cadastro.getPerfis().toString().contains("FORNECEDOR")) {
                if (cadastro.getMercadorias().isEmpty()) {
                  return new ResponseEntity<>(
                    "Fornecedor encontrado sem mercadoria, por favor insira um",
                    HttpStatus.NOT_ACCEPTABLE
                  );
                } else {
                  usuarioServico.salvarUsuario(cadastro);
                  main.getUsuarios().add(cadastro);
                  main.getMercadorias().addAll(cadastro.getMercadorias());
                  servicoEmpresa.Salvar(main);
                  return new ResponseEntity<>(
                    "Cadastro Efetuado",
                    HttpStatus.CREATED
                  );
                }
              } else {
                usuarioServico.salvarUsuario(cadastro);
                main.getUsuarios().add(cadastro);
                servicoEmpresa.Salvar(main);
                return new ResponseEntity<>(
                  "Cadastro Efetuado",
                  HttpStatus.CREATED
                );
              }
            }
          }
        }
      }
    }else {
    	return new ResponseEntity<>("Empresa não encontrada", HttpStatus.NOT_FOUND);
    }
  }
}
