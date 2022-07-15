import java.util.Arrays;
import java.util.concurrent.Semaphore;


public class Monitor {
	
	private RdP RP;
    public Semaphore mutex;
    private Politicas politica;
    private Colas colas; 

	public Monitor(RdP red, Politicas politica,Colas colas){
        this.politica = politica;
        RP = red;
		this.colas = colas;
		mutex = new Semaphore(1);//con o sin fairness?
	}
	
	public void Disparar (int [][] secuencia) {

        boolean disparar = false;

        try {
          //  System.out.println(" Realizando acquire - Hilo: "+Thread.currentThread()+" - Hilos en cola: " + mutex.getQueueLength());

            // intentamos adquirir permisos para entrar el monitor. Si no hay nadie entonces entro, sino quedo bloqueado esperando?
            mutex.acquire();


            System.out.println(" Acquire realizado por: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits());



            disparar = RdP.shootIfWeCan(secuencia); // intentamos disparar

            // si noo puede disparar entonces
            while (!disparar){
                //   System.out.println(" No pude disparar, me voy a dormir: "+Thread.currentThread());

                //  si debe dormirse y es temporal entra
                if(RP.getDormirse() && RP.esTemporal(secuencia)) {
                    RdP.setDormirse(false);	//bajo el flag, borro el indicador para el proximo hilo
                    System.out.println(" No pude disparar temporal, me voy a dormir por: "+RP.getSleepTime()+" ms.Thread:"+Thread.currentThread());
                    try{
                        // suelta el mutex ya que debe dormirse
                        mutex.release();
                        Thread.sleep(RP.getSleepTime());
                    }
                    catch (InterruptedException exception){
                        exception.printStackTrace();
                    }
                    try {
                        // una vez que se levanta debe intentar adquirir el monitor de nuevo
                        mutex.acquire();
                    }
                    catch (InterruptedException exception){
                        exception.printStackTrace();
                    }
                }
                // si no es temporal la transicion o si es temporal pero estamos despues del beta entramos aca
                else {
                    System.out.println(" No pude disparar me voy a mi cola. "+Thread.currentThread());
                    mutex.release();
                    colas.setDormirse(secuencia);
                    System.out.println("Soy: " + Thread.currentThread() + " voy a intentar disparar de nuevo, me sacaron de la cola.");

                }
                // cuando se despierte va a volver a preguntar si puede disparar
                disparar = RdP.shootIfWeCan(secuencia);
            }



    /*
//           // si no se pudo disparar entra
            while(!RedPetri.EcuacionEstado(secuencia)){
             //   System.out.println(" No pude disparar, me voy a dormir: "+Thread.currentThread());

                //  pregunta si se tiene que dormir y si es temporal
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
                // cuando se despierte va a volver a preguntar si puede disparar
            }
            */
        // ya disparo
            System.out.println(" He disparado: "+Thread.currentThread());

            // debemos dejarle el mutex a alguien, tienen mas prioridad aquellos que ya entraron al monitor
            // y estan esperando en las colas
            
            // dormidos que estan sensibilizados
            int[][] sensibilizadas = Utils.calcularAND(RP.getSensibilizado(),colas.getDormidos());

            // guardamos en el log de transiciones la que fue disparada
            Log.Tlogger(secuencia);
                //System.out.println("Hilo: "+Thread.currentThread()+"Valor de And["+i+"][0]: "+and[i][0]);}

            // obtenemos que cantidad de transiciones que estan sensibilizadas
            int cantDormidosSens=0;
            for (int[] sensibilizada : sensibilizadas) {
                if (sensibilizada[0] == 1) {
                    cantDormidosSens++;
                }
            }
            // si hay algunas sensi debemos despertar una
            if(cantDormidosSens>0) {
               System.out.println("Hilo: " + Thread.currentThread() + ". Hay " + cantDormidosSens + " sensibilizadas dormida con hilos esperando");
                colas.signal(sensibilizadas,this.politica,cantDormidosSens);
                return;
            }
            mutex.release();
            System.out.println(" Me voy del monitor y hago Release desde: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits() + " - Hilos esperando: " + mutex.getQueueLength());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

    }

    
}
