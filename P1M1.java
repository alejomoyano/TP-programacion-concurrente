public class P1M1 implements Runnable{
    private int[][] secuencia;
    private Monitor monitor;
    private Memoria m1;

    public P1M1(Monitor monitor, Memoria m1){
        secuencia = new int[17][1];
        secuencia[9][0]=1;
        this.m1 = m1;
        this.monitor = monitor;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(secuencia);
    		m1.guardar();
    	}
    }
}