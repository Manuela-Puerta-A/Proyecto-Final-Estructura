package co.edu.uniquindio.poo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 * Al ejecutarlo se levanta el servidor y la pagina queda en
 * http://localhost:8080
 */
@SpringBootApplication
public class MonederoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonederoApplication.class, args);
    }
}
