package src;

public class ArrivalRate implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    private int ejecuciones;
    private int counter;

    public ArrivalRate(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[0][0] = 1;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
        this.counter = 0;
    }

    public void run() {
        for (int i = 0; i < ejecuciones; i++) {
            monitor.Disparar(secuencia);
            this.counter++;
        }
        System.out.println("ArrivalRate: " + this.counter);
    }

    @Override
    public String toString() {
        return "ArrivalRate";
    }
}