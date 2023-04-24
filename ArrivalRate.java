public class ArrivalRate implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;
   // private String nombre; no se usa la variable

    private int ejecuciones;

    public ArrivalRate(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[0][0] = 1;
        this.monitor = monitor;
       // nombre = "ArrivalRate";
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
        }
    }

    @Override
    public String toString() {
        return "ArrivalRate";   //le estamos pasando el string directamente, si queda asi borrar la variable nombre
    }
}