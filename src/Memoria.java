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

    public synchronized void guardar(){
            buffer++;
            contador++;
    }

    public synchronized void vaciar(){
            buffer--;
    }
}