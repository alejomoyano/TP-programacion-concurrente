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
     * Puede que haya sido porque no estaba sensibilizada o porque esta despues de la ventana temporal.
     * @param secuencia secuencia de disparo
     */
    public void setDormirse(int[][] secuencia){
        // int indice = 0;
        for(int i=0;i<17;i++){
            if(secuencia[i][0] == 1){
                // indice = i;
                //este indice creo que esta de mas
                semaforos.get(i).acquireUninterruptibly();
                break;
                //directamente aca ya que solo se entra una vez al if que es a la secuencia q corresponda
            }
        }
        //semaforos.get(indice).acquireUninterruptibly();
    }

    /**
     * Metodo que selecciona al azar una transicion que se encuentra sensibilizada y que tiene
     * hilos esperando en la cola para ejecutarla
     * @param sensibilizadas transiciones sensibilizadas con hilos dormidos
     * @param politica instancia de la clase Politica
     * @param cantDormidosSens cantidad de transiciones sensibilizadas
     */
    public void signal(int[][] sensibilizadas,Politicas politica, int cantDormidosSens){

        int count = 0;
        int transicionSeleccionada = (int) (Math.random() * cantDormidosSens) + 1;//rand entre 1 y la cant de hilos disparables
        int indexTransicion = 0;

        //encuentra la posicion del hilo elegido, se elige un hilo al azar para despertar,
        // en caso de que deba aplicarse politicas se aplicara
        while(count != transicionSeleccionada){
        	if(sensibilizadas[indexTransicion][0]==1)
                count++;
        	if(count == transicionSeleccionada)
                break;
            indexTransicion++;
        }

        // generamos la secuencia que fue elegida para ser disparada, revisamos y solucionamos el conflicto.
        int[][] secuencia = new int[17][1];
        secuencia[indexTransicion][0] = 1;


        // devolvera el indice de la transicion que debera dispararse.
        indexTransicion = politica.HayConflicto(secuencia,sensibilizadas);
        semaforos.get(indexTransicion).release();

    }

}
