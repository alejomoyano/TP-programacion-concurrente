package src;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Entonces la pool va a tener listos 15 hilos para ser ejecutados.
 * Estos quedan "esperando" a que le demos la tarea a realizar.
 * Cuando hagamos execute(Runnable), el executor busca un hilo y lo
 * pone a ejecutar.
 */



public class ThreadExecutor {

    private final ThreadPoolExecutor executor;

    /**
     * Constructor donde definimos a la pool para que cree n hilos con un
     * factory definido
     * @param factory: Factory que le indica a la pool como va a crear los hilos
     */
    public ThreadExecutor(Factory factory){
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15,factory);
    }

    /**
     * Ejecutamos un hilo
     * @param task: Runnable que queremos que el hilo ejecute
     */
    public void ejecutar(Runnable task){
        executor.execute(task);
    }
}