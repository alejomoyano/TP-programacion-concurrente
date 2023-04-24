package src;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Colas {
    private int[][] dormidos;
    private ArrayList<Semaphore> semaforos;

    public Colas(){
        dormidos = new int[17][1];
        semaforos = new ArrayList<Semaphore>();
        for(int i=0; i<17; i++){
            semaforos.add(new Semaphore(0,true));
        }
    }

    public int[][] getDormidos(){
        for(int i=0; i<17;i++){
            dormidos[i][0]=semaforos.get(i).getQueueLength();
        }
        return dormidos;
    }

    /**
     * Metodo que en el que se manda a dormir al hilo que intento disparar una transicion no disponible.
     * Puede que haya sido porque no estaba sensibilizada o porque esta después de la ventana temporal.
     * @param secuencia secuencia de disparo
     */
    public void setDormirse(int[][] secuencia){
      
        for(int i=0;i<17;i++){
            if(secuencia[i][0] == 1){
                /* Estuve viendo sobre la diferencia entre acquireUninterruptibly() y acquire().
                 * Porque en el mutex usamos acquire() y aca el acquireUninterruptibly() y vos recordas si hicimos esto por alguna razon?
                 */
                semaforos.get(i).acquireUninterruptibly();
                break;
            }
        }
    }

    /**
     * Metodo que despierta un hilo dormido en una cola
     * @param hilo hilo a despertar
     */
    public void signal(int indexTransicion){
        semaforos.get(indexTransicion).release();
    }

}
