<!DOCTYPE html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page session="true"%>

<head>
	<title>Ixia - Sistema de Escala - Cadastro projeto</title>
    <jsp:include page="../shared/headerPartialView.jsp"/>
	<script src="<c:url value='/js/ausencia.js' />"></script>
	<script> 

	 function apagarReposicao(id) {
		if (confirm("Deseja realmente apagar esta reposição?")) {

			var url1 = urlBase + "ausencia/reposicao/"+id;
			$.ajax({
				type : "DELETE",
				contentType : "application/json",
				accept: 'text/plain',
				url : url1,
				success : function(result) {

					console.log("success: "+ id);
					 $('#tabela-reposicao tbody tr#reposicao' + id).remove();	
					aplicarSelecionarTabela();
					cancelarReposicao();

				},
				error : function(e) {
					alert("Error!")
					console.log("ERROR: ", e);
				}
			});
		}
		else{
			return false;
		}
	 }

	 function apagarSolicitacao(id) {
		if (confirm("Deseja realmente apagar esta solicitação de ausência?")) {
			window.location.href = urlBase + "ausencia/delete/" + id;
		}
		else{
			return false;
		}
	 }

	 function enviarSolicitacao(id) {
		if (confirm("Deseja realmente enviar esta solicitação para aprovação?")) {
			$("#ativo").val(1);
		}
		else{
			return false;
		}
	 }
	 

	function aceita(id, aceita) {
		aceitaRecusaSolicitacaoAusencia(id, aceita, $("#motivo-recusa"), 1);
	}

	function aceitaRecusaSolicitacaoAusencia(id, aceita, motivo, origem)
    {
		if (!aceita && origem != 2){
			if (motivo == "" || motivo == null){
				alert("Preencha o campo observação com o motivo da recusa");
				return;
			}
		}
		
		if (confirm("Deseja realmente " + (aceita ? "aceitar" : "recusar") + " esta solicitação?")) {
			window.location.href = urlBase + "ausencia/aceita/" + id + "?origem=" + origem + "&aceita=" + aceita + (motivo == null || motivo == "" ? "" :  "&motivo=" + motivo);
		}		
    }
	 
	
	</script>
