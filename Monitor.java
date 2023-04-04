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
		mutex = new Semaphore(1,true);
    }

	public void Disparar (int [][] secuencia) {

        boolean disparar = false;

        try {
          //  System.out.println(" Realizando acquire - Hilo: "+Thread.currentThread()+" - Hilos en cola: " + mutex.getQueueLength());

            // intentamos adquirir permisos para entrar el monitor. Si no hay nadie entonces entro, sino quedo bloqueado esperando
            mutex.acquire();

            System.out.println(" Acquire realizado por: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits());

            disparar = RdP.shootIfWeCan(secuencia); // intentamos disparar

            // si no puede disparar entonces
            while (!disparar){
                   System.out.println(" No pude disparar, me voy a dormir: "+Thread.currentThread());

                //  si debe dormirse y es temporal entra
                if(RP.getDormirse() && RP.esTemporal(secuencia)) {
                    RdP.setDormirse(false);	//bajo el flag, borro el indicador para el proximo hilo
                    System.out.println(" No pude disparar temporal, me voy a dormir por: "+RP.getSleepTime()+" ms.Thread:"+Thread.currentThread());
                    try{
                        // suelta el mutex ya que debe dormirse
                        mutex.release();
                        Thread.sleep(RP.getSleepTime()); // se duerme el tiempo que necesite para llegar dentro de la ventana luego.
                    }
                    catch (InterruptedException exception){
                        exception.printStackTrace();
                    }
                    try {
                        // una vez que se levanta debe intentar adquirir el monitor de nuevo
                        mutex.acquire();
                        // debe dormirse por tiempo, no tiene q esperar a q alguien lo levante de las colas de transicion, sino se le pasa la ventana
                    }
                    catch (InterruptedException exception){
                        exception.printStackTrace();
                    }
                }
                // si no es temporal la transicion o si es temporal pero estamos después del beta entramos aca
                else {
                   System.out.println(" No pude disparar me voy a mi cola. "+Thread.currentThread());
                    mutex.release();
                    colas.setDormirse(secuencia);
                    System.out.println("Soy: " + Thread.currentThread() + " voy a intentar disparar de nuevo, me sacaron de la cola.");

                }
                // cuando se despierte va a volver a preguntar si puede disparar
                disparar = RdP.shootIfWeCan(secuencia);
            }


            /* Ya disparo, busca a quien despertar */
//            System.out.println(" He disparado: "+Thread.currentThread());
            // guardamos en el log de transiciones la que fue disparada
            Log.Tlogger(secuencia);

            // debemos dejarle el mutex a alguien, tienen más prioridad aquellos que ya entraron al monitor
            // y están esperando en las colas
            
            // dormidos que est an sensibilizados
            int[][] sensibilizadas = Utils.calcularAND(RP.getSensibilizado(),colas.getDormidos());

            // obtenemos que cantidad de transiciones que estan sensibilizadas y con hilos dormidos
            int cantDormidosSens = 0;
            
            for (int[] sensibilizada : sensibilizadas) {
                if (sensibilizada[0] == 1) {
                    cantDormidosSens++;
                }
            }
            // si hay uno o mas hilos dormidos cuya transicion esta sensibilizada debemos despertar uno

            // si hay alguno dormido en una cola con su transicion sensibilizada le dejamos el mutex, sino hay nadie hacemos release para q lo agarre otro q este en la cola de entrada del mutex
            if(cantDormidosSens > 0) {
                System.out.println("Hilo: " + Thread.currentThread() + ". Hay " + cantDormidosSens + " sensibilizadas dormida con hilos esperando");

                colas.signal(sensibilizadas,this.politica,cantDormidosSens);
                return;
            }

            /* Soltamos el mutex en caso de no haber nadie esperando en alguna transicion. Entra un hilo en la cola del monitor. */
            mutex.release();
            System.out.println(" Me voy del monitor y hago Release desde: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits() + " - Hilos esperando: " + mutex.getQueueLength());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

    }
}

