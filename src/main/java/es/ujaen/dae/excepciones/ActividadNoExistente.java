package es.ujaen.dae.excepciones;

public class ActividadNoExistente extends RuntimeException {
    public ActividadNoExistente(String message) {
        super(message);
    }
    public ActividadNoExistente(){}
}
