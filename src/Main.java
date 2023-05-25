package src;

import src.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// import sun.management.Sensor;

public class Main {

    private static int tareas;

    private static Object get_tareas_lock;
    private static Object tareas_lock;


    public static void main(String[] args) {

        get_tareas_lock = new Object();
        tareas_lock = new Object();

        long Tiempoinicio = System.currentTimeMillis();
        RdP RedDePetri = new RdP();
        Colas colas = new Colas();
        Politicas politicas = new Politicas(RedDePetri);
        Memoria memoria_uno = new Memoria();
        Memoria memoria_dos = new Memoria();
        Monitor monitor = new Monitor(RedDePetri, politicas, colas,memoria_uno,memoria_dos);
        Factory factory = new Factory();
        ThreadExecutor executor = new ThreadExecutor(factory);
        Log log = new Log(memoria_uno, memoria_dos);

        int ejecuciones = 1000; // cantidad de tiradas

//        executor.ejecutar(new ArrivalRate(monitor,ejecuciones));    //T0
//        executor.ejecutar(new AsignarP1(monitor,ejecuciones));      //T1
//        executor.ejecutar(new AsignarP2(monitor,ejecuciones));      //T2 etc...
//        executor.ejecutar(new EmpezarP1(monitor,ejecuciones));
//        executor.ejecutar(new EmpezarP2(monitor,ejecuciones));
//        executor.ejecutar(new Tarea1P1(monitor,ejecuciones));
//        executor.ejecutar(new Tarea1P2(monitor,ejecuciones));
//        executor.ejecutar(new Tarea2P1(monitor,ejecuciones));
//        executor.ejecutar(new Tarea2P2(monitor,ejecuciones));
//        executor.ejecutar(new P1M1(monitor, memoria1,ejecuciones));
//        executor.ejecutar(new P1M2(monitor, memoria2,ejecuciones));
//        executor.ejecutar(new P2M1(monitor, memoria1,ejecuciones));
//        executor.ejecutar(new P2M2(monitor, memoria2,ejecuciones));
//        executor.ejecutar(new VaciarM1(monitor, memoria1,ejecuciones));
//        executor.ejecutar(new VaciarM2(monitor, memoria2,ejecuciones));
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
    }

    public static void sumarTareas() {
        synchronized (tareas_lock){
            tareas++;
        }
//        System.out.println("Tareas realizadas: " + tareas);
    }

    public static int getTareas() {
        synchronized (get_tareas_lock){
            return tareas;
        }
    }
}
