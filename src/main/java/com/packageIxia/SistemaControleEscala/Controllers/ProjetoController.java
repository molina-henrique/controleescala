package com.packageIxia.SistemaControleEscala.Controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.packageIxia.SistemaControleEscala.Helper.Utilities;
import com.packageIxia.SistemaControleEscala.Models.Projeto.Projeto;
import com.packageIxia.SistemaControleEscala.Models.Projeto.ProjetoEscala;
import com.packageIxia.SistemaControleEscala.Models.Projeto.ProjetoEscalaPrestador;
import com.packageIxia.SistemaControleEscala.Models.Projeto.ProjetoFolgaSemanal;
import com.packageIxia.SistemaControleEscala.Models.Referencias.DadoGenerico;
import com.packageIxia.SistemaControleEscala.Models.Referencias.DiaSemana;
import com.packageIxia.SistemaControleEscala.Models.Referencias.Funcao;
import com.packageIxia.SistemaControleEscala.Models.Referencias.FuncaoEnum;
import com.packageIxia.SistemaControleEscala.Models.Referencias.MotivoAusencia;
import com.packageIxia.SistemaControleEscala.Models.Referencias.TipoApontamentoHoras;
import com.packageIxia.SistemaControleEscala.Models.Usuario.Usuario;
import com.packageIxia.SistemaControleEscala.Services.ReferenciasService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoEscalaPrestadorService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoEscalaService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoFolgaSemanalService;
import com.packageIxia.SistemaControleEscala.Services.Projetos.ProjetoService;
import com.packageIxia.SistemaControleEscala.Services.Usuario.UsuarioService;
import com.packageIxia.SistemaControleEscala.Services.Usuario.UsuarioTurnosDisponiveisService;

@Controller
public class ProjetoController {

	private boolean CamposDisabilitados;
	private ModelAndView modelViewCadastro = new ModelAndView("projeto/projetoView");
	private ModelAndView modelViewCadastros = new ModelAndView("projeto/projetosView");
	
	private ProjetoService projetoService;
	private ProjetoEscalaService escalaService;
	private ProjetoEscalaPrestadorService projetoEscalaPrestadorService;
	private UsuarioService usuarioService;
	private ReferenciasService referenciasService;
	
	private Usuario usuarioLogado;
	private Projeto projetoEditado;
	private List<ProjetoEscala> escalas;
	private List<ProjetoEscalaPrestador> prestadores;
	private ProjetoEscala escalaSelecionada;
	private ProjetoFolgaSemanalService projetoFolgaSemanalService;
	private List<ProjetoFolgaSemanal> folgasSemanais;
	private UsuarioTurnosDisponiveisService usuarioTurnosDisponiveisService;
	private HttpSession session;
	
	@Autowired
	public ProjetoController(
			ProjetoService projetoService,
			ProjetoEscalaService projetoEscalaService,
			ProjetoEscalaPrestadorService prestadorService,
			UsuarioService usuarioService,
			ReferenciasService referenciasService,
			ProjetoFolgaSemanalService projetoFolgaSemanalService,
			UsuarioTurnosDisponiveisService usuarioTurnosDisponiveisService,
			HttpSession session) {
		this.projetoService = projetoService;
		this.escalaService = projetoEscalaService;
		this.projetoEscalaPrestadorService = prestadorService;
		this.usuarioService = usuarioService;
		this.referenciasService = referenciasService;
		this.projetoFolgaSemanalService = projetoFolgaSemanalService;
		this.usuarioTurnosDisponiveisService = usuarioTurnosDisponiveisService;
		this.session = session;
	}

	@GetMapping("/projetos")
	public ModelAndView projetos(HttpServletRequest request) {		

    	modelViewCadastro.addObject("result", null);
    	modelViewCadastro.addObject("errorMessage", null);
    	
		System.out.println("projetos");
		this.usuarioLogado = ((Usuario)request.getSession().getAttribute("usuarioLogado"));
    	if (this.usuarioLogado.getFuncaoId() == FuncaoEnum.atendimento.funcao.getId() ||
			this.usuarioLogado.getFuncaoId() == FuncaoEnum.financeiro.funcao.getId()) {
    		ModelAndView erroModelView = new ModelAndView("errorView");
    		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de projetos");
            return erroModelView;
    	}

		modelViewCadastros.addObject("projetosCadastrados", this.projetoService.findAllByUsuarioLogado());		
				
		return modelViewCadastros;
	}   
	