</head>
<body>
	<jsp:include page="../shared/navbarPartialView.jsp"/>

    <div class="container">    
   
	  <form:form id="form-solicitacao" modelAttribute="solicitacao" method="POST"  style="margin-top:50px" class="mainbox col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12 form-horizontal">
        	  
          <div class="panel panel-info">
          				
					
              <div class="panel-heading row">
                	
                  <h3 class="panel-title col-7 col-sm-7 col-md-7 col-lg-7 col-xl-7">Solicitação ausência</h3>   

				  <input id="id" name="id" value="${solicitacao.id}" type="hidden" /> 
				  <form:hidden path="ativo" /> 
				  
	        	  <div class="col-5 col-sm-5 col-md-5 col-lg-5 col-xl-5" > 
					
					 
	        	  	<c:if test='${solicitacao.id != 0}'>
           				<a class="btn btn-sm btn-primary float-right" style="margin: 1px" href="<c:url value='/dashboard' />/${solicitacao.projetoEscala.projetoId}?aceito=0&solicitacao=${solicitacao.id}&ano=${solicitacao.dataInicio.year}&mes=${solicitacao.dataInicio.monthValue}#selecionar">dashboard</a>
         			</c:if>
         			 
	        	  	<c:if test="${isAdministracao || solicitacao.usuario.id == usuarioLogado.id || solicitacao.ativo == 0}">
		        	  	
		        	  	<c:if test="${(solicitacao.ativo == 0 || solicitacao.ativo == 2)}">		        	  	
			        	  	<c:if test="${solicitacao.id != 0}">
			        	  		<c:if test='${!isDisableCampos}'> 
									<input onclick="apagarSolicitacao(${solicitacao.id})" id="btn-apagar-solicitacao" type="button" class="btn btn-sm btn-danger float-right" value="apagar" style="margin: 1px" />
								</c:if>
								<c:if test='${isDisableCampos}'>
								<a id="btn-editar" class="btn btn-sm btn-primary float-right" href="<c:url value='/ausencia' />/${solicitacao.id}/editar" style="margin: 1px">Editar</a>
								</c:if>
							</c:if>
							
			        	  	<c:if test="${!isDisableCampos && (solicitacao.ativo == 0 || solicitacao.ativo == 2)}">
								
				        	  	<c:if test="${solicitacao.ativo == 0 && solicitacao.id != 0}"> 
									<input id="btn-enviar-solicitacao" onclick="enviarSolicitacao(${solicitacao.id})"  type="submit" class="btn btn-sm btn-success float-right" value="Salvar e enviar aprovação" style="margin: 1px" />
								</c:if>
								
								<input id="btn-salvar-solicitacao" type="submit" onclick="$('#ativo').val(0);" class="btn btn-sm btn-success float-right" value="Salvar" style="margin: 1px" />
								
			        	  	</c:if>
						</c:if>
					</c:if>
					
       				
	        	  	<c:if test='${solicitacao.id == 0}'>
	        	  		<a id="btn-voltar" class="btn btn-sm btn-default float-right" href="<c:url value='ausencias' />" style="margin: 1px">Voltar</a>
					</c:if>
	        	  	<c:if test='${!isDisableCampos && solicitacao.id != 0}'>
	        	  		<a id="btn-voltar" class="btn btn-sm btn-default float-right" href="<c:url value='../../ausencias' />" style="margin: 1px">Voltar</a>
					</c:if>
	        	  	<c:if test="${isDisableCampos  && solicitacao.id != 0}"> 
        	  			<a id="btn-voltar" class="btn btn-sm btn-default float-right" href="<c:url value='../ausencias' />" style="margin: 1px">Voltar</a>
					</c:if>
           				
        	  	</div>
              </div>  
              <div class="panel-body" >
              
        		<jsp:include page="../shared/errorPartialView.jsp"/>	
          				        	  	                      
          			<div class="row align-items-start" style="margin-top: 30px">	  
					
											        
					   <c:forEach items="${solicitacao.dadosAcesso.dadosAcesso}" var="acesso">
        	  			<c:if test='${!acesso.nome.isEmpty()}'>
					  	 <div class="text-success col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
	  							  	Nivel de acesso: ${acesso.nome}
	  							  </div>
  							 </c:if>
					   </c:forEach>
					  
					  
						<div class="container row panel-custom col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
									       
					       <div class="form-group col-12 col-sm-12 col-md-12 col-lg-4 col-xl-4"> 
					       	<label for="motivo" class="control-label">Motivo</label>
					       	<div>
					           <form:select path="motivoAusencia.id" id="motivo-ausencia" class="form-control" disabled="${isDisableCampos}">
							        <option value="0"></option>
							        <c:forEach items="${motivos}" var="motivo">
							            <option <c:if test="${motivo.id == solicitacao.motivoAusencia.id}">selected</c:if> value="${motivo.id}">${motivo.nome}</option>
							        </c:forEach>
					           </form:select>
					           
							        <c:forEach items="${motivos}" var="motivo">
							            <input type="hidden" id="motivo-tipo${motivo.id}" value="${motivo.tipo}"/>
							        </c:forEach>
					           	<div class="invalid-feedback" id="feedback-folgaMotivo"></div>
					           </div>
					       </div>

						      <div class='form-group col-12 col-sm-12 col-md-12 <c:if test="${isAtendimento}">col-lg-8 col-xl-8</c:if><c:if test="${!isAtendimento}">col-lg-4 col-xl-4 </c:if>'>
							    <label for="projetoEscala.id" class="control-label">Escala projeto *</label>
						        <form:select path="projetoEscala.id" id="selected-projeto-escala-principal" class='form-control editable-select ${result.hasFieldErrors("projetoEscala.id") ? "is-invalid" : ""}'  disabled="${isDisableCampos}" >
							        <option value="0"></option>
							        <c:forEach items="${projetoEscalas}" var="item">
							        	<option <c:if test="${item.id == solicitacao.projetoEscala.id}">selected</c:if> value="${item.id}">${item.descricaoCompletaEscala}</option> 
							        </c:forEach>
						        </form:select>
						        <div class="invalid-feedback"><form:errors path="projetoEscala.id" /></div>
							  </div>		                    

						      			
	        	  			  						    
							  <div class="form-group col-12 col-sm-12 col-md-12 col-lg-4 col-xl-4" 
							  id="panel-selected-projeto-escala-principal" 
							  <c:if test="${isAtendimento}">style="display:none;"</c:if> >
							      <label for="usuario.id" class='control-label container row'> 
							   	    Prestador 
							      </label>
							        <form:select path="usuario.id" id="selected-usuario-solicitacao" disabled="${isDisableCampos && solicitacao.id != 0}" class="form-control" >
							        <option value="0"></option>
							        <form:options items="${usuariosSolicitacao}"  itemLabel="nomeCompletoMatricula" itemValue="id" class='form-control' />
							        </form:select>
							        <div class="invalid-feedback"><form:errors path="usuario.id" /></div>
							  </div> 


							  <div class="form-group col-12 col-xs-12 col-sm-6 col-md-6 col-lg-3 col-xl-3">
							      <label for="dataInicio" class='control-label'>Data início *</label>
		                	  	  <fmt:parseDate pattern="yyyy-MM-dd" value="${solicitacao.dataInicio}" var="dtIni" />
		                	  	  <c:set var="dataInicio"><fmt:formatDate value="${dtIni}" pattern="dd/MM/yyyy" /></c:set>
							      <form:input 
						      				path="dataInicio"
						      				value="${dataInicio}"
						      				class='form-control mask-date datepicker  ${result.hasFieldErrors("dataInicio") ? "is-invalid" : ""}' 
						      				placeholder2="data início" 
						      				disabled="${isDisableCampos}" 
						      				style="z-index: 1;" />
		                          <div class="invalid-feedback"><form:errors path="dataInicio" /></div>
							  </div>
							     
							  <div class="form-group col-12 col-xs-6 col-sm-6 col-md-6 col-lg-3 col-xl-3">
							      <label for="dataFim" id="dataFim" class='control-label container row'>
							      <fmt:parseDate pattern="yyyy-MM-dd" value="${solicitacao.dataFim}" var="dtFim" />
		                	  	  <c:set var="dataFim"><fmt:formatDate value="${dtFim}" pattern="dd/MM/yyyy" /></c:set>
							      <input 
							   	    	id="check-box-data-fim" 
					      				value="${dataFim}"
								   	    ${isDisableCampos ? "disabled" : ""} 
								   	    class="form-control row col-2 col-xs-2 col-sm-2 col-md-2 col-lg-2 col-xl-2" 
								   	    type="checkbox" 
								   	    style="margin-left: 1px; widht: 15px; top: 5px; z-index: 1;" />
							   	    <span class="col-10 col-xs-10 col-sm-10 col-md-10 col-lg-10 col-xl-10" >Data fim</span> 
							      </label>
						          <form:input 
						          			path="dataFim" 
						      				value="${dataFim}"
						      				class='form-control mask-date datepicker  ${result.hasFieldErrors("dataFim") ? "is-invalid" : ""}'  
						      				placeholder2="data fim" 
						      				disabled="${isDisableCampos}" 
						      				style="z-index: 2;" />
							  </div> 
							  
							   
							  <div class="form-group col-12 col-xm-6 col-sm-6 col-md-6 col-lg-3 col-xl-3">
							      <label for="horaInicio" class="control-label">Hora início *</label>
						          <form:input path="horaInicio" class='form-control mask-hour ${result.hasFieldErrors("horaInicio") ? "is-invalid" : ""}' placeholder2="hora início" disabled="${isDisableCampos}" />
						      	  <div class="invalid-feedback"><form:errors path="horaInicio" /></div>
							  </div>    
							   
							  <div class="form-group col-12 col-xm-6 col-sm-6 col-md-6 col-lg-3 col-xl-3">
							      <label for="horaFim" class="control-label">Hora fim *</label>
						          <form:input path="horaFim" class='form-control mask-hour ${result.hasFieldErrors("horaFim") ? "is-invalid" : ""}' placeholder2="hora fim" disabled="${isDisableCampos}" />
						            <c:if test="${solicitacao.horas != ''}"><span style="font-size: 10pt">${solicitacao.horas}/dia</span></c:if>
						      	  <div class="invalid-feedback"><form:errors path="horaFim" /></div>
							  </div> 
							  
							  
							  <div class="form-group col-sm-12">
							      <label for="observacao" class="control-label">Observações</label>
						          <form:textarea path="observacao" class='form-control' placeholder2="observações" disabled="${isDisableCampos}" />
							  </div> 
				
								
							  <c:forEach items="${solicitacao.dadosAcesso.dadosAprovacao}" var="aprovacao">
							  	<c:if test='${aprovacao.nome.contains("Pendente") || aprovacao.nome.contains("Não enviada")}'>
								  	<div class="text-primary col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12" style="font-size: 10pt">
    	  							  	${aprovacao.nome}
    	  							  </div>
							  	</c:if>
							  	<c:if test='${aprovacao.nome.contains("Aprovado")}'>
								  	<div class="text-success col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12" style="font-size: 10pt">
    	  							  	${aprovacao.nome}
    	  							  </div>
							  	</c:if>
							  	<c:if test='${aprovacao.nome.contains("Recusado")}'>
								  	<div class="text-danger col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12" style="font-size: 10pt">
    	  							  	${aprovacao.nome}
    	  							  </div>
							  	</c:if>
							  </c:forEach>			  
							  <br>
			  									  
							  
							  <c:if test="${solicitacao.dadosAcesso.visivelAprovacao}">
								  <div class="form-group col-12 col-xs-12 col-xm-12 col-sm-12 col-md-8 col-lg-8 col-xl-8">
								      <div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								      	  	
										    	<br />
										    	
								      	  		<c:if test="${solicitacao.dadosAcesso.aceitePrincipal == 0 || solicitacao.dadosAcesso.aceitePrincipal == 2}">
													<input id="btn-aprovar-reposicao" type="button" onclick="aceita(${solicitacao.id}, true)" class="btn btn-lg btn-success" value="Aceitar" style="margin: 1px" />
								      	  		</c:if>
								    			<c:if test="${solicitacao.dadosAcesso.aceitePrincipal == 0 || solicitacao.dadosAcesso.aceitePrincipal == 1}">
													<input id="btn-recusar-reposicao" type="button" onclick="aceita(${solicitacao.id}, false)" class="btn btn-lg btn-danger" value="Recusar" style="margin: 1px" /> 
												</c:if>
												
								    			<br />
								   	  </div>									   	  
							   	  
									  <div class="form-group col-sm-12">
									      <label for="ausencia-aprovacao" class="control-label">Observações aceite/recusa</label>
								          <textarea id="motivo-recusa" class='form-control'></textarea>
									  </div>
									  <span class="text-danger"><c:if test="${isAdministracao}">Cuidado admistrador(a): Com o perfil de administração todas as aprovações ou recusas serão feitas automaticamente.</c:if></span><br> 
									  <span class="">Verifique as solicitaçoes abaixo para realizar a aprovação</span>
								  </div> 
					     	  	</c:if> 
					     	  	
							  
							  <div class="form-group col-sm-12 col-md-8">
							      <label for="colocar-horario-a-disposicao" class='control-label container row'> 
							   	    <form:checkbox path="colocarHorarioDisposicao" id="colocar-horario-a-disposicao" disabled="${isDisableCampos}" class="form-control row col-2 col-xs-2 col-sm-1 col-md-1 col-lg-1 col-xl-1" style="margin-left: 1px; widht: 15px; top: 5px;; z-index: 1;" />  
							   	    <span class="col-9 col-xs-9 col-sm-10 col-md-10 col-lg-10 col-xl-10">Apenas colocar horário a disposição para troca</span>
							      </label>
							  </div> 
							  
							  <div class="form-group col-sm-12 col-md-8" id="panel-indicar-horario-para-repor">
							      <label for="indicarHorarioParaRepor" class='control-label container row'> 
							   	    <form:checkbox path="indicarHorarioParaRepor" id="indicar-horario-para-repor" disabled="${isDisableCampos}" class="form-control row col-2 col-xs-2  col-sm-1 col-md-1 col-lg-1 col-xl-1" style="margin-left: 1px; widht: 15px; top: 5px;; z-index: 1;" />
							   	    <span class="col-9 col-xs-9 col-sm-10 col-md-10 col-lg-10 col-xl-10">Indicar horário para repor</span> 
							      </label>
							  </div> 
							  
							  
							  <div class="form-group col-12 col-xs-12 col-xm-12 col-sm-12 col-md-8 col-lg-8 col-xl-8">  
							  
								<div id="panel-horario-reposicao" class="container border panel-custom">
								
									<h5 class="col-sm-12 border-bottom" style="padding-bottom: 17px">Horário reposição</h5> 
							    
								     <div class="container row">
												                        
					                	<input type="hidden" id="id-reposicao${reposicao.id}" value="${reposicao.id}" />
										  <div class="form-group col-12 col-xm-12 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										      <label for="data-reposicao" class='control-label'>Data *</label>
									          <input id="data-reposicao" class='form-control mask-date datepicker' disabled="${isDisableCampos}"  /> 
									      	  <div class="invalid-feedback" id="invalid-feedback-data-reposicao"></div>
										  </div>
										  
										   
										  <div class="form-group col-12 col-xm-6 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										      <label for="hora-inicio-reposicao" class="control-label">Hora início *</label>
									          <input id="hora-inicio-reposicao" class='form-control mask-hour' disabled="${isDisableCampos}" />
									      	  <div class="invalid-feedback" id="invalid-feedback-hora-inicio-reposicao"></div>
										  </div>    
										    
										  <div class="form-group col-12 col-xm-6 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										      <label for="hora-fim-reposicao" class="control-label">Hora fim *</label>
									          <input id="hora-fim-reposicao" class='form-control mask-hour' disabled="${isDisableCampos}" />
									      	  <div class="invalid-feedback" id="invalid-feedback-hora-fim-reposicao"></div>
										  </div> 
															       
									    
									    
									    
									    
									      <div class="form-group col-12 col-sm-12 col-md-12 col-lg-12 col-xl-6v "> 
										    <label for="selected-projeto-escala-troca" class="control-label">Escala projeto *</label>      
														 
									        <select id="selected-projeto-escala-troca" class='form-control editable-select ${result.hasFieldErrors("ausenciaReposicao.projetoEscalaTroca.id") ? "is-invalid" : ""}'  disabled="${isDisableCampos}" >
						        				<option value="0"></option>
										        <c:forEach items="${projetoEscalasTroca}" var="item">
										        	<option <c:if test="${item.id == ausenciaReposicao.projetoEscalaTroca.id}">selected</c:if> value="${item.id}">${item.descricaoCompletaEscala}</option> 
										        </c:forEach>
									        </select>											        

									        <div class="invalid-feedback" id="invalid-feedback-selected-projeto-escala-troca"></div>
										  </div>		 
												       
									    	  									    
										  <div class="form-group col-12 col-sm-12 col-md-12 col-lg-12 col-xl-6" >
										      <label for="ausenciaReposicao.indicadoOutroUsuario" class='control-label container row'> 
										   	    <input type="checkbox" disabled="${isDisableCampos}" id="checkbox-indicar-outro-usuario-troca" class="form-control row col-2 col-xs-2  col-sm-1 col-md-1 col-lg-1 col-xl-1" style="margin-left: 1px; widht:15px; top: 5px;; z-index: 1;" />
										   	    <span class="col-9 col-xs-9 col-sm-10 col-md-10 col-lg-10 col-xl-10" >Indicar prestador troca</span> 
										      </label>
										        <select id="selected-usuario-troca" class='form-control' disabled="${isDisableCampos}" >
											        <option value="0"></option>
											        <c:forEach items="${usuariosTroca}" var="item">
											        	<option <c:if test="${item.id == ausenciaReposicao.usuarioTroca.id}">selected</c:if> value="${item.id}">${item.nomeCompletoMatricula}</option> 
											        </c:forEach>
										        </select> 
										        <div class="invalid-feedback" id="invalid-feedback-selected-usuario-troca"></div>
										  </div> 	
										    
									   	  
									   	  
									   	  
									   	  
									   	  
										  <div class="form-group col-sm-12">
										      <label for="observacao-reposicao" class="control-label">Observações</label>
									          <textarea id="observacao-reposicao" class='form-control' disabled="${isDisableCampos}"></textarea>
										  </div>
								
										  
										  
									      <div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
									      	  	<c:if test="${!isDisableCampos}">
											    	<br />
									      	  		<input id="btn-cancelar-reposicao" type="button" class="btn btn-sm btn-success float-right" value="Cancelar" style="margin: 1px" />
													<input id="btn-editar-reposicao" type="button" class="btn btn-sm btn-success float-right" value="Novo" style="margin: 1px" />
									      	  		<input id="btn-salvar-reposicao" onclick="salvarReposicao()" type="button" class="btn btn-sm btn-success float-right" value="Salvar" style="display: none; margin: 1px" /> 
									    			<br /> 
									     	  	</c:if>
									   	  </div>
								 	
											 <div class="table-container table-responsive"  style="margin-left: 20px">
												<table id="tabela-reposicao" class="display tabela-simples">
											       <!-- Header Table -->
											       <thead>
											            <tr> 
															<th>Data</th>
											                <th>Horário</th>
															<th>Detalhes</th>
											                <th>Ação</th>
											            </tr>
											        </thead>
											        <tbody>
											        <c:forEach items="${solicitacao.ausenciaReposicoes}" var="reposicao">
									                	<fmt:parseDate pattern="yyyy-MM-dd" value="${reposicao.data}" var="data" /> 
											            <tr id="reposicao${reposicao.id}" onclick="selectReposicao(${reposicao.id})">
															<td id="xdata-reposicao${reposicao.id}">
																<fmt:formatDate value="${data}" pattern="dd/MM/yyyy" />
															</td>
											                <td id="xhora-inicio-fim-reposicao${reposicao.id}">${reposicao.horaInicio} - ${reposicao.horaFim}   <c:if test="${reposicao.horas != ''}">(${reposicao.horas})</c:if></td>
															<td  id="xdetalhes-reposicao${reposicao.id}" style="font-size:10pt">
																<%-- <c:if test="${reposicao.projetoEscalaTroca.prestador != null}">${reposicao.projetoEscalaTroca.prestador.nome}<br></c:if> --%>
																${reposicao.projetoEscalaTroca.descricaoCompletaEscala}
															</td>
											                <td>
																<input  onclick="apagarReposicao(${reposicao.id})" 
																		type="button" 
																		class="btn btn-sm btn-danger btn-class-apagar-reposicao float-right" 
																		value="apagar" 
																		style="margin: 1px" 
																		<c:if test="${isDisableCampos}">disabled</c:if> /> 
																		
											                	<input type="hidden" id="id-reposicao${reposicao.id}" value="${reposicao.id}" />
							      								<input type="hidden" id="data-reposicao${reposicao.id}" value="<fmt:formatDate value="${data}" pattern="dd/MM/yyyy" />" />
											                	<input type="hidden" id="hora-inicio-reposicao${reposicao.id}" value="${reposicao.horaInicio}" />
											                	<input type="hidden" id="hora-fim-reposicao${reposicao.id}" value="${reposicao.horaFim}" />
											                	<input type="hidden" id="selected-projeto-escala-troca${reposicao.id}" value="${reposicao.projetoEscalaTroca.id}" />
											                	<input type="hidden" id="observacao-reposicao${reposicao.id}" value="${reposicao.observacao}" />
											                	<input type="hidden" id="selected-usuario-troca${reposicao.id}" value="${reposicao.usuarioTroca.id}" />
											                	
											                </td>
											            </tr>													        
											        </c:forEach>     
											        </tbody>
											    </table>
											</div>
											
											
	  	
								     </div>
						  		</div>
							  </div>
							  
					   </div>  				


				</div>
               </div>
          </div>
               
        </form:form>	
    </div>
    
</body>

</html>