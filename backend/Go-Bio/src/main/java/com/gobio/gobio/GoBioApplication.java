package com.gobio.gobio;

import io.vertx.core.Vertx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class GoBioApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoBioApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle(){
		Vertx vertx=Vertx.vertx();
		vertx.deployVerticle( new ServerVerticle());
	}

}
