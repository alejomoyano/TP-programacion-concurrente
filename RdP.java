import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.concurrent.Semaphore;

public class RdP {

	private static int[][] MIncidencia;
	private static int[][] MarcadoInicial;
	private static int[][] MarcadoActual;
	private static int[][] TSensibilizadas;
	private static int[][] PInvariantes;
	private static int[][] temporales;
	private static long[][] matrizTemp;//matriz guardar info temporales
	private static String IncidenciaPath, marcadoPath;
	private static boolean dormirse;
	private static long sleepTime;

	private static int[][] conflictos;


	/**
	 * Metodo donde inicializamos la red
	 * Obtenemos las matrices, seteamos los tiempos,
	 * la sensibilizacion incial...
	 */
	public RdP() {
		IncidenciaPath = "/home/alejomoyano/Documents/Facultad/PC/TP-programacion-concurrente/Matrizincidencia.txt";
		marcadoPath = "/home/alejomoyano/Documents/Facultad/PC/TP-programacion-concurrente/Marcadoinicial.txt";

		PInvariantes = new int[][]{{1},{4},{4},{1},{1},{8},{8},{1}};
		temporales = new int[][]{{0},{0},{0},{0},{0},{1},{1},{1},{1},{0},{0},{0},{0},{1},{1},{0},{0}}; // matriz con las transiciones que son temporales
		//aca en temporales no estan las transiciones de vaciar memorias como temporales. Yo dejaria asi, sin que sean temporales las transiciones
		matrizTemp = new long[7][5];
		conflictos = new int[][]{{0},{1},{1},{0},{0},{1},{1},{0},{0},{1},{1},{1},{1},{1},{1},{0},{0}}; // contiene las transiciones con conflicto

		// obtenemos la matriz de incidencia
		try {
			MIncidencia = Utils.leerMatriz2D(IncidenciaPath, 19, 17);
		} catch (FileNotFoundException e) {
		}
		System.out.println("Matriz incidencia");
		Utils.imprimirMatriz2D(MIncidencia);

		// obtenemos el marcado inicial
		try {
			MarcadoInicial = Utils.leerMatriz2D(marcadoPath, 19, 1);
		} catch (FileNotFoundException e) {
		}
		System.out.println("MarcadoInicial");
		Utils.imprimirMatriz2D(MarcadoInicial);


		MarcadoActual = MarcadoInicial;

		Sensibilizados();
		System.out.println("Vector E de transiciones sensibilizadas:\n");
		Utils.imprimirMatriz2D(TSensibilizadas);

		RdP.setTiempos();
		int k=0;
		for (int i = 0; i < 17; i++) {
			if (temporales[i][0] == 1){	
				matrizTemp[k][1] =(long) ((Math.random() * 50) + 1);//alfa entre 1-50ms
				matrizTemp[k][2] =(long) ((Math.random() * 1000) + 400);//beta	entre 400-1000ms
				k++;
			/*Enunciado: La suma de los tiempos asignados a las transiciones relacionadas a las tareas de tipo T2,
			debe ser mayor al tiempo asignado a la transición de tipo T1.
			Tiempo de ProcesarT2Px + tiempo de FinalizarT2Px > tiempo de FinalizarT1Px
			Esto esta contemplado en los thread.sleep de cada hilo pero aca no. Podria darse el caso q una tareaT1 demore mas
			que las T2 mas alla de los sleep q hay en los run? voy a seguir revisando eso aunque creo q esta bien asi
			Ya revise y en teoria esta bien por los sleep q hay en los run. Ahora puede darse el caso que se demore de mas
			el finalizarT1 porq no puede adquirir el mutex despues de hacer el sleep pero me parece algo medio fuera de 
			nuestro control o algo demasiado engorroso de manejar*/
			}
		}
	}
	

