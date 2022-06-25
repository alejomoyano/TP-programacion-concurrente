public class ArrivalRate implements Runnable {
    
    private int[][] secuencia;
    private Monitor monitor;
    private String nombre;
    
    public ArrivalRate(Monitor monitor){
    	secuencia = new int[17][1];
        secuencia[0][0]=1;
        this.monitor = monitor;
        nombre="ArrivalRate";
    }
    
    public void run(){
    	for(int i=0;i<4;i++) {
    		//System.out.println("Entro al for, proximo a disparar");
    		monitor.Disparar(secuencia);
    		//System.out.println("T0 ya dispare");

        }
    	System.out.println("T0, yo ya gane");
    }
    
    @Override
    public String toString() {
    	return "ArrivalRate";
    }
}