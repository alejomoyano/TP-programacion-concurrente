package src;

public class ArrivalRate implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;

    public ArrivalRate(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[0][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }

    public void run() {
        for (int i = 0; i < ejecuciones; i++) {
            monitor.Disparar(secuencia);
        }
    }

    @Override
    public String toString() {
        return "ArrivalRate";
    }
}