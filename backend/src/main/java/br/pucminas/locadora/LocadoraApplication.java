package br.pucminas.locadora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocadoraApplication {

	public static void main(String[] args) {
		// Postgres/Supabase nao aceitam alias legado de fuso (ex.: Brazil/East)
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Sao_Paulo"));
		SpringApplication.run(LocadoraApplication.class, args);
	}

}
