package es.ujaen.dae.excepciones;

public class UsuarioNoRegistrado extends RuntimeException {
    public UsuarioNoRegistrado(String message) {
        super(message);
    }
    public UsuarioNoRegistrado(){}
}
