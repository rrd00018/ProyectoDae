package es.ujaen.dae.excepciones;

public class ActividadYaCreada extends RuntimeException {
    public ActividadYaCreada(String message) {
        super(message);
    }
    public ActividadYaCreada(){}
}
