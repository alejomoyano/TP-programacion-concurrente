public class AsignarP1 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private String nombre;

    public AsignarP1(Monitor monitor){
    	secuencia = new int[17][1];
        secuencia[1][0]=1;
        this.monitor = monitor;
        nombre= "AsignarP1";
    }
    
    public String getNombre() {
    	return nombre;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
    	}
    	System.out.println("Asignar p1 yo ya gane");
    }
}