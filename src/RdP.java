package src;

import java.io.FileNotFoundException;	//solo esta se estaria usando en esta clase rdp


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
		IncidenciaPath = "./Matrizincidencia.txt";
		marcadoPath = "./Marcadoinicial.txt";

		PInvariantes = new int[][]{{1},{4},{4},{1},{1},{8},{8},{1}};

		temporales = new int[][]{{1},{0},{0},{0},{0},{1},{1},{1},{1},{0},{0},{0},{0},{1},{1},{1},{1}}; // matriz con las transiciones que son temporales
		matrizTemp = new long[9][5];
		conflictos = new int[][]{{0},{1},{1},{0},{0},{1},{1},{0},{0},{1},{1},{1},{1},{1},{1},{0},{0}}; // contiene las transiciones con conflicto

		// obtenemos la matriz de incidencia
		try {
			MIncidencia = Utils.leerMatriz2D(IncidenciaPath, 19, 17);
		} catch (FileNotFoundException e) {}

		System.out.println("Matriz incidencia");
		Utils.imprimirMatriz2D(MIncidencia);

		// obtenemos el marcado inicial
		try {
			MarcadoInicial = Utils.leerMatriz2D(marcadoPath, 19, 1);
		} catch (FileNotFoundException e) {}

		System.out.println("MarcadoInicial");
		Utils.imprimirMatriz2D(MarcadoInicial);

		MarcadoActual = MarcadoInicial;

		Sensibilizados();
