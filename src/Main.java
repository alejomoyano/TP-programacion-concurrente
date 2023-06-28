package src;

import src.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// import sun.management.Sensor;

public class Main {

    private static int tareas;

    public static void main(String[] args) {

        RdP RedDePetri = new RdP();
        Colas colas = new Colas();
        Politicas politicas = new Politicas(RedDePetri);
        Memoria memoria_uno = new Memoria();
        Memoria memoria_dos = new Memoria();
        Monitor monitor = new Monitor(RedDePetri, politicas, colas, memoria_uno, memoria_dos);
        Factory factory = new Factory();
        ThreadExecutor executor = new ThreadExecutor(factory);
        Log log = new Log(memoria_uno, memoria_dos);

        int ejecuciones = 1000; // cantidad de tiradas

        long Tiempoinicio = System.currentTimeMillis();

        executor.ejecutar(new Task(monitor,ejecuciones,0));
        executor.ejecutar(new Task(monitor,ejecuciones,1));
        executor.ejecutar(new Task(monitor,ejecuciones,2));
        executor.ejecutar(new Task(monitor,ejecuciones,3));
        executor.ejecutar(new Task(monitor,ejecuciones,4));
        executor.ejecutar(new Task(monitor,ejecuciones,5));
        executor.ejecutar(new Task(monitor,ejecuciones,6));
        executor.ejecutar(new Task(monitor,ejecuciones,13,7));
        executor.ejecutar(new Task(monitor,ejecuciones,14,8));
        executor.ejecutar(new Task(monitor,memoria_uno,ejecuciones,9));
        executor.ejecutar(new Task(monitor,memoria_dos,ejecuciones,10));
        executor.ejecutar(new Task(monitor,memoria_uno,ejecuciones,11));
        executor.ejecutar(new Task(monitor,memoria_dos,ejecuciones,12));
        executor.ejecutar(new Task(monitor,memoria_uno,ejecuciones,15));
        executor.ejecutar(new Task(monitor,memoria_dos,ejecuciones,16));


        while (tareas < ejecuciones) {
            System.out.println("Procesando...Tareas realizadas: " + tareas);
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (Exception e) {
                System.out.println("Error al dormir hilo Main");
            }

        }

        executor.shutdown();
        log.logger();
        System.out.println("");
        System.out.println("------------Programa finalizado------------");
        System.out.println("Memoria1 cantidad actual: " + memoria_uno.getCantActual());
        System.out.println("Memoria2 cantidad actual: " + memoria_dos.getCantActual());
        System.out.println("Guardados en memoria1: " + memoria_uno.getHistorial());
        System.out.println("Guardados en memoria2: " + memoria_dos.getHistorial());

        System.out.println("Tareas realizadas: " + tareas);
        System.out.println("Tiempo de ejecucion: "+ ((System.currentTimeMillis()-Tiempoinicio)/1000)+" segundos.");
        System.out.println("-------------Marcado-------------");
        Utils.imprimirMatriz2D(RedDePetri.getMarcado());
        System.out.println("---------------------------------");
        System.out.println("Ejecucion terminada...");
        System.exit(0); // Para que el programa realmente termine (se cierra la JVM). Sino siguen corriendo los hilos
                        // ya que son independientes del main y siempre terminan las t-invariantes completas vaciando todas las memorias
    }

    public synchronized static void sumarTareas() {
            tareas++;
//        System.out.println("Tareas realizadas: " + tareas);
    }

    public synchronized static int getTareas() {
            return tareas;
    }
}
