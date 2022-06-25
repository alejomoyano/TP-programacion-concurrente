import java.util.concurrent.Semaphore;


public class Monitor {
	
	private RdP RedPetri;
    public Semaphore mutex;
    private Politicas politica;
    private Colas colas; 

	public Monitor(RdP red, Politicas politica,Colas cola){
        this.politica = politica;
		RedPetri=red;
		this.colas=cola;
		mutex= new Semaphore(1);//con o sin fairness?
	}
	
	public void Disparar (int [][] secuencia) {

        try {
          //  System.out.println(" Realizando acquire - Hilo: "+Thread.currentThread()+" - Hilos en cola: " + mutex.getQueueLength());
  
            mutex.acquire();

            // --------------
            System.out.println(" Acquire realizado por: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits());
            // -------------

//            
            while(!RedPetri.EcuacionEstado(secuencia,true)){
             //   System.out.println(" No pude disparar, me voy a dormir: "+Thread.currentThread());
            	
                
                if(RedPetri.getDormirse() && RedPetri.esTemporal(secuencia)) {		 
                	RdP.setDormirse(false);	//bajo el flag, borro el indicador para el proximo hilo
                	 System.out.println(" No pude disparar temporal, me voy a dormir por: "+RedPetri.getSleepTime()+" ms.Thread:"+Thread.currentThread());
                	try{
                		mutex.release();
        				Thread.sleep(RedPetri.getSleepTime());
        			}
        			catch (InterruptedException e){e.printStackTrace();}
        			try {
        				mutex.acquire();
        			}
        			catch (InterruptedException e){e.printStackTrace();}
                }
                else {
                	System.out.println(" No pude disparar me voy a mi cola. "+Thread.currentThread());
                	mutex.release();
                	colas.setDormirse(secuencia);
                }
            }
        // ya disparo
            System.out.println(" He disparado: "+Thread.currentThread());
            
            
            
            int[][] and = RdP.calcularAND(RedPetri.getSensibilizado(),colas.getDormidos());
            
            Log.Tlogger(secuencia);
                //System.out.println("Hilo: "+Thread.currentThread()+"Valor de And["+i+"][0]: "+and[i][0]);}
                
            int m=0;
            for(int i=0;i<and.length;i++) {
                if(and[i][0]==1) {
                    m++;
                }
            }
            if(m>0) {
                 //  System.out.println("Hilo: "+Thread.currentThread()+"entre a if con m: "+m+" voy a despertar");

                colas.despertar(and,this.politica);
                return;

            }
            mutex.release();
            System.out.println(" Me voy del monitor y hago Realese desde: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits() + " - Hilos esperando: " + mutex.getQueueLength());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

    }

    
}

