package src;

public class Tarea2P2 implements Runnable {

    private int[][] ProcesarT2P2, FinalizarT2P2;
    private Monitor monitor;


    private int ejecuciones;
        private int counter;

    public Tarea2P2(Monitor monitor, int ejecuciones) {
        ProcesarT2P2 = new int[17][1];
        ProcesarT2P2[14][0] = 1;
        FinalizarT2P2 = new int[17][1];
        FinalizarT2P2[8][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(ProcesarT2P2);
            monitor.Disparar(FinalizarT2P2);
            Main.sumarTareas();
            this.counter++;
        }
        System.out.println("TareaP2: " + this.counter);
    }
}