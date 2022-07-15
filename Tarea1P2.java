public class Tarea1P2 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;

    public Tarea1P2(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[6][0] = 1;

        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
            Main.sumarTareas();
            Log.logTareas(secuencia);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}