	/**
	 * Metodo que devuelve si la secuencia es disparable o no
	 * @param secuencia secuencia que se quiere disparar
	 * @return null si no es disparable, matriz con el nuevo marcado si es disparable
	 */
	public static int[][] esDisparable(int[][] secuencia){
		int[][] multiplicacion = Utils.MultiplicarMatrices(MIncidencia, secuencia); // realizamos el disparo de la secuencia [ W*si ]
		int[][] NuevoMarcado = Utils.SumarMatrices(multiplicacion, MarcadoActual); // obtenemos el nuevo marcado [ W*si+mi = mi+1 ]

		// debemos revisar si le nuevo marcado tiene algun elemento negativo. De esta forma podemos determinar
		// si es posible o no realizar el disparo.
		for (int[] ints : NuevoMarcado) {
			if (ints[0] < 0) {    //si algun elemento del nuevo marcado es negativo, no se puede efectuar el disparo
				return null;
			}
		}

		return NuevoMarcado;
	}



	/**
	 * Metodo que realiza el disparo de una secuencia si es posible hacerlo
	 * @param secuencia secuencia a disparar
	 * @return true si se disparo, false si no
	 */
	public static boolean shootIfWeCan(int[][] secuencia) {

		boolean disparar = false;

		int[][] nuevoMarcado = esDisparable(secuencia); // obtenemos el nuevo marcado si es que es disparable


		int tempTransIndex = isTemporal(secuencia); // -1 no es temporal, 0 <= si es temporal
		//System.out.println("Disparar segun marcado: "+disparar+" Hilo: "+Thread.currentThread().getName());

		disparar = nuevoMarcado != null; 		// true si tiene marcado, false si es null

		if(tempTransIndex >= 0 && disparar) {	//si es temporal y esta sensibilizado/se puede disparar
			//si no cumplen las condiciones de una transicion temporal, disparar sera falso y no se efectuara el disparo
			disparar = RdP.dispararTemporal(tempTransIndex);
			//System.out.println("dispararTemporal: "+disparar+" Hilo: "+Thread.currentThread().getName());

		}
		// si no es temporal o si es temporal y se cumplen las condiciones, dispara.
		if(disparar){
			System.out.println("--------------------------------------------------------");

			System.out.println("Acabo de disparar la secuencia:");
			Utils.imprimirMatriz2D(secuencia);
			System.out.println(Thread.currentThread().getName());
			System.out.println("El Marcado quedo:");
			Utils.imprimirMatriz2D(nuevoMarcado);
			System.out.println("--------------------------------------------------------");

			MarcadoActual = nuevoMarcado;    //efectuo el disparo si se cumplen las condiciones, guardando el nuevo marcado
			RdP.Sensibilizados();            //se actualizan las transiciones sensibilizadas
			RdP.invariantesDePlaza();
			RdP.setTiempos();

		}
		//System.out.println("Vuelvo de ecuacion estado con disparar: "+disparar+Thread.currentThread().getName());
		return disparar;
	}


	/**
	 * Metodo que actualiza la matriz de sensibilizados si es que se realiza el disparo
	 */
	public static void Sensibilizados() {

		int[][] vector = new int[17][1];
		int[][] sensibilizadas = new int[17][1];

		// ---- vamos probando transicion por transicion si es posible dispararla ----
		for (int i = 0; i < 17; i++) {
			vector[i][0] = 1;
			if (!(i == 0)) {
				vector[i - 1][0] = 0;	//vamos rotando haciendo la anterior 0 y la actual 1 ej: 100.. 0100.. 0010..
			}

			if(esDisparable(vector) != null){ // entra si es disparable
				sensibilizadas[i][0] = 1;
			}

		}
		// ---- 0 ----

		// seteamos la matriz con todas las transiciones actualmente sensibilizadas
		TSensibilizadas = sensibilizadas;
	}


	/**
	 * Metodo donde obtenemos las transiciones que estan actualmente sensibilizadas
	 */
	public int[][] getSensibilizado() {
		return TSensibilizadas;
	}

