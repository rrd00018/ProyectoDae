package es.ujaen.dae.excepciones;

public class FechaIncorrecta extends RuntimeException{
    public FechaIncorrecta(){
        super("La fecha introducida es incorrecta");
    }
}