//		System.out.println("Vector E de transiciones sensibilizadas:\n");
//		Utils.imprimirMatriz2D(TSensibilizadas);

		RdP.setTiempos();
		int k=0;

		for (int j = 0; j < 17; j++) {
			if (temporales[j][0] == 1) {
				if (k == 3 || k == 4) {                    //matrizTemp[3] y [4] corresponden a FinalizarT2P1 y FinalizarT2P2
					matrizTemp[k][1] = (long) 60;//alfa 60ms		//FinalizarT2Px demoran como minimo 60ms
				}
				else {
					matrizTemp[k][1] = (long) 20;//alfa 30ms		// El resto de temporales demora como minimo 20 ms
				}
				matrizTemp[k][2] = (long) 0xffff;//beta infinito
				k++;
			}
		}
	}
	

	/**
	 * Metodo que devuelve si la secuencia es disparable o no
	 * @param secuencia secuencia que se quiere disparar
	 * @return null si no es disparable, matriz con el nuevo marcado si es disparable
	 */
	private static int[][] esDisparable(int[][] secuencia){
		int[][] multiplicacion = Utils.MultiplicarMatrices(MIncidencia, secuencia); // realizamos el disparo de la secuencia [ W*si ]
		int[][] NuevoMarcado = Utils.SumarMatrices(multiplicacion, MarcadoActual); // obtenemos el nuevo marcado [ W*si+mi = mi+1 ] [Ecuacion fundamental]

		// debemos revisar si el nuevo marcado tiene algún elemento negativo. De esta forma podemos determinar si es posible o no realizar el disparo.
		for (int[] ints : NuevoMarcado) {
			// si algún elemento del nuevo marcado es negativo, no se puede efectuar el disparo
			if (ints[0] < 0)
				return null;
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

		if(tempTransIndex >= 0 && disparar) {	// si es temporal y está sensibilizado/se puede disparar
			//si no cumplen las condiciones de una transition temporal, disparar será falso y no se efectuara el disparo
			disparar = dispararTemporal(tempTransIndex);
//			System.out.println("dispararTemporal: "+disparar+" Hilo: "+Thread.currentThread().getName());
		}
		// si no es temporal o si es temporal y se cumplen las condiciones, dispara.
		if(disparar){
			/*System.out.println("--------------------------------------------------------");

			System.out.println("Acabo de disparar la secuencia:");
			src.Utils.imprimirMatriz2D(secuencia);
			System.out.println(Thread.currentThread().getName());
			System.out.println("El Marcado quedo:");
			src.Utils.imprimirMatriz2D(nuevoMarcado);
			System.out.println("--------------------------------------------------------");
*/
			MarcadoActual = nuevoMarcado;    //efectuo el disparo si se cumplen las condiciones, guardando el nuevo marcado
			Sensibilizados();            //se actualizan las transiciones sensibilizadas
			invariantesDePlaza();
			setTiempos();

		}
		//System.out.println("Vuelvo de ecuacion estado con disparar: "+disparar+Thread.currentThread().getName());
		return disparar;
	}


	/**
	 * Metodo que actualiza la matriz de sensibilizados si es que se realiza el disparo
	 */
	private static void Sensibilizados() {
//		public static void Sensibilizados() {

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
	 * Si estaba sensibilizada y ahora no, entonces limpia su posicion en la matriz.
	 * Si se acaba de sensibilizar guardamos el wi y la marcamos como sensibilizada.
	 *
	 * @matrizTemp[n][4] -> 1 si esta sensibilizada, 0 si no lo esta
	 * @matrizTemp[n][3] -> id del hilo que llego primero entre wi y alpha y esta sleep
	 * @matrizTemp[n][2] -> beta
	 * @matrizTemp[n][1] -> alpha
	 * @matrizTemp[n][0] -> tiempo de sensibilizacion (wi)
	 */

	private static void setTiempos() {

		int counter = 0; // posicion en matrizTemp

		for (int i = 0; i < 17; i++) {
			// esto esta para hacer que counter aumente, asi podemos encontrar el indice en matrizTemp
			if (temporales[i][0] == 1){
				if(TSensibilizadas[i][0] == 1) {//si es temporal y esta sensibilizada entra
					// si no estaba sensibilizada entonces debemos guardar el momento en el que se sensibilizo
					if (matrizTemp[counter][4] == 0) {
						long wiStart = System.currentTimeMillis(); // wiStart = tiempo en ese instante en ms
						matrizTemp[counter][0] = wiStart; // guarda wiStart en la matriz, en la transicion que corresponde
						matrizTemp[counter][4] = 1; // set sensibilizada
					}
					// else -> antes estaba sensibilizada
				}
				else{ // no esta sensibilizada
					if(matrizTemp[counter][4] == 1){ // estaba sensibilizada
						matrizTemp[counter][4] = 0;
						matrizTemp[counter][3] = 0;
						matrizTemp[counter][0] = 0;
					}
					// else -> no estaba sensibilizada
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
		/*
		   -Si llega entre el wi y el alfa entonces sleep(alfa-(tiempo actual-wi)).
		   -Si llega en la ventana se dispara.
		   -Si llega y ya hay un id en la matriz, se duerme en la cola del semaforo que corresponde.
		*/

		long arrivalTime = System.currentTimeMillis();//tiempo en el que llega el hilo a disparar la transicion

		long alfaRelativo = matrizTemp[pos][0] + matrizTemp[pos][1]; // wi + alfa
		long betaRelativo = matrizTemp[pos][0] + matrizTemp[pos][2]; // wi + beta

		long currentThreadId = Thread.currentThread().getId(); // id del hilo que se esta ejecutando

		// es el primero en llegar o el hilo registrado es el mismo
		if(matrizTemp[pos][3] == 0 || matrizTemp[pos][3] == currentThreadId) {

			// si esta dentro de la ventana debe dispararse
			if ((alfaRelativo <= arrivalTime) && (betaRelativo >= arrivalTime)) {
//				System.out.println("Llegue justo en la ventana. Alfa: "+alfaRelativo+"ms. arrivalTime: "+arrivalTime+"ms.");
				return true;
			}


			// si es menor que el alpha relativo entonces significa que esta entre wi y alfa. Ya que tampoco esta dentro de la ventana

			else if(arrivalTime < alfaRelativo) {

				// si no hay un id entonces guardamos el current.
				matrizTemp[pos][3] =  matrizTemp[pos][3] == 0 ? currentThreadId : matrizTemp[pos][3];

				// debemos dormir el hilo durante alfaRelativo-arrivalTime que es lo mismo que (alfa-(tiempo actual-wi))
				RdP.setDormirse(true); // para indicar que se debe dormir y no saltar a la cola de la transicion
				RdP.setSleepTime(alfaRelativo-arrivalTime);
//				System.out.println("Llegue antes de la ventana, deberia dormirme. Alfa: "+alfaRelativo+"ms. arrivalTime: "+arrivalTime+"ms.");
				return false;
			}
			// esta despues del beta
			else{
//				System.out.println("No estoy en la ventana ni antes de alfa, estoy despues de beta");
				return false;//se debe ir a la cola de la transicion
			}

		}
		//si ya hay un id guardado y no es suyo devuelvo falso
		return false;
	}


	/**
	 * Metodo que devuelve que posicion de la matrizTemp es la transicion temporal
	 * que le pasamos
	 * @param t transicion temporal
	 * @return posicion en la matrizTemp
	 */
	private static int posicion(int t) {
		int counter = 0;//posicion en matrizTemp
		for (int i = 0; i < 17; i++) {//buscar en que posicion de la matrizTemp esta esa transicion
			if (i == t)
				break;

			if(temporales[i][0]==1)
				counter++;
		}
		return counter;
	}


	/**
	 * Metodo que indica si una secuencia tiene una transicion que es temporal o no
	 * @param secuencia secuencia de ejecucion
	 * @return true si tiene una temporal, false si no tiene
	 */
	public boolean esTemporal(int[][] secuencia) {
		int[][] sens = Utils.calcularAND(secuencia, temporales);

		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1)
				return true;
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
		int[][] sens = Utils.calcularAND(secuencia, temporales);

		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1)
				pos = posicion(i);
		}

		return pos;
	}


	/**
	 * Metodo que revisa si se cumplen las invariantes de plaza
	 */
	private static void invariantesDePlaza() {
		//PInvariantes = new int[][]{{1},{4},{4},{1},{1},{8},{8},{1}};
		int[][] inv = new int[8][1];
		inv[0][0] = MarcadoActual[11][0] + MarcadoActual[2][0];	// P0 + ColaProcesos
		inv[1][0] = MarcadoActual[0][0] + MarcadoActual[5][0];	// ColaP1 + LimiteColaP1
		inv[2][0] = MarcadoActual[1][0] + MarcadoActual[6][0];	// ColaP2 + LimiteColaP2
		inv[3][0] = MarcadoActual[16][0] + MarcadoActual[14][0] + MarcadoActual[15][0] + MarcadoActual[18][0] + MarcadoActual[17][0]; // RecursoTarea + ProcesandoP1 + ProcesandoP2 + src.Tarea2P1 + src.Tarea2P2
		inv[4][0] = MarcadoActual[15][0] + MarcadoActual[18][0] + MarcadoActual[8][0] + MarcadoActual[13][0];	// ProcesandoP2 + src.Tarea2P2 + ListoP2 + Procesador2
		inv[5][0] = MarcadoActual[4][0] + MarcadoActual[10][0];	// DisponibleM2 + M2
		inv[6][0] = MarcadoActual[3][0] + MarcadoActual[9][0];	// DisponibleM1 + M1
		inv[7][0] = MarcadoActual[14][0] + MarcadoActual[12][0] + MarcadoActual[17][0] + MarcadoActual[7][0]; // ProcesandoP1 + src.Tarea2P1 + ListoP1 + Procesador1

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
//		System.out.println("Se cumplen las p-inv\n");
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

		// buscamos que transicion tiene conflicto y la retornamos
		for(int i=0; i < conflicto.length; i++) {
			if(conflicto[i][0] == 1 ) {
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