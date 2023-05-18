package src;

public class AsignarP2 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;
    private int counter;

    public AsignarP2(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[2][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            this.counter++;
        }
        System.out.println("AsignarP2: " + this.counter);
    }
}