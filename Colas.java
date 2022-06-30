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

    public void setDormirse(int[][] secuencia){
        int indice = 0;
        for(int i=0;i<17;i++){
            if(secuencia[i][0] == 1){
                indice = i;
            }
        }
        semaforos.get(indice).acquireUninterruptibly();
    }

    /**
     * Metodo que selecciona al azar una transicion que se encuentra sensibilizada y que tiene
     * hilos esperando en la cola para ejecutarla
     * @param sensibilizadas: transiciones sensibilizadas
     * @param politica: instancia de la clase Politica
     * @param cantDormidosSens: cantidad de transiciones sensibilizadas
     */
    public void despertar(int[][] sensibilizadas,Politicas politica, int cantDormidosSens){
        //int cant=0;
        int count=0;
			
        //for (int i = 0; i < and.length; i++) {//cuenta la cantidad hilos que pueden disparar
        //    if(and[i][0]==1){cant++;}
        //}
        int pos = (int) (Math.random() * cantDormidosSens) + 1;//rand entre 1 y la cant de hilos disparables
        int indexTransicion=0;

        //encuentra la posicion del hilo elegido, se elije un hilo al azar para despertar, en caso que deba aplicarse politicas se aplicara
        while(count!=pos){
        	if(sensibilizadas[indexTransicion][0]==1)
                count++;
        	if(count==pos)
                break;
            indexTransicion++;
        }


        //TODO -> REVISAR [politicas.HayConflicto()]


     //   System.out.println("Hilo en clase colas despertando: "+Thread.currentThread()+"Valor j: "+j);
        if((indexTransicion==1 || indexTransicion==2) && (sensibilizadas[1][0]==1 && sensibilizadas[2][0]==1)) {		//conflicto para elegir el procesador
			semaforos.get(politica.ConflictoProcesador()).release();
			//System.out.println("Hilo: "+Thread.currentThread()+"conflicto procesador "+p.ConflictoProcesador());
			return;
        }
        if((indexTransicion==9 || indexTransicion==10) && (sensibilizadas[9][0]==1 && sensibilizadas[10][0]==1)) {		//conflicto de memorias P1
        	semaforos.get(politica.ConflictoMemoriasP1()).release();
        	//System.out.println("Hilo: "+Thread.currentThread()+"conflicto memorias1 "+p.ConflictoMemoriasP1());
        	return;
        }
        if((indexTransicion==11 || indexTransicion==12) && (sensibilizadas[11][0]==1 && sensibilizadas[12][0]==1)) {	//conflicto de memorias P2
        	semaforos.get(politica.ConflictoMemoriasP2()).release();
        	//System.out.println("Hilo: "+Thread.currentThread()+"conflicto memorias2 "+p.ConflictoMemoriasP2());

        	return;
        }

        //si no hay conflicto con politicas o solo hay un hilo en cola de los dos, simplemente hago release del hilo en el semaforo en el que estaba
        semaforos.get(indexTransicion).release();
    	//System.out.println("Hilo: "+Thread.currentThread()+"sin conflicto. Valor j: "+j);

    }
}
