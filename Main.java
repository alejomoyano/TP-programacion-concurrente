import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// import sun.management.Sensor;

public class Main {

    private static int tareas;

    public static void main(String[] args) {

        long Tiempoinicio = System.currentTimeMillis();
        RdP RedDePetri = new RdP();
        Colas colas = new Colas();
        Politicas politicas = new Politicas(RedDePetri);
        Monitor monitor = new Monitor(RedDePetri, politicas, colas);
        Factory factory = new Factory();
        ThreadExecutor executor = new ThreadExecutor(factory);
        Memoria memoria1 = new Memoria();
        Memoria memoria2 = new Memoria();
        Log log = new Log(memoria1, memoria2);

        int ejecuciones = 1000; // cantidad de tiradas

        executor.ejecutar(new ArrivalRate(monitor,ejecuciones));    //T0
        executor.ejecutar(new AsignarP1(monitor,ejecuciones));      //T1
        executor.ejecutar(new AsignarP2(monitor,ejecuciones));      //T2 etc...
        executor.ejecutar(new EmpezarP1(monitor,ejecuciones));      // si, yo le cambie el orden para ver que pasaba
        executor.ejecutar(new EmpezarP2(monitor,ejecuciones));
        executor.ejecutar(new Tarea1P1(monitor,ejecuciones));
        executor.ejecutar(new Tarea1P2(monitor,ejecuciones));
        executor.ejecutar(new Tarea2P1(monitor,ejecuciones));
        executor.ejecutar(new Tarea2P2(monitor,ejecuciones));
        executor.ejecutar(new P1M1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new P1M2(monitor, memoria2,ejecuciones));
        executor.ejecutar(new P2M1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new P2M2(monitor, memoria2,ejecuciones));
        executor.ejecutar(new VaciarM1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new VaciarM2(monitor, memoria2,ejecuciones));
        


        while (tareas <= ejecuciones) {
//            System.out.println("while Tareas realizadas: " + tareas);
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (Exception e) {
                System.out.println("Error al dormir hilo Main");
            }

            if (tareas == ejecuciones ){
                log.logger();
                System.out.println("");
                System.out.println("------------Programa finalizado------------");
                System.out.println("Log ejecutado");
                System.out.println("Memoria1 cantidad actual: " + memoria1.getCantActual());
                System.out.println("Memoria2 cantidad actual: " + memoria2.getCantActual());
                System.out.println("Guardados en memoria1: " + memoria1.getHistorial());
                System.out.println("Guardados en memoria2: " + memoria2.getHistorial());

                System.out.println("Tareas realizadas: " + tareas);
                System.out.println("Tiempo de ejecucion: "+ ((System.currentTimeMillis()-Tiempoinicio)/1000)+" segundos.");
                System.out.println("-------------Marcado-------------");
                Utils.imprimirMatriz2D(RedDePetri.getMarcado());
                break;
            }
        }

        System.out.println("salimos tocando...");
    }

    public static synchronized void sumarTareas() {
        tareas++;
        System.out.println("Tareas realizadas: " + tareas);
    }

    public static int getTareas() {
        return tareas;
    }
}
