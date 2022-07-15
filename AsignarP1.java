public class AsignarP1 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private String nombre;

    private int ejecuciones;

    public AsignarP1(Monitor monitor, int ejecuciones) {
        secuencia = new int[17][1];
        secuencia[1][0] = 1;
        this.monitor = monitor;
        nombre = "AsignarP1";
        this.ejecuciones = ejecuciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void run() {
        while (Main.getTareas() < ejecuciones) {
            monitor.Disparar(secuencia);
        }
        System.out.println("Asignar p1 yo ya gane");
    }
}