	@GetMapping("/projeto/{id}")
	public ModelAndView cadastroPorId(@PathVariable("id") long id) {		
		return getCadastroPorId(id, true);
	}

	@GetMapping("/projeto/{id}/editar")
	public ModelAndView cadastroPorIdEdit(@PathVariable("id") long id) {		
		return getCadastroPorId(id, false);
	}


	@GetMapping("/projeto")
	public ModelAndView cadastroNovo() {

		System.out.println("projeto novo");
		this.DisabilitarCampos(0, false);

    	modelViewCadastro.addObject("result", null);
    	modelViewCadastro.addObject("errorMessage", null);

		this.usuarioLogado = ((Usuario)session.getAttribute("usuarioLogado"));
    	if (this.usuarioLogado.getFuncaoId() == FuncaoEnum.atendimento.funcao.getId() ||
			this.usuarioLogado.getFuncaoId() == FuncaoEnum.financeiro.funcao.getId()) {
    		ModelAndView erroModelView = new ModelAndView("redirect:/error");
    		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de projetos");
            return erroModelView;
    	}
    	
		this.projetoEditado = new Projeto();
		this.projetoEditado.setId(0);
		this.projetoEditado.setDataInicio(LocalDate.now());
		this.projetoEditado.setAtivo(true);
		modelViewCadastro.addObject("projeto", this.projetoEditado); 

		this.escalas = new ArrayList<ProjetoEscala>(); 
		modelViewCadastro.addObject("escalas", this.escalas); 
		ProjetoEscala escalaEditada = new ProjetoEscala();
		escalaEditada.setAtivo(true);
		escalaEditada.setProjetoId(this.projetoEditado.getId());
		modelViewCadastro.addObject("escala", escalaEditada); 

		ProjetoEscalaPrestador prestadorEditado = new ProjetoEscalaPrestador();
		prestadorEditado.setProjeto(this.projetoEditado);
		prestadorEditado.setAtivo(true);
		this.prestadores = new ArrayList<ProjetoEscalaPrestador>();
		modelViewCadastro.addObject("prestadores", prestadores); 
		modelViewCadastro.addObject("prestador", prestadorEditado); 
		folgasSemanais = new ArrayList<ProjetoFolgaSemanal>();
				
		return modelViewCadastro;
	}

	private ModelAndView getCadastroPorId(long id, boolean disabilitarTodosCampos) {	
		
		System.out.println("projeto");		
		this.DisabilitarCampos(id, disabilitarTodosCampos);

    	modelViewCadastro.addObject("result", null);
    	modelViewCadastro.addObject("errorMessage", null);

		this.usuarioLogado = ((Usuario)session.getAttribute("usuarioLogado"));
    	if (this.usuarioLogado.getFuncaoId() == FuncaoEnum.atendimento.funcao.getId() ||
			this.usuarioLogado.getFuncaoId() == FuncaoEnum.financeiro.funcao.getId()) {
    		ModelAndView erroModelView = new ModelAndView("redirect:/error");
    		erroModelView.addObject("errorMessage", "Não permitido acesso a tela de projetos");
            return erroModelView;
    	}
    	
		this.projetoEditado = this.projetoService.findById(id);
		if (this.projetoEditado == null) {
    		ModelAndView erroModelView = new ModelAndView("redirect:/error");
    		erroModelView.addObject("errorMessage", "Projeto não existe ou nao cadastrado para este usuário");
            return erroModelView;
    	}
		
		modelViewCadastro.addObject("projeto", projetoEditado); 
					
		this.escalas = this.escalaService.findAllByProjetoId(id);		
		modelViewCadastro.addObject("escalas", this.escalas); 
		ProjetoEscala escalaEditada = new ProjetoEscala();
		escalaEditada.setAtivo(true);
		escalaEditada.setProjetoId(this.projetoEditado.getId());
		modelViewCadastro.addObject("escala", escalaEditada); 	

		if (this.escalaSelecionada == null || !(this.escalaSelecionada.getProjetoId() == id)) {
			this.escalaSelecionada = this.escalas.stream().findFirst().orElse(new ProjetoEscala()) ;
		}
		
		modelViewCadastro.addObject("escalaSelecionada", this.escalaSelecionada); 	
		this.prestadores = this.projetoEscalaPrestadorService.findAllByProjetoEscalaId(this. escalaSelecionada.getId());
		modelViewCadastro.addObject("prestadores", prestadores); 
		ProjetoEscalaPrestador prestadorEditado = new ProjetoEscalaPrestador();
		prestadorEditado.setProjeto(this.projetoEditado);
		prestadorEditado.setProjetoEscala(this.escalaSelecionada);
		prestadorEditado.setAtivo(true);
		modelViewCadastro.addObject("prestador", prestadorEditado); 
		folgasSemanais = new ArrayList<ProjetoFolgaSemanal>();
		
		return modelViewCadastro;
	}
	
