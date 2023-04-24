package src;

public class EmpezarP1 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;

    public EmpezarP1(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[3][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            Log.logProcesadores(secuencia);
        }
    }

}