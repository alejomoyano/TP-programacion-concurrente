public class EmpezarP1 implements Runnable {

    private int[][] secuencia;
    private Monitor monitor;

    public EmpezarP1(Monitor monitor){
    	secuencia = new int[17][1];
        secuencia[3][0]=1;
        this.monitor = monitor;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
    		Log.logProcesadores(secuencia);
    	}
    }
    
}