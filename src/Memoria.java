package src;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Memoria {

    private int buffer;
    private int contador; //cuenta el total de tareas que se guardaron en las memorias
    private final Object guardar_lock;
    private final Object vaciar_lock;

    public Memoria(){
        buffer=0;
        contador=0;
        guardar_lock = new Object();
        vaciar_lock = new Object();
    }

    public int getCantActual() {return buffer;}
    public int getHistorial() {return contador;}

    public void guardar(){
        synchronized (guardar_lock){
            buffer++;
            contador++;
        }

    }

    public void vaciar(){
        synchronized (vaciar_lock){
            buffer--;
        }
    }
}