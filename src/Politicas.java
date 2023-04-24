package src;

import java.util.Random;

public class Politicas {
    private RdP Red;

    public Politicas(RdP red) {
        this.Red = red;

    }

    /**
     * Metodo que resuelve los conflicos estructurales.
     *
     * @param secuencia secuencia que contiene la tansicion con conflicto que quiere ser ejecutada
     * @param sensConDormidos transiciones sensibilidadas que ademas tienen hilos dormidos en cola
     * @return secuencia a ejecutar
     */
    private int HayConflicto(int[][] secuencia, int[][] sensConDormidos) {

        boolean hayConflicto = false;
        int indice = 0;

        // indice que corresponde a que transicion tiene conflicto. Puede devolver -1 que sig
        // que no hay conflicto en la secuencia
        indice = RdP.tieneConflicto(secuencia);

        // coloca true si >= 0 y false si == -1
        hayConflicto = indice != -1;

        // si hay conflicto lo resolvemos
        if (hayConflicto) {
            return resolvemosConflicto(indice, sensConDormidos);
        }

        //si no hay conflicto, devolvemos la transicion original
        return Utils.getIndex(secuencia);
    }

    /**
     * Metodo que resuelve el conflicto
     * @param indice indice de la transicion donde hay conflicto
     * @param sensAndDormidos transiciones sensibilizadas con hilos dormidos
     * @return indice de la transicion elegida
     */
    private int resolvemosConflicto(int indice, int[][] sensAndDormidos) {

        int auxiliar = 0;
        Random random = new Random();

        //conflicto para elegir el procesador
        if ((indice == 1 || indice == 2) && (sensAndDormidos[1][0] == 1 && sensAndDormidos[2][0] == 1)) {
            auxiliar =  this.ConflictoProcesador();
        }

        //conflicto Tareas en procesador1 - 50% de probabilidad cada uno
        if ((indice == 5 || indice == 13) && (sensAndDormidos[5][0] == 1 && sensAndDormidos[13][0] == 1)){
            int rand = random.nextInt(2);
            if (rand == 0) {
                auxiliar = 5;
            } else {
                auxiliar = 13;
            }
        }

        //conflicto en tareas procesador2 - 50% de probabilidad cada uno
        if ((indice == 6 || indice == 14) && (sensAndDormidos[6][0] == 1 && sensAndDormidos[14][0] == 1)) {

            int rand = random.nextInt(2);

            if (rand == 0) {
                auxiliar = 6;

            } else {
                auxiliar = 14;

            }
        }

        //conflicto en memorias desde procesador1
        if ((indice == 9 || indice == 10) && (sensAndDormidos[9][0] == 1 && sensAndDormidos[10][0] == 1)){
            auxiliar = this.ConflictoMemoriasP1();

        }

        //conflicto en memorias desde procesador2
        if ((indice == 11 || indice == 12) && (sensAndDormidos[11][0] == 1 && sensAndDormidos[12][0] == 1)){
            auxiliar = this.ConflictoMemoriasP2();
        }

        return auxiliar != 0 ? auxiliar : indice;

    }

    private int ConflictoProcesador() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos asignados
        if (marca[0][0] < marca[1][0]) {
            return 1;//devolver la transicion src.AsignarP1
        }
        if (marca[0][0] > marca[1][0]) {
            return 2;//devolver la transicion src.AsignarP2
        }


        // seleccionamos con un 50% alguna de las transiciones en caso de que esten iguales de tokens
        int random = rand.nextInt(2);
        if (random == 0) {
            return 1;
        } else {
            return 2;
        }

    }

    private int ConflictoMemoriasP1() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos ocupados
        if (marca[9][0] < marca[10][0]) {
            return 9;       //devolver la transicion src.P1M1
        }
        if (marca[9][0] > marca[10][0]) {
            return 10;      //devolver la transicion src.P1M2
        }

        // seleccionamos con un 50% alguna de las transiciones.
        int random = rand.nextInt(2);
        if (random == 0) {
            return 9;
        } else {
            return 10;
        }
    }

    private int ConflictoMemoriasP2() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos ocupados
        if (marca[9][0] < marca[10][0]) {
            return 11;      //devolver la transicion src.P2M1
        }
        if (marca[9][0] > marca[10][0]) {
            return 12;      //devolver la transicion src.P2M2
        }

        // seleccionamos con un 50% alguna de las transiciones.
        int random = rand.nextInt(2);
        if (random == 0) {
            return 11;
        } else {
            return 12;
        }
    }

    /**
     * Metodo que decide que hilo despertar, seleccionando uno random y resolviendo conflicto
     * @param cantDormidosSens cantidad de dormidos sensibilizados
     * @param sensAndDormidos transiciones sensibilizadas con hilos dormidos
     * @return indice de la transicion elegida
     */
    public int decideTransicion(int cantDormidosSens, int[][] sensAndDormidos){
        int count = 0;
        int transicionSeleccionada = (int) (Math.random() * cantDormidosSens) + 1;//rand entre 1 y la cant de hilos disparables
        int indexTransicion = 0;

        //encuentra la posicion del hilo elegido
        while(count != transicionSeleccionada){
            if(sensAndDormidos[indexTransicion][0]==1)
                count++;
            if(count == transicionSeleccionada)
                break;
            indexTransicion++;
        }

        // generamos la secuencia que fue elegida para ser disparada.
        int[][] secuencia = new int[17][1];
        secuencia[indexTransicion][0] = 1;

        return HayConflicto(secuencia,sensAndDormidos);
//        return secuencia;
    }
}
	