	private void DisabilitarCampos(long projetoId, boolean disabilitarTodosCampos) {
		
		this.CamposDisabilitados = disabilitarTodosCampos;
		
		if (disabilitarTodosCampos) {
			modelViewCadastro.addObject("isDisableCamposProjeto", true);
			modelViewCadastro.addObject("isDisableCamposEscala", true);
			modelViewCadastro.addObject("isDisableCamposPrestador", true);
		} else {
			modelViewCadastro.addObject("isDisableCamposProjeto", false);
			modelViewCadastro.addObject("isDisableCamposEscala", projetoId == 0);
			modelViewCadastro.addObject("isDisableCamposPrestador", projetoId == 0 ? true : !escalaService.existsByProjetoId(projetoId));
			// todo: trocar por OR
		}
	} 

	
	@PostMapping("/projeto/{id}/editar")
	public ModelAndView cadastroPost(
    		@Valid @ModelAttribute("projeto")Projeto projeto,
    		BindingResult resultProjeto,
    		@Valid @ModelAttribute("escala")ProjetoEscala escala,
    		BindingResult resultEscala,
    		@Valid @ModelAttribute("prestador")ProjetoEscalaPrestador prestador,
    		BindingResult resultPrestador,
			@PathVariable("id") long id) {		

		if (escala.getDescricaoEscala() != null) {
			return escalaPost(escala, resultEscala);  
		}		
		
		if (prestador.getObservacaoPrestador() != null) {
			return prestadorPost(prestador, resultPrestador);  
		}		

		return projetoPost(projeto, resultProjeto);	
	}

	@PostMapping("/projeto")
	public ModelAndView cadastroNovoPost(
    		@Valid @ModelAttribute("projeto")Projeto projeto,
    		BindingResult result) {			
		return projetoPost(projeto, result);        
    }

	@RequestMapping("/escala/delete/{id}")
	public ModelAndView deleteescala(@PathVariable("id") long id) {			

		ModelAndView modelView = new ModelAndView("redirect:/projeto/" + this.projetoEditado.getId() + "/editar");
		System.out.println("projeto delete");
		System.out.println(id);
		
        String message = this.escalaService.delete(id);
		modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
        }
        
