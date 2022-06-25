public class Tarea2P1 implements Runnable{
    
    private int[][] ProcesarT2P1, FinalizarT2P1;
    private Monitor monitor;

    public Tarea2P1(Monitor monitor){
        ProcesarT2P1 = new int[17][1];
        ProcesarT2P1[13][0]=1;
        FinalizarT2P1 = new int[17][1];
        FinalizarT2P1[7][0]=1;
        this.monitor = monitor;
    }
    
    public void run(){
    	while(Main.getTareas()<1000) {
    		monitor.Disparar(ProcesarT2P1);
    		try{Thread.sleep(150);}
    		catch (InterruptedException e){e.printStackTrace();}
    		monitor.Disparar(FinalizarT2P1);
    		Main.sumarTareas();
    		try{Thread.sleep(100);}
    		catch (InterruptedException e){e.printStackTrace();}
    	}
    }
}