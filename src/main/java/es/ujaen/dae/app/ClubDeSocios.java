package es.ujaen.dae.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"es.ujaen.dae.servicios","es.ujaen.dae.repositorios", "es.ujaen.dae.rest", "es.ujaen.dae.seguridad"})
@EntityScan(basePackages="es.ujaen.dae.entidades")
@EnableScheduling
public class ClubDeSocios {
    public static void main(String[] args) {
        SpringApplication.run(ClubDeSocios.class);
    }
}