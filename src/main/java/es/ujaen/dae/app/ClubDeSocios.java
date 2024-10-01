package es.ujaen.dae.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages="es.ujaen.dae.entidades")
public class ClubDeSocios {
    public static void main(String[] args) {
        SpringApplication.run(ClubDeSocios.class);
    }
}