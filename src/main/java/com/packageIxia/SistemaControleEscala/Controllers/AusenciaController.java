package com.packageIxia.SistemaControleEscala.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.packageIxia.SistemaControleEscala.Helper.Utilities;
import com.packageIxia.SistemaControleEscala.Models.Projeto.AusenciaReposicao;
import com.packageIxia.SistemaControleEscala.Models.Projeto.AusenciaSolicitacao;
import com.packageIxia.SistemaControleEscala.Models.Projeto.DadosAcessoSolicitacaoAusencia;
import com.packageIxia.SistemaControleEscala.Models.Projeto.ProjetoEscala;
import com.packageIxia.SistemaControleEscala.Models.Referencias.FuncaoEnum;
import com.packageIxia.SistemaControleEscala.Models.Referencias.MotivoAusencia;
import com.packageIxia.SistemaControleEscala.Models.Usuario.Usuario;
import com.packageIxia.SistemaControleEscala.Services.ReferenciasService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.AusenciaReposicaoService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.AusenciaSolicitacaoService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoEscalaPrestadorService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoEscalaService;

@Controller
public class AusenciaController {
	
	private AusenciaSolicitacaoService ausenciaSolicitacaoService;

	private ModelAndView modelViewSolicitacoes = new ModelAndView("projeto/ausencias");
	private ModelAndView modelViewSolicitacao = new ModelAndView("projeto/ausencia");

	private AusenciaSolicitacao solicitacaoEditada;

	private ProjetoEscalaPrestadorService projetoEscalaPrestadorService;

	private ReferenciasService referenciasService;

	private HttpSession session;

	private ProjetoEscalaService projetoEscalaService;

	private AusenciaReposicaoService ausenciaReposicaoService;

	private Usuario usuarioLogado;	
	
	@Autowired
	public AusenciaController(
		AusenciaSolicitacaoService ausenciaSolicitacaoService,
		AusenciaReposicaoService ausenciaReposicaoService,
		ProjetoEscalaPrestadorService projetoEscalaPrestadorService,
		ProjetoEscalaService projetoEscalaService,
		ReferenciasService referenciasService,
		HttpSession session) {
		this.ausenciaSolicitacaoService = ausenciaSolicitacaoService;
		this.projetoEscalaPrestadorService = projetoEscalaPrestadorService;
		this.projetoEscalaService = projetoEscalaService;
		this.referenciasService = referenciasService;
		this.ausenciaReposicaoService = ausenciaReposicaoService;
		this.session = session;
	}

	@GetMapping("/ausencias")
	public ModelAndView ausencias(
    		HttpServletRequest request) {
		
		System.out.println("ausencias");
		modelViewSolicitacao.addObject("result", null);
		modelViewSolicitacao.addObject("errorMessage", null);
    	
		modelViewSolicitacoes.addObject("solicitacoes", this.ausenciaSolicitacaoService.findAll());		
				
		return modelViewSolicitacoes;
	}
	
	@GetMapping("/ausencia/{id}")
	public ModelAndView ausencia(@PathVariable("id") long id) {		
		return ausencia(id, true);
	}
	
	@GetMapping("/ausencia/{id}/editar")
	public ModelAndView ausenciaEditar(@PathVariable("id") long id) {				
		return ausencia(id, false);
	}
	
	@PostMapping("/ausencia/{id}/editar")
	public ModelAndView ausenciaSalvar(
			@PathVariable("id") long id,
    		@Valid @ModelAttribute("solicitacao")AusenciaSolicitacao solicitacao,
    		BindingResult result) {
		

		System.out.println("ausencia post");
		return ausenciaPost(id, solicitacao, result);
	}

