package src;

public class Memoria {

    private int buffer;
    private int contador; //cuenta el total de tareas que se guardaron en las memorias

    public Memoria(){
        buffer=0;
        contador=0;
    }

    public int getCantActual() {return buffer;}
    public int getHistorial() {return contador;}

    synchronized public void guardar(){
        buffer++;
        contador++;
    }

    synchronized public void vaciar(){
        buffer--;
    }
}
