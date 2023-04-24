public class Tarea2P1 implements Runnable {

    private int[][] ProcesarT2P1, FinalizarT2P1;
    private Monitor monitor;

    private int ejecuciones;

    public Tarea2P1(Monitor monitor, int ejecuciones) {
        ProcesarT2P1 = new int[17][1];
        ProcesarT2P1[13][0] = 1;
        FinalizarT2P1 = new int[17][1];
        FinalizarT2P1[7][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(ProcesarT2P1);
            monitor.Disparar(FinalizarT2P1);
            Main.sumarTareas();
        }
    }
}