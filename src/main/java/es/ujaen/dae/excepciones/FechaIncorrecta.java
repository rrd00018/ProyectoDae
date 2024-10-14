package es.ujaen.dae.excepciones;

public class FechaIncorrecta extends RuntimeException{
    public FechaIncorrecta(String msg){
        super(msg);
    }
    public FechaIncorrecta(){}
}
