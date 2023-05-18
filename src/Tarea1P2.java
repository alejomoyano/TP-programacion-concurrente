package src;

public class Tarea1P2 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;
        private int counter;

    public Tarea1P2(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[6][0] = 1;

        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            Main.sumarTareas();
            Log.logTareas(secuencia);
            this.counter++;
        }
        System.out.println("Tarea1P2: " + this.counter);
    }

}