	private ModelAndView ausenciaPost(long id, @Valid AusenciaSolicitacao solicitacao, BindingResult result) {
		String message = "";		
		
		modelViewSolicitacao.addObject("result", null);
		modelViewSolicitacao.addObject("errorMessage", null);
		
    	solicitacao.setTipoAusencia(
    			solicitacao.getColocarHorarioDisposicao() ? 1 : 
    				(solicitacao.getIndicarHorarioParaRepor() ? 2 : 0)); 		
		
		solicitacao.setAusenciaReposicoes(this.solicitacaoEditada.getAusenciaReposicoes());
    	modelViewSolicitacao.addObject("solicitacao", solicitacao);
		
		this.setUsuarioSolicitacao(solicitacao, modelViewSolicitacao);

		List<ProjetoEscala> projetoEscalas = this.projetoEscalaService.findAllByPermissao();
		modelViewSolicitacao.addObject("projetoEscalas", projetoEscalas);
		modelViewSolicitacao.addObject("projetoEscalasTroca", projetoEscalas);
		
		this.setProjetoEscalaTroca(solicitacao, modelViewSolicitacao);
		
		boolean hasErrors = result.hasErrors();
    	modelViewSolicitacao.addObject("result", result);
    	if(hasErrors) {	    		
        	modelViewSolicitacao.addObject("camposComErro", Utilities.getAllErrosBindingResult(result));
            return modelViewSolicitacao;
        }   
    			
    	message = this.ausenciaSolicitacaoService.save(solicitacao);
    	modelViewSolicitacao.addObject("solicitacao", solicitacao);	    
		if (Strings.isNotBlank(message)) {
	    	modelViewSolicitacao.addObject("errorMessage", message);
	    	return modelViewSolicitacao;
	    }
		
		if (solicitacao.getAtivo() == 1) {
			return new ModelAndView("redirect:/ausencia/"+solicitacao.getId());
		}
		
		return new ModelAndView("redirect:/ausencia/"+solicitacao.getId()+"/editar");
		
	}

	private void setUsuarioSolicitacao(AusenciaSolicitacao solicitacao, ModelAndView modelViewSolicitacao) {

		modelViewSolicitacao.addObject("usuariosSolicitacao", null);
		modelViewSolicitacao.addObject("usuariosTroca", null);
		
		if (hasProjetoEscala(solicitacao)) {
			List<Usuario> usuariosSolicitacao = this.projetoEscalaPrestadorService.findAllPrestadoresByProjetoEscalaId(solicitacao.getProjetoEscala().getId());
			modelViewSolicitacao.addObject("usuariosSolicitacao", usuariosSolicitacao);
			
			List<Usuario> usuariosTroca = usuariosSolicitacao.stream().filter(x->x.getId() != solicitacao.getUsuario().getId()).collect(Collectors.toList());
			if (hasUsuario(solicitacao)) {
				modelViewSolicitacao.addObject("usuariosTroca", usuariosTroca);
			}
		}
	}

	private boolean hasProjetoEscala(AusenciaSolicitacao solicitacao) {
		return solicitacao != null &&
			solicitacao.getProjetoEscala() != null &&
			solicitacao.getProjetoEscala().getId() != 0;
	}

	private boolean hasUsuario(AusenciaSolicitacao solicitacao) {
		return solicitacao != null &&
				solicitacao.getUsuario() != null &&
						solicitacao.getUsuario().getId() != 0;
	}

