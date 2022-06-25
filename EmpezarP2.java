public class EmpezarP2 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    public EmpezarP2(Monitor monitor){
    	secuencia = new int[17][1];
        secuencia[4][0]=1;
        this.monitor = monitor;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
            Log.logProcesadores(secuencia);
    	}
    }
    
}