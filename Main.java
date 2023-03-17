import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// import sun.management.Sensor;

public class Main {

    private static int tareas;

    public static void main(String[] args) {

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
        int ejecuciones2 = 994; // agregue esta vvariable para crear menos tareas y probar que pasa.
        // claramente se queda el hilo main dando vueltas porque espera que se hagan 1000 ejecuciones pero hay 995 tareas
        // tire el script de las invariantes y sobran transiciones asi que joya.
        // lo que si, cuando le di que haga 995 hizo 998. O sea hace mas tareas de las que le pido.
        // todavia no le di vueltas al tema, seguro algo se me esta pasando.

        executor.ejecutar(new ArrivalRate(monitor,ejecuciones));
        executor.ejecutar(new AsignarP1(monitor,ejecuciones));
        executor.ejecutar(new AsignarP2(monitor,ejecuciones));
        executor.ejecutar(new EmpezarP1(monitor,ejecuciones));
        executor.ejecutar(new EmpezarP2(monitor,ejecuciones));
        executor.ejecutar(new Tarea1P1(monitor,ejecuciones2));
        executor.ejecutar(new Tarea1P2(monitor,ejecuciones2));
        executor.ejecutar(new P1M1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new P1M2(monitor, memoria2,ejecuciones));
        executor.ejecutar(new P2M1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new P2M2(monitor, memoria2,ejecuciones));
        executor.ejecutar(new Tarea2P1(monitor,ejecuciones2));
        executor.ejecutar(new Tarea2P2(monitor,ejecuciones2));
        executor.ejecutar(new VaciarM1(monitor, memoria1,ejecuciones));
        executor.ejecutar(new VaciarM2(monitor, memoria2,ejecuciones));

		/*try {
            TimeUnit.MILLISECONDS.sleep(5000);
        }
        catch (Exception e) {
            System.out.println("Error al dormir hilo Main");
        }*/


        while (tareas <= ejecuciones) {
            System.out.println("while Tareas realizadas: " + tareas);
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (Exception e) {
                System.out.println("Error al dormir hilo Main");
            }

            if (tareas == ejecuciones ){
                log.logger();
                System.out.println("Log ejecutado");
                System.out.println("Memoria1 cantidad actual: " + memoria1.getCantActual());
                System.out.println("Memoria2 cantidad actual: " + memoria2.getCantActual());
                System.out.println("Guardados en memoria1: " + memoria1.getHistorial());
                System.out.println("Guardados en memoria2: " + memoria2.getHistorial());

                System.out.println("Tareas realizadas: " + tareas);
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
