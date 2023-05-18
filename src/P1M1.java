package src;

public class P1M1 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m1;

    private int ejecuciones;
        private int counter;

    public P1M1(Monitor monitor, Memoria m1, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[9][0] = 1;
        this.m1 = m1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            m1.guardar();
            this.counter++;
        }
        System.out.println("P1M1: " + this.counter);
    }
}