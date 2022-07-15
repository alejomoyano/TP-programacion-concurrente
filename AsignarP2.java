public class AsignarP2 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private String nombre;

    private int ejecuciones;

    public AsignarP2(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[2][0] = 1;
        this.monitor = monitor;
        nombre = "AsignarP2";
        this.ejecuciones = ejecuciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
        }
    }
}