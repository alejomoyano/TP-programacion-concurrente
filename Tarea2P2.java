public class Tarea2P2 implements Runnable {

    private int[][] ProcesarT2P2, FinalizarT2P2;
    private Monitor monitor;


    private int ejecuciones;

    public Tarea2P2(Monitor monitor, int ejecuciones) {
        ProcesarT2P2 = new int[17][1];
        ProcesarT2P2[14][0] = 1;
        FinalizarT2P2 = new int[17][1];
        FinalizarT2P2[8][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(ProcesarT2P2);
            /*try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            monitor.Disparar(FinalizarT2P2);
            Main.sumarTareas();
            /*try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
}