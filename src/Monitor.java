package src;

import java.util.concurrent.Semaphore;


public class Monitor {

    private RdP RP;
    public Semaphore mutex;
    private Politicas politica;
    private Colas colas;
    private Memoria memoria_uno, memoria_dos;

    public Monitor(RdP red, Politicas politica,Colas colas, Memoria memoria_uno, Memoria memoria_dos){
        this.politica = politica;
        RP = red;
        this.colas = colas;
        mutex = new Semaphore(1,true);
        this.memoria_uno = memoria_uno;
        this.memoria_dos = memoria_dos;
    }

    public void Disparar (int transicion) {

        boolean disparar = false;

        try {
          //  System.out.println(" Realizando acquire - Hilo: "+Thread.currentThread()+" - Hilos en cola: " + mutex.getQueueLength());

            mutex.acquire();

//            System.out.println(" Acquire realizado por: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits());

            disparar = RP.dispararTransicion(transicion); // intentamos disparar

            // si no puede disparar entonces
            while (!disparar){
//                   System.out.println(" No pude disparar, me voy a dormir: "+Thread.currentThread());
                if(RP.getDormirse() && RP.esTemporal(transicion)) {
                    RP.setDormirse(false);    //bajo el flag, borro el indicador para el proximo hilo
//                    System.out.println(" No pude disparar temporal, me voy a dormir por: "+RP.getSleepTime()+" ms.Thread:"+Thread.currentThread().getName());

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
//                        System.out.println("Soy "+Thread.currentThread().getName()+ " he despertado de mi sleep, intento disparar de nuevo");
                        mutex.acquire();
                    }
                    catch (InterruptedException exception){
                        exception.printStackTrace();
                    }
                }
                else {
//                   System.out.println(" No pude disparar me voy a mi cola. "+Thread.currentThread());
                    mutex.release();
                    colas.setDormirse(transicion);
//                    System.out.println("Soy: " + Thread.currentThread() + " voy a intentar disparar de nuevo, me sacaron de la cola.");
                }
                // cuando se despierte va a volver a preguntar si puede disparar
                disparar = RP.dispararTransicion(transicion);
            }


            /* Ya disparo, busca a quien despertar */
            // System.out.println(" He disparado: "+Thread.currentThread());
            Log.Tlogger(transicion);


            int[][] sensAndDormidos = Utils.calcularAND(RP.getSensibilizado(),colas.getDormidos());
            int cantDormidosSens = 0;
            for (int[] sensibilizada : sensAndDormidos) {
                if (sensibilizada[0] == 1) {
                    cantDormidosSens++;
                }
            }

            // si hay uno o mas hilos dormidos cuya transicion esta sensibilizada debemos despertar uno
            if(cantDormidosSens > 0) {
//                System.out.println("Hilo: " + Thread.currentThread() + ". Hay " + cantDormidosSens + " sensibilizadas dormida con hilos esperando");
                int indexTransicion = politica.decideTransicion(cantDormidosSens,sensAndDormidos);
                colas.signal(indexTransicion);
                return;
            }

            /* Soltamos el mutex en caso de no haber nadie esperando en alguna transicion. Entra un hilo en la cola del monitor. */
            mutex.release();
//                System.out.println(" Me voy del monitor y hago Release desde: "+Thread.currentThread()+" - Permisos restantes: " + mutex.availablePermits() + " - Hilos esperando: " + mutex.getQueueLength());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

    }
}

