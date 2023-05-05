package src;

public class Memoria {

    private int buffer;
    private int contador; //cuenta el total de tareas que se guardaron en las memorias

    public Memoria(){
        buffer=0;
        contador=0;
    }

    public int getCantActual() {return buffer;}
    public int getHistorial() {return contador;}

    synchronized public void guardar(){
        buffer++;
        contador++;
    }

    synchronized public void vaciar(){
        buffer--;
    }
}

/*  quiero saber si entendi bien esto... Si el buffer esta lleno significa que no hay nada para guardar
     entonces se duerme hasta que algun otro hilo entre a vaciar y lo despierte.
     Pero en que casos se daria esto? porque si ejecuta vaciar es porque hay algo no?
     Digamos que la transicion estaba sensibilizada
     En el caso de vaciar si, porq hay una sola transicion y un solo hilo por memoria, pero en el caso de guardar hay 2
     P1M1 y P2M1 por ej los dos guardan en M1 y podria darse el caso q lo quieran hacer al mismo tiempo, porque dependen de la plaza
     ListoP1 y ListoP2 y q haya lugar en la memoria, si se da el caso q se disparen las dos PxMx seguidas y por alguna razon
     se demoro una y entraron a la memoria al mismo tiempo 
     o que mientras uno este guardando entre otro hilo a querer vaciar.
     Esto de las memorias lo vio el profe? en q estado estaba? como antes? Es medio rebuscado que pase que se pisen adentro 
     pero hay una infima posibilidad de que pase y ventre seguro nos va a preguntar como lo planteamos. Yo lo dejaria como antes para asegurar
     de que no pase nunca que se pisen adentro de la memoria
     */