	/**
	 * Metodo que setea los tiempos de las transiciones temporales.
	 *
	 * @matrizTemp[n][4] -> 1 si esta sensibilizada, 0 si no lo esta
	 * @matrizTemp[n][3] -> id del hilo que llego primero (entre wi y alpha) y esta sleep esperando para disparar
	 * @matrizTemp[n][2] -> beta
	 * @matrizTemp[n][1] -> alpha
	 * @matrizTemp[n][0] -> guarda el tiempo de sensibilizacion
	 */
	public static void setTiempos() {//mira quien de las sensibilizadas es temp y completa  la matriz

		int counter = 0;//posicion en matrizTemp - indica que transicion temporal es?
		//int[][] sensibilizadas = Utils.calcularAND(TSensibilizadas, temporales);//devuelve las que son temp y estan sensibilizadas
		//esta de mas calcular el and si abajo en los if pregunto primero si es temporal y despues si esta sensibilizada

		for (int i = 0; i < 17; i++) {
			// esto esta para hacer que counter aumente, asi podemos encontrar el indice en matrizTemp
			if (temporales[i][0] == 1){
				if(TSensibilizadas[i][0] == 1) {//si es temporal y esta sensibilizada entra
					// si no estaba sensibilizada entonces debemos guardar el momento en el que se sensibilizo
					if (matrizTemp[counter][4] == 0) {
						long wiStart = System.currentTimeMillis();//wiStart = tiempo en ese instante en mili, se puede poner en nano
						matrizTemp[counter][0] = wiStart;//guarda wiStart en la matriz, en la transicion que corresponde
						matrizTemp[counter][4] = 1;//set sensibilizada
					}
					//else -> antes estaba sensibilizada
				}
				else{//no esta sensibilizada
					if(matrizTemp[counter][4] == 1){//estaba sensibilizada
						matrizTemp[counter][4] = 0;
						matrizTemp[counter][3] = 0;
						matrizTemp[counter][0] = 0;
					}
					//else -> no estaba sensibilizada
				}

				counter++;
			}
		}
	}

	/**
	 * Metodo que se encarga de indicar si se puede disparar o no una transicion temporal
	 * @param pos transicion temporal a disparar
	 * @return true si se puede disparar (esta dentro de la ventana), false si no puede hacerlo
	 */
	private static boolean dispararTemporal(int pos) {
		/* -Si llega antes de que este sensibilizada entonces se duerme en la cola del semaforo que corresponde.
		   -Si llega entre el wi y el alfa entonces sleep(alfa-(tiempo actual-wi))
		   -Si llega en la ventana se dispara
		   -Si llega y ya hay un id en la matriz, se duerme en la cola del semaforo que corresponde
		*/

		long arrivalTime = System.currentTimeMillis();//tiempo en el que llega el hilo a disparar la transicion

		long alfaRelativo = matrizTemp[pos][0] + matrizTemp[pos][1]; // wi + alfa
		long betaRelativo = matrizTemp[pos][0] + matrizTemp[pos][2]; // wi + beta

		long currentThreadId = Thread.currentThread().getId(); // id del hilo que se esta ejecutando

		// revisamos si hay alguien esperando para disparar o si ese alguien es el
		if(matrizTemp[pos][3] == 0 || matrizTemp[pos][3] == currentThreadId) {

			if ((alfaRelativo <= arrivalTime) && (betaRelativo >= arrivalTime)){	// si esta dentro de la ventana debe dispararse
											/*  la funcion ventana se usa solo aca, y podriamos preguntar lo mismo
 												usando las variables alfaRelativo y betaRelativo y no usar una funcion
											*/
					return true;
			}

			// si es menor que el beta relativo entonces significa que esta entre wi y alfa
			// ya que tampoco esta dentro de la ventana
			else if(arrivalTime < betaRelativo) {

				// si no hay un id entonces guardamos el current. Si hay id entonces dejamos el que esta
				matrizTemp[pos][3] =  matrizTemp[pos][3] == 0 ? currentThreadId : matrizTemp[pos][3];

				// debemos dormir el hilo durante alfaRelativo-arrivalTime que es lo mismo que (alfa-(tiempo actual-wi))
				RdP.setDormirse(true); // para indicar que se debe dormir y no saltar a la cola de la transicion
				RdP.setSleepTime(alfaRelativo-arrivalTime);
//				System.out.println("Llegue antes de la ventana, deberia dormirme. Alfa: "+alfaRelativo+"ms. arrivalTime: "+arrivalTime+"ms.");
				return false;
			}
			// esta despues del beta
			else{
				System.out.println("No estoy en la ventana ni antes de alfa");
				return false;//se debe ir a la cola de la transicion
			}

		}
		
		//si ya hay un id guardado y no es suyo devuelvo falso
		return false;
	}

