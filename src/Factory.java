package src;

import java.util.concurrent.ThreadFactory;

/**
 * Implementa ThreadFactory que es la clase que nos permite crear hilos
 * on demand.
 */
public class Factory implements ThreadFactory{
    
    private int counter; // contador de hilos

    public Factory(){
        this.counter=0;
    }

    /**
     * Metodo donde creamos un hilo y le damos una tarea a realizar
     * @param task: Runnable que realizara el hilo
     * @return el hilo creado (Thread)
     */
    public Thread newThread(Runnable task){
            Thread hilo = new Thread(task);
            hilo.setName("Thread T"+counter);
            counter++;
            return hilo;
    }

}