	private ModelAndView ausencia(long id, boolean disable) {
		System.out.println("ausenciaby id");
		modelViewSolicitacao.addObject("isDisableCampos", disable);
		modelViewSolicitacao.addObject("result", null);
		modelViewSolicitacao.addObject("errorMessage", null);

		this.solicitacaoEditada = new AusenciaSolicitacao();
		modelViewSolicitacao.addObject("solicitacao", this.solicitacaoEditada);

		List<ProjetoEscala> projetoEscalas = this.projetoEscalaService.findAllByPermissao();
		modelViewSolicitacao.addObject("projetoEscalas", projetoEscalas);
		modelViewSolicitacao.addObject("projetoEscalasTroca", projetoEscalas);

		this.setUsuarioSolicitacao(solicitacaoEditada, modelViewSolicitacao);
		
		this.usuarioLogado = ((Usuario)session.getAttribute("usuarioLogado"));
		if (id == 0) {		
	    	if (this.usuarioLogado.getFuncaoId() == FuncaoEnum.financeiro.funcao.getId() ||
    			this.usuarioLogado.getFuncaoId() == FuncaoEnum.diretoria.funcao.getId()) {
	        		ModelAndView erroModelView = new ModelAndView("errorView");
	        		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de ausencia");
	                return erroModelView;
	        	}
	    			
			return modelViewSolicitacao;
		}
		
		this.solicitacaoEditada = this.ausenciaSolicitacaoService.findById(id);

		if (solicitacaoEditada.getAtivo() == 1) {
			modelViewSolicitacao.addObject("isDisableCampos", true);
		}		

		if (usuarioLogado.getFuncaoId() != FuncaoEnum.administracao.getFuncao().getId() && 
			this.solicitacaoEditada.getUsuario().getId() != usuarioLogado.getId() &&
			(solicitacaoEditada.getAtivo() == 0 || solicitacaoEditada.getAtivo() == 4)) {
    		ModelAndView erroModelView = new ModelAndView("redirect:errorView");
    		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de ausencia");
            return erroModelView;
    	} 
		
		DadosAcessoSolicitacaoAusencia acesso = new DadosAcessoSolicitacaoAusencia(usuarioLogado, this.solicitacaoEditada);
		acesso.setDadosAprovacaoVisibilidade();	
		solicitacaoEditada.setDadosAcesso(acesso);
		
		if (!(acesso.isAdminstracao() ||
				acesso.isAtendente() ||
				acesso.isMonitor() ||
				acesso.isGerente() ||
				acesso.isResponsavelTrocaReposicao() ||
				acesso.isAtendenteTrocaReposicao() ||
				acesso.isGerenteReposicao())) {
    		ModelAndView erroModelView = new ModelAndView("errorView");
    		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de ausencia");
            return erroModelView;
    	} 
    	else if (!acesso.isAdminstracao() && this.solicitacaoEditada.getUsuario().getId() != usuarioLogado.getId()) { 
    		// não é o atendente desabilita campos não esconde aprovações
    		modelViewSolicitacao.addObject("isDisableCampos", true);      		
		}

		this.solicitacaoEditada.setColocarHorarioDisposicao(this.solicitacaoEditada.getTipoAusencia() == 1);
		this.solicitacaoEditada.setIndicarHorarioParaRepor(this.solicitacaoEditada.getTipoAusencia() == 2);

		this.setUsuarioSolicitacao(solicitacaoEditada, modelViewSolicitacao);
		
		this.setProjetoEscalaTroca(solicitacaoEditada, modelViewSolicitacao);
			
		modelViewSolicitacao.addObject("solicitacao", this.solicitacaoEditada);
		
		return modelViewSolicitacao;
	}

	private void setProjetoEscalaTroca(AusenciaSolicitacao solicitacao, ModelAndView modelViewSolicitacao) {
		if (hasUsuario(solicitacao)) {
			if(solicitacao.getUsuario().getId() != usuarioLogado.getId()) {
				if (hasProjetoEscala(solicitacao)) {
					Object projetoEscala = this.projetoEscalaService.findAllByPrestadorIdExceptPrestadorEscalaId(
							solicitacao.getUsuario().getId(),
							solicitacao.getProjetoEscala().getId());
					modelViewSolicitacao.addObject("projetoEscalasTroca", projetoEscala);					
				}
				else {
					Object projetoEscala = this.projetoEscalaService.findAllByPrestadorId(
							solicitacao.getUsuario().getId());
					modelViewSolicitacao.addObject("projetoEscalasTroca", projetoEscala);
				}
			}
		}
	}

	@GetMapping("/ausencia")
	public ModelAndView nova() {

		System.out.println("ausencia nova");		
		return ausencia(0, false) ;
	}
	
	@PostMapping("/ausencia")
	public ModelAndView post(
    		@Valid @ModelAttribute("solicitacao")AusenciaSolicitacao solicitacao,
    		BindingResult result) {

		System.out.println("ausencia nova post");
		return ausenciaPost(0, solicitacao, result);
	}