	/**
	 * Metodo que devuelve si esta dentro de la ventana o no
	 * @param arrival tiempo de llegada
	 * @param t posicion en la matrizTemp
	 * @return true si esta en la ventana, false si esta fuera
	 */
	 /* 
	private static boolean ventana(long arrival, int t) {//esta o no en la ventana
		return (matrizTemp[t][1] + matrizTemp[t][0]) <= arrival && (matrizTemp[t][2] + matrizTemp[t][0]) >= arrival;
	} */
	
//

	/**
	 * Metodo que devuelve que posicion de la matrizTemp es la transicion temporal
	 * que le pasamos
	 * @param t transicion temporal
	 * @return posicion en la matrizTemp
	 */
	private static int posicion(int t) {
		int counter = 0;//posicion en matrizTemp
		for (int i = 0; i < 17; i++) {//buscar en que posicion de la matrizTemp esta esa transicion
			if (i == t) {
				break;
			}
			if(temporales[i][0]==1){counter++;}
		}
		return counter;
	}


	/**
	 * Metodo que indica si una secuencia tiene una transicion que es temporal o no
	 * @param secuencia secuencia de ejecucion
	 * @return true si tiene una temporal, false si no tiene
	 */
	public boolean esTemporal(int[][] secuencia) {
		int[][] sens = Utils.calcularAND(secuencia, temporales);//mira si la transicion es temporal

		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Funcion que devuelve -1 si no es temp, >=0 si es temp y la posicion en matrizTemp
	 * @param secuencia secuencia a disparar
	 * @return posicion en la matrizTemp
	 */
	private static int isTemporal(int[][] secuencia) {
		int pos = -1;
		int[][] sens = Utils.calcularAND(secuencia, temporales);//mira si la transicion es temporal
		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1) {
				pos = posicion(i);
			}
		}
		return pos;
	}


	/**
	 * Metodo que revisa si se cumplen las invariantes de plaza
	 */
	public static void invariantesDePlaza() {//comprueba si se cumplen las p-inv
		int[][] inv = new int[8][1];
		inv[0][0] = MarcadoActual[11][0] + MarcadoActual[2][0];
		inv[1][0] = MarcadoActual[0][0] + MarcadoActual[5][0];
		inv[2][0] = MarcadoActual[1][0] + MarcadoActual[6][0];
		inv[3][0] = MarcadoActual[16][0] + MarcadoActual[14][0] + MarcadoActual[15][0] + MarcadoActual[18][0] + MarcadoActual[17][0];
		inv[4][0] = MarcadoActual[15][0] + MarcadoActual[18][0] + MarcadoActual[8][0] + MarcadoActual[13][0];
		inv[5][0] = MarcadoActual[4][0] + MarcadoActual[10][0];
		inv[6][0] = MarcadoActual[3][0] + MarcadoActual[9][0];
		inv[7][0] = MarcadoActual[14][0] + MarcadoActual[12][0] + MarcadoActual[17][0] + MarcadoActual[7][0];

		for (int i = 0; i < 8; i++) {
			if (inv[i][0] != PInvariantes[i][0]) {
				System.out.println("No se cumplen las p-inv\n");
				System.out.println("----------------------------P-inv--------------------------");
				for (int j = 0; j < 8; j++) {
					System.out.println(inv[j][0]);
				}
				System.out.println("-----------------------------------------------------------");
				return;
			}
		}
		System.out.println("Se cumplen las p-inv\n");
	}


	/**
	 * Metodo para saber si una secuencia tiene conflicto o no
	 * @param secuencia
	 * @return -1 si no tiene conflicto, 0 <= si tiene conflicto
	 */
	public static int tieneConflicto(int[][] secuencia) {

		int[][] conflicto = new int[17][0];
		int indice = -1;

		conflicto = Utils.calcularAND(secuencia, conflictos);	//la transicion a disparar tiene conflicto?

		// si tiene conflicto entonces buscamos en que transicion es y la retornamos
		for(int i=0;i < conflicto.length;i++) {
			if(conflicto[i][0]==1) {
				indice = i;
				break;
			}
		}
		return indice;
	}



	public int[][] getMarcado() {
		return MarcadoActual;
	}

	public boolean getDormirse() {
		return dormirse;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public static void setDormirse(boolean c) {
		dormirse=c;
	}

	public static void setSleepTime(long time) {
		sleepTime=time;
	}

}