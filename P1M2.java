public class P1M2 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m2;

    private int ejecuciones;

    public P1M2(Monitor monitor, Memoria m2, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[10][0] = 1;
        this.m2 = m2;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            m2.guardar();
        }
    }
}