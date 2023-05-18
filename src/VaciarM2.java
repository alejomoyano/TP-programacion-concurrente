package src;

public class VaciarM2 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m2;

    private int ejecuciones;
        private int counter;

    public VaciarM2(Monitor monitor, Memoria m2, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[16][0] = 1;
        this.m2 = m2;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            m2.vaciar();
            this.counter++;
        }
        System.out.println("VaciarM2: " + this.counter);
    }
}