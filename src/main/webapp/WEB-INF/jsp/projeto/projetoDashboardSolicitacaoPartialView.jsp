<!DOCTYPE html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page session="true"%>

<c:forEach items="${solicitacoes}"
	var="solicitacao" varStatus="status">
	<c:if
		test="${(solicitacao.dataFim == null ? solicitacao.dataInicio == dia.data : dia.data <= solicitacao.dataFim && dia.data >= solicitacao.dataInicio) }">
		
		<br>
		<b>Solicitação folga</b>
		<button style="font-size: 10pt; font-weight: bold"
			class="btn btn-sm btn-warning"
			onclick="$(this.nextElementSibling).toggle();"
			style="margin: 1px">detalhes</button>

		<div id="card-solicitacao${solicitacao.id}${indexString}"
			class="card bg-light" style="position: absolute; display: none">
			<div class="card-body">

				<b>Solicitação efetuada</b> <a target="_blank"
					style="font-size: 10pt; font-weight: bold"
					class="btn btn-sm btn-warning"
					href="<c:url value='/ausencia' />/${solicitacao.id}"
					style="margin: 1px">ver</a>

				<div style="font-size: 10pt;">
					<b>Status: </b>
					<c:if test='${solicitacao.aceito == 0}'>Pendente</c:if>
					<c:if test='${solicitacao.aceito == 1}'>Em aprovação</c:if>
					<c:if test='${solicitacao.aceito == 2}'>Recusada</c:if>
					<c:if test='${solicitacao.aceito == 3}'>Aprovada</c:if>
					<c:if test='${solicitacao.aceito == 4}'>Finalizada</c:if>
				</div>

				<div style="font-size: 10pt">
					<b>Solicitada por: </b>${solicitacao.usuario.nomeCompletoMatricula}
				</div>

				<div class="badge badge-info" style="font-size: 10pt">${solicitacao.motivoAusencia.nome}
					(${solicitacao.horaInicio} - ${solicitacao.horaFim} |
					${solicitacao.horas}/dia)
				</div>

				<div style="font-size: 10pt">
					<b>Projeto/escala: </b> <a target="_blank"
						href="<c:url value='/projeto' />/${solicitacao	.projetoEscala.projetoId}">
						<i>${solicitacao.projetoEscala.descricaoCompletaEscala}</i>
					</a>
					<c:if test="${solicitacao.observacao != ''}">Observação: ${solicitacao.observacao}</c:if>
				</div>

				<c:if test="${solicitacao.dataFim != null}">
					<fmt:parseDate pattern="yyyy-MM-dd"
						value="${solicitacao.dataInicio}" var="dtIni" />
					<c:set var="dataInicio">
						<fmt:formatDate value="${dtIni}" pattern="dd/MM/yyyy" />
					</c:set>
					<fmt:parseDate pattern="yyyy-MM-dd"
						value="${solicitacao.dataFim}" var="dtFim" />
					<c:set var="dataFim">
						<fmt:formatDate value="${dtFim}" pattern="dd/MM/yyyy" />
					</c:set>
					<div style="font-size: 10pt">De ${dataInicio} até
						${dataFim}</div>
				</c:if>

				<c:if test='${solicitacao.tipoAusencia == 1}'>
					<div style="font-size: 10pt; font-weight: bold">Horário
						a disposição:</div>

				</c:if>
				<c:if
					test='${solicitacao.tipoAusencia == 2 && solicitacao.ausenciaReposicoes != null && solicitacao.ausenciaReposicoes.size() > 0}'>
					<div
						style="font-size: 12pt; font-weight: bold; margin-top: 10px">
						Dados reposição
					</div>

					<c:forEach items="${solicitacao.ausenciaReposicoes}"
						var="reposicao">
						<fmt:parseDate pattern="yyyy-MM-dd"
							value="${reposicao.data}" var="data" />
						<div class="badge badge-info" style="font-size: 10pt">
							<fmt:formatDate value="${data}" pattern="dd/MM/yyyy" />
							| ${reposicao.horaInicio} - ${reposicao.horaFim}
							<c:if test="${reposicao.horas != ''}">(${reposicao.horas})</c:if>
						</div>

						<c:if test='${reposicao.indicadoOutroUsuario}'>
							<div style="font-size: 10pt">
								<b>Solicitada para: </b>${reposicao.usuarioTroca.nomeCompletoMatricula}
							</div>
						</c:if>

						<div style="font-size: 10pt">
							<b>Reposição projeto/escala: </b> 
							<a target="_blank"
								href="<c:url value='/dashboard' />/${reposicao.projetoEscalaTroca.projetoId}?mes=${reposicao.data.monthValue}&ano=${reposicao.data.year}&escala=${reposicao.projetoEscalaTroca.id}&solicitacao=${solicitacao.id}&aceito=0#selecionarReposicao">
								<i>${reposicao.projetoEscalaTroca.descricaoCompletaEscala}</i>
							</a>
							<c:if test="${reposicao.observacao != ''}">Observação: ${reposicao.observacao}</c:if>
						</div>
						<br>
					</c:forEach>
				</c:if>
			</div>
		</div>

		<div style="font-size: 10pt;">
			<b>Status: </b>
			<c:if test='${solicitacao.aceito == 0}'>Pendente</c:if>
			<c:if test='${solicitacao.aceito == 1}'>Em aprovação</c:if>
			<c:if test='${solicitacao.aceito == 2}'>Recusada</c:if>
			<c:if test='${solicitacao.aceito == 3}'>Aprovada</c:if>
			<c:if test='${solicitacao.aceito == 4}'>Finalizada</c:if>
		</div>

		<div style="font-size: 10pt">
			<b>Solicitada por: </b>${solicitacao.usuario.nomeCompletoMatricula}
		</div>

		<div class="badge badge-info" style="font-size: 10pt">${solicitacao.motivoAusencia.nome}
			(${solicitacao.horaInicio} - ${solicitacao.horaFim} |
			${solicitacao.horas}/dia)</div>

		<c:if test="${solicitacaoId == solicitacao.id}">
			<input type="hidden"
				<c:if test="${selecionarSolicitacao == 1}">id="selecionar"</c:if>
				value="${solicitacao.id}" />
			<c:set var="selecionarSolicitacao">0</c:set>
		</c:if>

		<br>

	</c:if>
	
</c:forEach>