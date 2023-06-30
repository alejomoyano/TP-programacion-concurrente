package src;

public class Task implements Runnable{

    private final int transicion, ejecuciones;
    private final Monitor monitor;
    private int segunda_transicion;
    private Memoria memoria;

    public Task(Monitor monitor, int ejecuciones,  int transicion, int segunda_transicion) {
        this.transicion = transicion;
        this.segunda_transicion = segunda_transicion;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }
    public Task(Monitor monitor, Memoria memoria, int ejecuciones, int transicion) {
        this.memoria = memoria;
        this.transicion = transicion;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;
    }
    public Task(Monitor monitor, int ejecuciones,  int transicion) {
        this.transicion = transicion;
        this.monitor = monitor;
        this.ejecuciones = ejecuciones;

    }

    private void after_shoot_tasks(){
        if(transicion == 5 || transicion == 6 ||transicion == 13 || transicion == 14)
            Main.sumarTareas();
        if(transicion >= 9 && transicion <= 12)
            memoria.guardar();
        if(transicion == 15 || transicion == 16){
//        	System.out.println("Soy: "+Thread.currentThread().getName()+". Voy a vaciar memoria.");
            memoria.vaciar();
        }
        Log.loggeo(transicion);
    }

    public void run() {
        //while (Main.getTareas() < ejecuciones) {
        for(int i = 0; i < ejecuciones; i++ ){
            monitor.Disparar(this.transicion);
            if(transicion == 13 || transicion == 14)
                monitor.Disparar(this.segunda_transicion);
            after_shoot_tasks();
            if(Main.getTareas() == ejecuciones){
                break;
            }
        }
    }

}
