package com.packageIxia.SistemaControleEscala.Helper;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

public class Utilities { 	
	
	public static <E> List<E> toList(Iterable<E> iterable) {		
		if(iterable instanceof List) {
			return (List<E>) iterable;
		}
		 
		ArrayList<E> list = new ArrayList<E>();
		if(iterable != null) {
			for(E e: iterable) {
				list.add(e);
			}
		}
		
		return list;
	}

	public static String getAllErrosBindingResult(BindingResult result) {
		return result.getFieldErrors().stream().map(x->replaceIdTag(x.getField())).collect(Collectors.joining(", "));
	}
	
	private static String replaceIdTag(String value) {
		value = (value +"-").replaceAll("Id-", "").replaceAll("-", "");
		value = String.join(" ", value.split("(?<=\\p{Ll})(?=\\p{Lu})|(?<=\\p{L})(?=\\p{Lu}\\p{Ll})"));
		return  value.toLowerCase();
	}
	
	public static String dateToString(Date date) {
		System.out.println(date);
		System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(date));
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}
	
	public static boolean validarHora(String hora) {
		String rex = "^(([0-1][0-9])||([2][0-3])):[0-5][0-9]$";
		return hora == null || hora == "" ? false : hora.matches(rex);
	}
	
	public static int horaToInt(String hora) {
		return Integer.parseInt(hora.replace(":", ""));
	}
	
	public static String horaDiff(String hora1, String hora2) {
		if (!validarHora(hora1) || !validarHora(hora1)) {
			return "";
		}
		
		String[] hrs1 = hora1.split(":");
		double hr1 = Integer.parseInt(hrs1[0]) + ((Integer.parseInt(hrs1[1])/0.6)/100);

		String[] hrs2 = hora2.split(":");
		double hr2 = Double.parseDouble(hrs2[0]) + ((Double.parseDouble(hrs2[1])/0.6)/100);
		double diff = hr2 - hr1;
		if (diff == 0) {
			return "";
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(hr2 - hr1) + (diff > 1 ? "hrs" : "hr");
	}
	
	public static boolean dataEstaEntreDiasDaSemana(LocalDate dataInicio, int diaSemanaInicio, int diaSemanaFim) {
		return dataEstaEntreDiasDaSemana(dataInicio, diaSemanaInicio, diaSemanaFim, true);
	}
	
	public static boolean dataEstaEntreDiasDaSemana(LocalDate data, int diaSemanaInicio, int diaSemanaFim, boolean ajuste) {
		if (data == null) {
			return true;
		}
		
		List<Integer> diasSemanas = new ArrayList<Integer>();
		if (ajuste) {
			diaSemanaInicio = diaSemanaInicio == 1 ? 7 : diaSemanaInicio - 1;
			diaSemanaFim = diaSemanaFim == 1 ? 7 : diaSemanaFim - 1;
		}
		
		while (diaSemanaInicio <= diaSemanaFim) {
			diasSemanas.add(diaSemanaInicio);
			diaSemanaInicio = diaSemanaInicio+1;
		}

		if (diasSemanas.stream().anyMatch(x->x==data.getDayOfWeek().getValue())) {
			return true;
		}
		
		return false;
	}
	
}
