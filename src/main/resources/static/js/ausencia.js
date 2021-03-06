	$(document).ready(function() {		

   	 	cancelarReposicao(); 

	     $("#btn-cancelar-reposicao").click(function(){
	    	 cancelarReposicao();
			 $("#btn-editar-reposicao").val("Nova");
			 $("#btn-editar-reposicao").show();
			 $("#btn-salvar-reposicao").hide();
	    	 
	      });
	     
		 $("#btn-editar-reposicao").click(function() {
	         editarReposicao();
		 }); 
		 
		if ($("#indicar-horario-para-repor").prop("checked")) {
			$("#panel-horario-reposicao").show();			
		}
		else {
			$("#panel-horario-reposicao").hide(); 				
		}

		if ($("input[name='dataFim']").val() == null || $("input[name='dataFim']").val() == "") {
			$("#check-box-data-fim").prop("checked", false);

			if (!$("input[name='dataFim']").prop("disabled")) {
				$("input[name='dataFim']").prop("disabled", true);
			}
		}
		else {
			$("#check-box-data-fim").prop("checked", true);
			if (!$("input[name='dataFim']").prop("disabled")) {
				$("input[name='dataFim']").prop("disabled", false);
			}
		}
		
		
		$("#indicar-horario-para-repor").change(function() { 
			if (this.checked) {
				$("#colocar-horario-a-disposicao").prop("checked", false);
				$("#panel-horario-reposicao").show();
			} else {
				$("#panel-horario-reposicao").hide();				
			}
		});	
		
		$("#colocar-horario-a-disposicao").change(function() {
			if (this.checked) {
				$("#indicar-horario-para-repor").prop("checked", false);
				$("#panel-horario-reposicao").hide();	
			}
		});

		$("#motivo-ausencia").change(function() {
			motivoAusenciaEscondePaineis();
		});		

		function motivoAusenciaEscondePaineis() {
			if ($("#motivo-ausencia").val() == 2) {
				$("#indicar-horario-para-repor").prop("checked", false);
				$("#panel-horario-reposicao").hide();	
				$("#panel-indicar-horario-para-repor").hide();				
			} 
			else {
				$("#panel-indicar-horario-para-repor").show();				
			}
		}
		
		motivoAusenciaEscondePaineis();
		
		$("#check-box-data-fim").change(function() {
			if (this.checked) {
				$("input[name='dataFim']").prop("disabled", false);
			} else {
				$("input[name='dataFim']").prop("disabled", true);		
			}
		}); 
		
		

		$("#selected-usuario-troca").prop("disabled", true);
		
		$("#checkbox-indicar-outro-usuario-troca").change(function() {			
			if (this.checked) {
				$("#selected-usuario-troca").prop("disabled", false);
			} else {
				$("#selected-usuario-troca").prop("disabled", true);	
				 $("#selected-usuario-troca option").each(function () { 
			         $(this).removeAttr("selected");
			         $(this).prop("selected", false);
				 });	
			}
		});

		

		// 3 
		if ($("#panel-selected-projeto-escala-principal").is(":visible")) {
			$("#selected-projeto-escala-principal").change(function() {
				preencherUsuarioPorProjetoEscalaId(this.value, '#selected-usuario-solicitacao')
			});
		}
		
		// 2 main
		$("#selected-projeto-escala-troca").change(function() {
			preencherUsuarioPorProjetoEscalaId(this.value, '#selected-usuario-troca', null, true)
		});

		// 1 optional
	    $("#selected-usuario-solicitacao").change(function() {
			preencherProjetoEscalaPorUsuarioId(this.value, '#selected-projeto-escala-troca', '#selected-usuario-troca', true)
		});

	});
	
	function selectReposicao(id) {
		cancelarReposicao();
		$("#id-reposicao").val($("#id-reposicao"+id).val());
		$("#btn-editar-reposicao").val("Editar");
		$("#data-reposicao").val($("#data-reposicao"+id).val());
		$("#hora-inicio-reposicao").val($("#hora-inicio-reposicao"+id).val());
		$("#hora-fim-reposicao").val($("#hora-fim-reposicao"+id).val()); 
		$("#observacao-reposicao").val($("#observacao-reposicao"+id).val());

		
		var projeto = null;
		 $("#selected-projeto-escala-troca option").each(function () { 
	         $(this).removeAttr("selected");
	         $(this).prop("selected", false);
	 		if ($(this).val() > 0 && $(this).val() == $("#selected-projeto-escala-troca"+id).val()) {
	            $(this).prop("selected", true);
	            $(this).attr("selected", "selected");
	        }
		 });
		 
		 preencherUsuarioPorProjetoEscalaId(
				 $("#selected-projeto-escala-troca"+id).val(), 
				 "#selected-usuario-troca", 
			 	 id,
			 	 true);
		 // preencherUsuarioTroca(id)
		
		 
	}
	
	function preencherUsuarioTroca(id) {
		 $("#selected-usuario-troca option").each(function () { 
	         $(this).removeAttr("selected");
	         $(this).prop("selected", false);
	 		if ($(this).val() > 0 && $(this).val() == $("#selected-usuario-troca"+id).val()) {
	            $(this).attr("selected", "selected");
		         $(this).prop("selected", true);
	    		$("#checkbox-indicar-outro-usuario-troca").prop("checked", true);
	        }
		 });		
		 
	}
	
	function editarReposicao(id) {
		if ($("#btn-editar-reposicao").val() == "Novo"){
			cancelarReposicao();
		} 

		$("#btn-editar-reposicao").hide();
		$("#btn-salvar-reposicao").show();
		 
		$("#data-reposicao").prop("disabled", false);
		$("#hora-inicio-reposicao").prop("disabled", false);
		$("#hora-fim-reposicao").prop("disabled", false);
		$("#observacao-reposicao").prop("disabled", false);

		$("#checkbox-indicar-outro-usuario-troca").prop("disabled", false);
		$("#selected-projeto-escala-troca").prop("disabled", false);
		if ($("#selected-usuario-troca").val() != null && $("#selected-usuario-troca").val() > 0) { 
			 $("#selected-usuario-troca").prop("disabled", false);
		}
		$(".btn-class-apagar-reposicao").prop("disabled", false);
	}
	
	function cancelarReposicao() { 
		$("#btn-editar-reposicao").val("Novo");
		$("#btn-editar-reposicao").show();
		$("#btn-salvar-reposicao").hide();
		$("#id-reposicao").val(0);
		$("#data-reposicao").val("");
		$("#hora-inicio-reposicao").val("");
		$("#hora-fim-reposicao").val("");
		$("#observacao-reposicao").val("");

		$("#selected-projeto-escala-troca option").each(function () { 
	         $(this).removeAttr("selected");
	         $(this).prop("selected", false);
		});

		$("#selected-usuario-troca option").each(function () { 
	         $(this).removeAttr("selected");
	         $(this).prop("selected", false);
		});

		$("#checkbox-indicar-outro-usuario-troca").prop("checked", false);
		
		$("#data-reposicao").prop("disabled", true);
		$("#hora-inicio-reposicao").prop("disabled", true);
		$("#hora-fim-reposicao").prop("disabled", true);
		$("#observacao-reposicao").prop("disabled", true);
		$("#selected-projeto-escala-troca").prop("disabled", true);
		$("#selected-usuario-troca").prop("disabled", true);


		$("#checkbox-indicar-outro-usuario-troca").prop("disabled", true);
	}

	// 2 main - escala depois usuario							       
	// 2 main - #selected-projeto-escala-troca 			#selected-usuario-troca								    
	function preencherUsuarioPorProjetoEscalaId(id, destino, reposicaoId, exceptUsuario)
    {
		var estaDesabilitado = $(destino).prop("disabled");
		$(destino).prop("disabled", true);
		var localUrl = urlBase + "ausencia/usuariosPorProjetoEscalaId/" + id;
		if (exceptUsuario) {
			var usuarioIdExcept = $("#selected-usuario-solicitacao").val();
			if (usuarioIdExcept) {
				localUrl = localUrl + "?usuarioId="+usuarioIdExcept;
			}	
		}
		
       $.ajax({
           type:'GET',
           url:localUrl,         
           dataType:'json',                    
           cache:false,
           success:function(aData) {
               $(destino).get(0).options.length = 0;
               $(destino).get(0).options[0] = new Option("", "0");      
               for(var i = 0; i < aData.length;i++) {
            	   var item = aData[i];
               		$(destino).get(0).options[$(destino).get(0).options.length] = new Option(item.nomeCompletoMatricula, item.id);                                                                                
           	   }
               
				if (reposicaoId) {
					preencherUsuarioTroca(reposicaoId);
				}
				
				if (!estaDesabilitado) {
					$(destino).prop("disabled", false);
				}
           },
           error:function(){
        	   alert("error");
				
				if (!estaDesabilitado) {
					$(destino).prop("disabled", false);
				}
			}
       });

       return false;
    }

	// 1 optional - escalas pelos usuarios e depois (escala depois usuario)
	// 1 optional - #selected-usuario-solicitacao depois    #selected-projeto-escala-troca  (depois #selected-projeto-escala-troca 			#selected-usuario-troca					) 

   function preencherProjetoEscalaPorUsuarioId(id, destino, destinoPai, exceptPrestadorEscala)
   {
	   var estaDesabilitado = $(destino).prop("disabled");
	   var estaDesabilitadoPai = $(destinoPai).prop("disabled");
	   $(destino).prop("disabled", true);
	   $(destinoPai).prop("disabled", true);
	   var localUrl = urlBase + "ausencia/projetoEscalaPorUsuarioId/" + id;

		if (exceptPrestadorEscala) {
			var prestadorEscalaIdExcept = $("#selected-projeto-escala-principal").val();
			if (prestadorEscalaIdExcept) {
				localUrl = localUrl + "?prestadorEscalaId="+prestadorEscalaIdExcept;
			}	
		}
		
       $.ajax({
           type:'GET',
           url:localUrl,         
           dataType:'json',                    
           cache:false,
           success:function(aData){ 
               $(destinoPai).get(0).options.length = 0;
               $(destino).get(0).options.length = 0;
               $(destino).get(0).options[0] = new Option("", "0");        
               for(var i = 0; i < aData.length;i++) {
            	   var item = aData[i];
	               $(destino).get(0).options[$(destino).get(0).options.length] = new Option(item.descricaoCompletaEscala, item.id);                                                                                   
           	   }
               
				if (!estaDesabilitado) {
					$(destino).prop("disabled", false);
				}
				if (!estaDesabilitadoPai) {
					$(destinoPai).prop("disabled", false);
				}
           },
           error: function() {
        	   alert("error");
				if (!estaDesabilitado) {
					$(destino).prop("disabled", false);
				}
				if (!estaDesabilitadoPai) {
					$(destinoPai).prop("disabled", false);
				}
   	   	   }
       });

       return false;
   }
	
	
	
	

	 function salvarReposicao() { 	
     	
		$("#feedback-data-reposicao").val("");
		$("#feedback-hora-inicio-reposicao").val("");
		$("#feedback-hora-fim-reposicao").val("");
		$("#feedback-selected-projeto-escala-troca").val("");
		$("#feedback-selected-usuario-troca").val("");

		$("#data-reposicao").removeClass("is-invalid");
		$("#hora-inicio-reposicao").removeClass("is-invalid");
		$("#hora-fim-reposicao").removeClass("is-invalid");
		$("#selected-projeto-escala-troca").removeClass("is-invalid");
		$("#selected-usuario-troca").removeClass("is-invalid");
		
	  	
	 	if ($("#data-reposicao").val().length <  10) { 		
	 		$("#hora-data-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-inicio-reposicao").html("Preencha o campo data reposição");
	 		return;
	 	}
	  	
	 	if ($("#hora-inicio-reposicao").val().length <  5) { 		
	 		$("#hora-inicio-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-inicio-reposicao").html("Preencha o campo hora início reposição");
	 		return;
	 	}

	 	if ($("#hora-fim-reposicao").val().length <  5) { 		
	 		$("#hora-fim-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-fim-reposicao").html("Preencha o campo hora fim reposição");
	 		return;
	 	}


	 	if (parseInt($("#hora-inicio-reposicao").val().replace(":", "")) > parseInt($("#hora-fim-reposicao").val().replace(":", ""))) { 		
	 		$("#hora-fim-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-fim-reposicao").html("A hora início deve ser menor do que a hora fim");
	 		return;
	 	}
	 	
	 	
	 	if (!/^(([0-1][0-9])||([2][0-3])):[0-5][0-9]$/.test($("#hora-inicio-reposicao").val())) {
	 		$("#hora-inicio-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-inicio-reposicao").html("Preencha o campo hora início corretamente");
	 		return;
	 	}

	 	if (!/^(([0-1][0-9])||([2][0-3])):[0-5][0-9]$/.test($("#hora-fim-reposicao").val())) {
	 		$("##hora-fim-reposicao").addClass("is-invalid");
	 		$("#feedback-hora-fim-reposicao").html("Preencha o campo hora fim corretamente");
	 		return;
	 	}



	  	if ($("#selected-projeto-escala-troca").val() == "" || 
		  	    $("#selected-projeto-escala-troca").val() == "0" ||
		  	    $("#selected-projeto-escala-troca").val() == null) {
	  		$("#selected-projeto-escala-troca").addClass("is-invalid");
	  		$("#feedback-selected-projeto-escala-troca").html("Preencha o campo escala projeto");
	  		return;
	  	}
	  	
	  	
		if ($("#checkbox-indicar-outro-usuario-troca").prop("checked") &&
				($("#selected-usuario-troca").val() == "" || 
		  	    $("#selected-usuario-troca").val() == "0" ||
		  	    $("#selected-usuario-troca").val() == null)) {
	  		$("#selected-usuario-troca").addClass("is-invalid");
	  		$("#feedback-select-usuario-troca").html("Preencha o campo usuário reposição");
	  		return;
	  	}
	  	
	  	
	  	
	 	var projetoEscalaSelected = $("#selected-projeto-escala-troca option:selected");
	 	var usuarioTrocaSelected =  $("#selected-usuario-troca option:selected");
	 	
		 	var data = $("#data-reposicao").val().split("/");
		 	console.log("#id-reposicao " +  $("#id-reposicao").val());
		 	var item = {
				id : $("#id-reposicao").val(),
				data : data[2]+"-"+data[1]+"-"+data[0], 
				dataFormatada :  $("#data-reposicao").val(), 
				horaInicio : $("#hora-inicio-reposicao").val(), 
				horaFim : $("#hora-fim-reposicao").val(),
				projetoEscalaTroca : { id: projetoEscalaSelected.val(), descricaoPrestador: projetoEscalaSelected.text() },
				indicadoOutroUsuario : $("#checkbox-indicar-outro-usuario-troca").prop("checked"),
				usuarioTroca : { id: usuarioTrocaSelected.val(), descricaoPrestador: usuarioTrocaSelected.text() },
				observacao : $("#observacao-reposicao").val()
	 	};
		 	
	 	console.log(JSON.stringify(item));
		var url1 = urlBase + "ausencia/reposicao";

    	$("#btn-salvar-reposicao").prop("disabled", true); 
		$.ajax({
			type : "POST",
			contentType : "application/json",
			accept: 'text/plain',
			url : url1,
			data : JSON.stringify(item),
			dataType: 'text',
			success : function(id) {
		
				//$("#folgaId").html(id);
				console.log("success: "+item.id);
				 
				if (item.id == null || item.id == "" || item.id == "0") {
	 				var row = 
	 					  '<tr id="reposicao' + id + '"  onclick="selectReposicao(' + id  + ')">' +
							'<td id="xdata-reposicao' + id + '" class="rowclick">' + item.dataFormatada  + '</td></td>' +
							'<td id="xhora-inicio-fim-reposicao' + id + '" class="rowclick">' + item.horaInicio + ' - ' + item.horaFim + '</td></td>' +
							'<td id="xdetalhes-reposicao' + id + '" class="rowclick" style="font-size: 10pt">' + (item.usuarioTroca.nome == null ? '' : item.usuarioTroca.nome+'<br>') + item.projetoEscalaTroca.descricaoPrestador + '</td></td>' +
							'<td>' +
								'<input type="button" onclick="apagarReposicao('+ id +')" class="btn-apagar-folga btn btn-sm btn-danger" value="apagar" />' +
								
								'<input id="id-reposicao' + id  + '" type="hidden" value="' +  id + '" />' +
								'<input id="data-reposicao' + id  + '" type="hidden" value="' + item.dataFormatada + '" />' +
								'<input id="hora-inicio-reposicao' + id  + '" type="hidden" value="' + item.horaInicio + '" />' +
								'<input id="hora-fim-reposicao' + id  + '" type="hidden" value="' + item.horaFim + '" />' +
								'<input id="observacao-reposicao' + id  + '" type="hidden" value="' + item.observacao + '" />' +
								'<input id="selected-projeto-escala-troca' + id  + '" type="hidden" value="' + item.projetoEscalaTroca.id + '" />' +
								'<input id="selected-usuario-troca' + id  + '" type="hidden" value="' + item.usuarioTroca.id + '" />' +
							'</td>'
						  '</tr>';
						  

	 			   $('#tabela-reposicao tbody tr.odd').remove();	
				   $('#tabela-reposicao tbody').prepend(row);
				   aplicarSelecionarTabela();
	    		}
			    else {
			    	$("#tabela-reposicao tbody #data-reposicao" + item.id).val(item.dataFormatada);
			    	$("#tabela-reposicao tbody #hora-inicio-reposicao" + item.id).val(item.horaInicio);
			    	$("#tabela-reposicao tbody #hora-fim-reposicao" + item.id).val(item.horaFim);
			    	$("#tabela-reposicao tbody #observacao-reposicao" + item.id).val(item.observacao);
			    	$("#tabela-reposicao tbody #selected-projeto-escala-troca" + item.id).val(item.projetoEscalaTroca.id);
			    	$("#tabela-reposicao tbody #selected-usuario-troca" + item.id).val(item.usuarioTroca.id);
			    	
			    	$("#tabela-reposicao tbody #xdata-reposicao" + item.id).val(item.dataFormatada);
			    	$("#tabela-reposicao tbody #xhora-inicio-fim-reposicao" + item.id).val(item.horaInicio + " - " + item.horaFim);
			    	$("#tabela-reposicao tbody #xdetalhes-reposicao" + item.id).val((item.usuarioTroca.nome == null ? '' : item.usuarioTroca.nome+'<br>') + item.projetoEscalaTroca.descricaoPrestador);
			    }

				cancelarReposicao();
	        	$("#btn-salvar-reposicao").prop("disabled",false);

			},
			error : function(e) {
				alert("Error!")
				console.log("ERROR: ", e);
	        	$("#btn-salvar-reposicao").prop("disabled",false);
			}
		});
	 }