		return modelView;
    }

	@RequestMapping("/prestador/delete/{id}")
	public ModelAndView deletePrestador(@PathVariable("id") long id) {			

		ModelAndView modelView = new ModelAndView("redirect:/projeto/" + this.projetoEditado.getId() + "/editar");
		System.out.println("prestador delete");
		
		String message = this.deleteFolgaSemanal(id);
		modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
    		return modelView;
        }        
		
        message = this.projetoEscalaPrestadorService.delete(id);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
    		return modelView;
        }
        
		return modelView;
    }

	private String deleteFolgaSemanal(long id) {
		String message = this.projetoFolgaSemanalService.deleteByProjetoEscalaPrestadorId(id);	
//		List<ProjetoFolgaSemanal> todosPrestadores = this.projetoFolgaSemanalService.findAllByProjetoEscalaPrestadorId(id);
//		for (ProjetoFolgaSemanal projetoFolgaSemanal : todosPrestadores) {
//			message = this.projetoFolgaSemanalService.delete(projetoFolgaSemanal.getId());		
//	        if (Strings.isNotBlank(message)) {
//	        	return message;
//	        }	
//		}

		folgasSemanais = new ArrayList<ProjetoFolgaSemanal>();
		return message;
	}

	@RequestMapping("/projeto/delete/{id}")
	public ModelAndView deleteProjeto(@PathVariable("id") long id) {			

		ModelAndView modelView = new ModelAndView("redirect:/projetos/");
		System.out.println("projeto delete");
		System.out.println(id);
    	
        String message = this.projetoService.delete(id);
		modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelView.addObject("errorMessage", message);
        }
        
		return modelView;
    }
	
	private ModelAndView projetoPost(Projeto projeto, BindingResult result) {
		
		System.out.println("projeto post");

		modelViewCadastro.addObject("projeto", projeto);

		modelViewCadastro.addObject("escalas", this.escalas); 
		modelViewCadastro.addObject("escala", new ProjetoEscala());


		modelViewCadastro.addObject("escalaSelecionada", this.escalaSelecionada); 
		modelViewCadastro.addObject("prestadores", this.prestadores); 
		modelViewCadastro.addObject("prestador", new ProjetoEscalaPrestador());

		this.DisabilitarCampos(projeto.getId(), false);
    	
		boolean hasErrors = result.hasErrors();
    	modelViewCadastro.addObject("result", result);
    	if(hasErrors) {	    		
        	modelViewCadastro.addObject("camposComErro", Utilities.getAllErrosBindingResult(result));
            return modelViewCadastro;
        }   

        String message = this.projetoService.saveProjeto(projeto);
		
    	modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelViewCadastro.addObject("errorMessage", message);
        	return modelViewCadastro;
        }
		
		return new ModelAndView("redirect:/projeto/"+projeto.getId() + "/editar");
	}
    
	private ModelAndView escalaPost(ProjetoEscala escala, BindingResult result) {
		
		System.out.println("escala post");
		System.out.println(escala.getId());
		System.out.println(escala.getProjetoId());
		System.out.println(escala.getHoraInicio());
		System.out.println(escala.getHoraFim());
		System.out.println(escala.getDefinidaPrioridade());
		
		this.DisabilitarCampos(escala.getProjetoId(), false);

		modelViewCadastro.addObject("projeto", this.projetoEditado);
		
		modelViewCadastro.addObject("escala", escala);
		modelViewCadastro.addObject("escalas", this.escalas); 

		modelViewCadastro.addObject("escalaSelecionada", this.escalaSelecionada); 
		modelViewCadastro.addObject("prestadores", this.prestadores); 
		modelViewCadastro.addObject("prestador", new ProjetoEscalaPrestador()); // this.prestadorEditado);
		
    	
    	modelViewCadastro.addObject("errorMessage", null);
		boolean hasErrors = result.hasErrors();
    	modelViewCadastro.addObject("result", result);
    	if(hasErrors) {	    		
        	modelViewCadastro.addObject("camposComErro", Utilities.getAllErrosBindingResult(result));
            return modelViewCadastro;
        }   

        String message = this.escalaService.saveEscala(escala);
        
    	modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelViewCadastro.addObject("errorMessage", message);
        	return modelViewCadastro;
        }        
		
		return new ModelAndView( "redirect:/projeto/"+escala.getProjetoId() + "/editar");
	}


	@RequestMapping("/escala/selecionar/{id}")
	public ModelAndView selectionarEscala(@PathVariable("id") long id) {			

		ModelAndView modelView = new ModelAndView("redirect:/projeto/" + this.projetoEditado.getId() +  (this.CamposDisabilitados ? "" : "/editar"));
		System.out.println("selecionar escala");
		System.out.println(id);
		
        this.escalaSelecionada = this.escalaService.findById(id);
        
		return modelView;
    }
    
	private ModelAndView prestadorPost(ProjetoEscalaPrestador prestador, BindingResult result) {
		 
		System.out.println("prestador post");
    	
		this.DisabilitarCampos(this.projetoEditado.getId(), false);

		modelViewCadastro.addObject("projeto", this.projetoEditado);
		
		modelViewCadastro.addObject("escala", new ProjetoEscala()); // this.escalaEditada);
		modelViewCadastro.addObject("escalas", this.escalas);

		prestador.setProjeto(this.projetoEditado);
		prestador.setProjetoEscala(this.escalaSelecionada);
		
		modelViewCadastro.addObject("escalaSelecionada", this.escalaSelecionada); 
		modelViewCadastro.addObject("prestadores", this.prestadores); 
		modelViewCadastro.addObject("prestador", prestador);
		
    	modelViewCadastro.addObject("errorMessage", null);
		boolean hasErrors = result.hasErrors();
    	modelViewCadastro.addObject("result", result);
    	if(hasErrors) {	    		
        	modelViewCadastro.addObject("camposComErro", Utilities.getAllErrosBindingResult(result));
            return modelViewCadastro;
        }
    	
    	String message = "";
    	
    	if (prestador.isIndicadaFolgaSemana() && !folgasSemanais.isEmpty()) {
	    	message = this.projetoFolgaSemanalService.validaFolgasSemanais(folgasSemanais, escalaSelecionada, this.escalas); 
	    	modelViewCadastro.addObject("result", result);      
	    	modelViewCadastro.addObject("errorMessage", null);
	        if (Strings.isNotBlank(message)) {
	        	modelViewCadastro.addObject("errorMessage", message);
	        	return modelViewCadastro;
	        }      
    	}
        
    	if (prestador.isReenviarConvite()) {
    		prestador.setAceito(0);
    	}
    	
        message = this.projetoEscalaPrestadorService.save(prestador);  
        
    	modelViewCadastro.addObject("result", result);      
    	modelViewCadastro.addObject("errorMessage", null);
        if (Strings.isNotBlank(message)) {
        	modelViewCadastro.addObject("errorMessage", message);
        	return modelViewCadastro;
        }       

    	if (prestador.isIndicadaFolgaSemana()) {
			System.out.println("Post all folgaSemanal");
			System.out.println(folgasSemanais.size());
	        this.projetoFolgaSemanalService.saveFolgasSemanais(folgasSemanais, prestador.getId());
	        if (folgasSemanais.isEmpty()) {
	        	prestador.setIndicadaFolgaSemana(false);
	        	this.projetoEscalaPrestadorService.save(prestador);
	        }
    	}
    	else {
    		message = this.deleteFolgaSemanal(prestador.getId());
    	}
    	
		return new ModelAndView( "redirect:/projeto/"+ this.projetoEditado.getId() + "/editar");
	}
	
    @ModelAttribute("gerentes")
    public List<Usuario> bancos() {
       return this.usuarioService.findByFuncaoId(FuncaoEnum.gerencia.funcao.getId());
    }
    
    @ModelAttribute("monitores")
    public List<Usuario> monitores() {
       return this.usuarioService.findByFuncaoId(FuncaoEnum.monitoramento.funcao.getId());
    }
    
    @ModelAttribute("atendentes")
    public List<Usuario> prestadores() {
    	List<Usuario> prestadores = this.usuarioService.findByFuncaoId(FuncaoEnum.atendimento.funcao.getId());
    	return prestadores;
    }
    
    @ModelAttribute("tipoApontamentoHoras")
    public List<TipoApontamentoHoras> tipoApontamentoHoras() {
       return this.referenciasService.getTipoApontamentoHoras();
    }
    
    @ModelAttribute("prioridades")
    public List<DadoGenerico> prioridades() {
       return this.referenciasService.getPrioridades();
    }
    
    @ModelAttribute("diasSemana")
    public List<DiaSemana> diasSemana() {
       return this.referenciasService.getDiasSemana();
    }
    
    @ModelAttribute("funcoes")
    public List<Funcao> funcoes() {
       return this.referenciasService.getFuncoes(1);
    }
    
    @ModelAttribute("motivos")
    public List<MotivoAusencia> motivos() {
        return this.referenciasService.getMotivosAusencia(1);
    }
	
	@RequestMapping(value = "/projeto/escalafolgasugerida/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> escalaFolgaSugerida(
			@PathVariable("id") long id,
			HttpServletRequest request) {
		
		System.out.println("Get escala folgas sugerida");		
		return usuarioTurnosDisponiveisService.findEscalaFolgaSugerida(id);
	}
	
	@RequestMapping(value = "/projeto/folgasSemanais/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ProjetoFolgaSemanal> folgaSemanal(
			@PathVariable("id") long id,
			HttpServletRequest request) {
		
		System.out.println("Get folgasSemanais");
		if (id == 0) {
			folgasSemanais = new ArrayList<ProjetoFolgaSemanal>();
			return folgasSemanais;
		}
		
		folgasSemanais = (folgasSemanais != null && !folgasSemanais.isEmpty()  && id == folgasSemanais.get(0).getProjetoEscalaPrestadorId()) ? 
							folgasSemanais : this.projetoFolgaSemanalService.findAllByProjetoEscalaPrestadorId(id);
		folgasSemanais = folgasSemanais != null ? folgasSemanais : new ArrayList<ProjetoFolgaSemanal>();
		return folgasSemanais;
	}
	
    @ResponseBody
    @PostMapping(value = "/projeto/folgaSemanal")
	public long folgaSemanalSave(@RequestBody ProjetoFolgaSemanal projetoFolgaSemanal) {		

		System.out.println("Post folgaSemanal");
		System.out.println(projetoFolgaSemanal);
    	return this.projetoFolgaSemanalService.preSaveFolgaSemanal(folgasSemanais, projetoFolgaSemanal);   	 
	}
	
    @ResponseBody
    @DeleteMapping(value = "/projeto/folgaSemanal")
	public int folgaSemanalDelete(@RequestBody int id) {
    	return this.projetoFolgaSemanalService.preDeleteFolgaSemanal(folgasSemanais, id);   
	}
}