    @ModelAttribute("motivos")
    public List<MotivoAusencia> motivos() {
        return this.referenciasService.getMotivosAusencia();
    }

    @RequestMapping(value = "/ausencia/usuariosPorProjetoEscalaId/{projetoEscalaId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Usuario> usuariosPorEscalaPrestadorId(
			@PathVariable("projetoEscalaId") long projetoEscalaId,
			@RequestParam(value = "usuarioId", defaultValue = "0") long usuarioId,
			HttpServletRequest request) {
		
		System.out.println("Get usuariosPorEscalaPrestadorId");
		if (projetoEscalaId == 0) {
			return new ArrayList<Usuario>();
		}
		
		return this.projetoEscalaPrestadorService.findAllByProjetoEscalaIdExceptUsuarioId(projetoEscalaId, usuarioId);
	}

    @RequestMapping(value = "/ausencia/projetoEscalaPorUsuarioId/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ProjetoEscala> projetoEscalaPorUsuarioId(
			@PathVariable("usuarioId") long usuarioId,
			@RequestParam(value = "prestadorEscalaId", defaultValue = "0") long prestadorEscalaId,
			HttpServletRequest request) {
		
		System.out.println("Get escalaPorUsuarioId");
		if (usuarioId == 0) {
			return new ArrayList<ProjetoEscala>();
		}
		
		if (prestadorEscalaId == 0) {
			return this.projetoEscalaService.findAllByPrestadorId(usuarioId);
		}
		
		return this.projetoEscalaService.findAllByPrestadorIdExceptPrestadorEscalaId(usuarioId, prestadorEscalaId);
	}


    @ResponseBody
	@DeleteMapping("/ausencia/reposicao/{id}")
	public String deleteAusenciaReposicao(@PathVariable("id") long id) {			
		System.out.println("ausencia delete");
		System.out.println(id);
		
		for (int i = 0; i < this.solicitacaoEditada.getAusenciaReposicoes().size();i++) {
			if(this.solicitacaoEditada.getAusenciaReposicoes().get(i).getId() == id) {
				this.solicitacaoEditada.getAusenciaReposicoes().remove(i);
				break;
			}
		}
		
		return "";
	}

    @ResponseBody
	@PostMapping(value = "/ausencia/reposicao")
	public String postReposicao(@RequestBody AusenciaReposicao ausenciaReposicao) {			
		System.out.println("reposicao post");
		long id = this.ausenciaReposicaoService.preSaveReposicoes(ausenciaReposicao, this.solicitacaoEditada);		
		return String.valueOf(id);
	}

	
	@RequestMapping("/ausencia/delete/{id}")
	public ModelAndView deleteAusencia(@PathVariable("id") long id) {			

		ModelAndView modelView = new ModelAndView("redirect:/ausencias/");
		System.out.println("ausencias delete");
		System.out.println(id);
    	
        String message = this.ausenciaSolicitacaoService.delete(id);
        modelView.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
        }
        
		return modelView;
    }

	@RequestMapping("/ausencia/aceita/{id}")
	public ModelAndView aceitaAusencia(
			 @PathVariable("id") long id,
			 @RequestParam("aceita") boolean aceita, 
			 @RequestParam(value = "origem", defaultValue = "0") int origem,
			 @RequestParam(value = "motivo", defaultValue = "") String motivo) {
		
		ModelAndView modelView = new ModelAndView("redirect:/ausencia/" + id);
		if (origem == 2) {
			modelView = new ModelAndView("redirect:/ausencias/");
		}
		else if (origem == 3) {
			modelView = new ModelAndView("redirect:/index");
		}
		
		System.out.println("ausencia aceita");
		System.out.println(id);
        modelView.addObject("errorMessage", null);
		
        String message = this.ausenciaSolicitacaoService.aceita(id, aceita, motivo);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
        }

		return modelView;
    }

}
