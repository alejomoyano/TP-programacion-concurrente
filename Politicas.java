import java.util.Random;

public class Politicas {
    private RdP Red;
//	private int[][] conflictos;

    public Politicas(RdP red) {
        this.Red = red;

//		conflictos=new int[][]{{0},{1},{1},{0},{0},{1},{1},{0},{0},{1},{1},{1},{1},{1},{1},{0},{0}};
    }


    /**
     * Metodo que resuelve los conflicos estructurales.
     *
     * @param secuencia secuencia que contiene la tansicion con conflicto que quiere ser ejecutada
     * @return secuencia a ejecutar
     */
    public int HayConflicto(int[][] secuencia, int[][] dormidos) {


        boolean hayConflicto = false;
        int indice = 0;

        // indice que corresponde a que transicion tiene conflicto. Puede devolver -1 que sig
        // que no hay conflicto en la secuencia
        indice = RdP.tieneConflicto(secuencia);

        // coloca true si >= 0 y false si == -1
        hayConflicto = indice != -1;

        // si hay conflicto lo resolvemos
        if (hayConflicto) {
            return resolvemosConflicto(indice, dormidos);
        }

        //si no hay conflicto, devolvemos la transicion original
        return Utils.getIndex(secuencia);
    }

    /**
     * Metodo que resuelve el conflicto
     * @param indice indice de la transicion donde hay conflicto
     * @return indice de la transicion elegida
     */
    public int resolvemosConflicto(int indice, int[][] dormidos) {

//        int[][] secuencia = new int[17][1];
        int auxiliar = 0;
        Random random = new Random();

        //conflicto para elegir el procesador
        if (indice == 1 || indice == 2) { //if((index==1 || index==2) && (sensibilizadas[1][0]==1 && sensibilizadas[2][0]==1))
                                          // deberiamos aprovechar q tenemos las sensibilizadas calculadas con AND
                                          // con los hilos q estan dormidos asi no preguntamos al final si se puede disparar con los dormidos
                                          // ya que queda un poco redundante
            auxiliar =  this.ConflictoProcesador();
        }

        //conflicto Tareas en procesador1 - 50% de probabilidad cada uno
        if (indice == 5 || indice == 13) {
            int rand = random.nextInt(2);
            if (rand == 0) {
                auxiliar = 5;
            } else {
                auxiliar = 13;
            }
        }

        //conflicto en tareas procesador2 - 50% de probabilidad cada uno
        if (indice == 6 || indice == 14) {

            int rand = random.nextInt(2);

            if (rand == 0) {
                auxiliar = 6;

            } else {
                auxiliar = 14;

            }
        }

        //conflicto en memorias desde procesador1
        if (indice == 9 || indice == 10) {
            auxiliar = this.ConflictoMemoriasP1();

        }

        //conflicto en memorias desde procesador2
        if (indice == 11 || indice == 12) {
            auxiliar = this.ConflictoMemoriasP2();
        }

        // si es posible disparar la transicion que eligio la politica
        // entonces la devolvemos. Sino, nos volvemos con la anterior.
        if(dormidos[auxiliar][0] > 0){
            return auxiliar;             
        }                                
        return indice;

    }

    public int ConflictoProcesador() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos asignados
        if (marca[0][0] < marca[1][0]) {
            return 1;//devolver la transicion AsignarP1
        }
        if (marca[0][0] > marca[1][0]) {
            return 2;//devolver la transicion AsignarP2
        }


        // seleccionamos con un 50% alguna de las transiciones en caso de que esten iguales de tokens
        int random = rand.nextInt(2);
        if (random == 0) {
            return 1;
        } else {
            return 2;
        }

    }

    public int ConflictoMemoriasP1() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos ocupados
        if (marca[9][0] < marca[10][0]) {
            return 9;        //devolver la transicion P1M1
        }
        if (marca[9][0] > marca[10][0]) {
            return 10;    //devolver la transicion P1M2
        }

        // seleccionamos con un 50% alguna de las transiciones.
        int random = rand.nextInt(2);
        if (random == 0) {
            return 9;
        } else {
            return 10;
        }
    }

    public int ConflictoMemoriasP2() {

        Random rand = new Random();
        int[][] marca = Red.getMarcado();

        // mirando en el marcado, seleccionamos el que tiene menos ocupados
        if (marca[9][0] < marca[10][0]) {
            return 11;      //devolver la transicion P2M1
        }
        if (marca[9][0] > marca[10][0]) {
            return 12;      //devolver la transicion P2M2
        }

        // seleccionamos con un 50% alguna de las transiciones.
        int random = rand.nextInt(2);
        if (random == 0) {
            return 11;
        } else {
            return 12;
        }
    }
}
	
