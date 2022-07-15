public class VaciarM1 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m1;

    private int ejecuciones;

    public VaciarM1(Monitor monitor, Memoria m1, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[15][0] = 1;
        this.m1 = m1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            m1.vaciar();
        }
    }
}