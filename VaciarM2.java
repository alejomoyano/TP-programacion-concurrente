public class VaciarM2 implements Runnable{
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m2;

    public VaciarM2(Monitor monitor, Memoria m2){
        secuencia = new int[17][1];
        secuencia[16][0]=1;
        this.m2= m2;
        this.monitor = monitor;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
    		m2.vaciar();
    	}
    }
}