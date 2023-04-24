package src;

public class Memoria {
    private int capMax;
    private int buffer;
    private int contador; //cuenta el total de tareas que se guardaron en las memorias

    public Memoria(){
        capMax=8;
        buffer=0;
        contador=0;
    }

    public int getCantActual() {return buffer;}
    public int getCapMax() {return capMax;}
    public int getHistorial() {return contador;}

    synchronized public void guardar(){
        while(buffer==capMax){
            try{
                wait();
            }
            catch(InterruptedException  e ){
                e.printStackTrace();
            }
        }
        buffer++;
        contador++;
        notify();
    }

    synchronized public void vaciar(){
        while(buffer==0){
            try{
                wait();
            }
            catch(InterruptedException  e ){
                e.printStackTrace();
            }
        }
            try {
                Thread.sleep((long)((Math.random() * 45) + 35)); //se demora entre alfa 35ms y beta 45ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        buffer--;
        notify();
    }
}
