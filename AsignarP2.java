public class AsignarP2 implements Runnable {
    private int[][] secuencia;
    private Monitor monitor;
    private String nombre;
    
    public AsignarP2(Monitor monitor){
    	secuencia = new int[17][1];
        secuencia[2][0]=1;
        this.monitor = monitor;
        nombre= "AsignarP2";
    }
    
    public String getNombre() {
    	return nombre;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
    	}
    }
}