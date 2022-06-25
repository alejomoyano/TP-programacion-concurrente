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
        int indice=0;
        for(int i=0;i<17;i++){
            if(secuencia[i][0]==1){indice=i;}
        }
        semaforos.get(indice).acquireUninterruptibly();
    }

    public void despertar(int[][] and,Politicas p){
        int cant=0;
        int count=0;
			
        for (int i = 0; i < and.length; i++) {//cuenta la cantidad hilos que pueden disparar
            if(and[i][0]==1){cant++;}
        }
        int pos = (int) (Math.random() * cant) + 1;//rand entre 1 y la cant de hilos disparables
        int j=0;
        while(count!=pos){//encuentra la posicion del hilo elegido, se elije un hilo al azar para despertar, en caso que deba aplicarse politicas se aplicara
         
        	
        	if(and[j][0]==1){count++;} 
        	if(count==pos) {break;}
        	j++;
        	
            
        }
     //   System.out.println("Hilo en clase colas despertando: "+Thread.currentThread()+"Valor j: "+j);
        if((j==1 || j==2) && (and[1][0]==1 && and[2][0]==1)) {		//conflicto para elegir el procesador
			semaforos.get(p.ConflictoProcesador()).release();
			//System.out.println("Hilo: "+Thread.currentThread()+"conflicto procesador "+p.ConflictoProcesador());
			return;
			}
        if((j==9 || j==10) && (and[9][0]==1 && and[10][0]==1)) {		//conflicto de memorias P1
        	semaforos.get(p.ConflictoMemoriasP1()).release();
        	//System.out.println("Hilo: "+Thread.currentThread()+"conflicto memorias1 "+p.ConflictoMemoriasP1());
        	return;
        }
        if((j==11 || j==12) && (and[11][0]==1 && and[12][0]==1)) {	//conflicto de memorias P2
        	semaforos.get(p.ConflictoMemoriasP2()).release();
        	//System.out.println("Hilo: "+Thread.currentThread()+"conflicto memorias2 "+p.ConflictoMemoriasP2());

        	return;
        }
        semaforos.get(j).release();		//si no hay conflicto con politicas o solo hay un hilo en cola de los dos, simplemente hag orelease del hilo en el semaforo en el que estaba
    	//System.out.println("Hilo: "+Thread.currentThread()+"sin conflicto. Valor j: "+j);

    }
}
