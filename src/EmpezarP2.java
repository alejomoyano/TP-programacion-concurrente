package src;

public class EmpezarP2 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;
    private int counter;

    public EmpezarP2(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[4][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            Log.logProcesadores(secuencia);
            this.counter++;
        }
        System.out.println("EmpezarP2: " + this.counter);
    }

}