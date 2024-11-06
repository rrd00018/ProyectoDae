package es.ujaen.dae.excepciones;

public class LoginIncorrecto extends RuntimeException {
    public LoginIncorrecto(String message) {
        super(message);
    }
    public LoginIncorrecto(){}
}
