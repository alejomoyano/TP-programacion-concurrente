import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	public RdP() {
		IncidenciaPath = "C:\\Users\\Axel\\Desktop\\\\Soft\\Tp2mate\\Matrizincidencia.txt";
		marcadoPath = "C:\\Users\\Axel\\Desktop\\\\Soft\\Tp2mate\\Marcadoinicial.txt";

		PInvariantes = new int[][]{{1},{4},{4},{1},{1},{8},{8},{1}};
		temporales = new int[][]{{0},{0},{0},{0},{0},{1},{1},{1},{1},{0},{0},{0},{0},{1},{1},{0},{0}};
		matrizTemp = new long[7][5];

		try {
			MIncidencia = leerMatriz2D(IncidenciaPath, 19, 17);
		} catch (FileNotFoundException e) {
		}
		System.out.println("Matriz incidencia");
		imprimirMatriz2D(MIncidencia);

		try {
			MarcadoInicial = leerMatriz2D(marcadoPath, 19, 1);
		} catch (FileNotFoundException e) {
		}
		System.out.println("MarcadoInicial");
		imprimirMatriz2D(MarcadoInicial);

		MarcadoActual = MarcadoInicial;

		Sensibilizados();
		System.out.println("Vector E de transiciones sensibilizadas:\n");
		RdP.imprimirMatriz2D(TSensibilizadas);

		RdP.setTiempos();
		int k=0;
		for (int i = 0; i < 17; i++) {
			if (temporales[i][0] == 1){	
				matrizTemp[k][1] =(long) ((Math.random() * 50) + 1);//alfa entre 1-50ms
				matrizTemp[k][2] =(long) ((Math.random() * 1000) + 400);//beta	entre 400-1000ms
				k++;
			}
		}
	}
	

	public static int[][] leerMatriz2D(String path, int filas, int columnas) throws FileNotFoundException {

		Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));
		int[][] myArray = new int[filas][columnas];
		while (sc.hasNextLine()) {
			for (int i = 0; i < myArray.length; i++) {
				String[] line = sc.nextLine().trim().split(" ");
				for (int j = 0; j < line.length; j++) {
					myArray[i][j] = Integer.parseInt(line[j]);
				}
			}
		}
		return myArray;
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

	public static void imprimirMatriz2D(int[][] matriz) {
		for (int i = 0; i < matriz.length; i++) {
			for (int j = 0; j < matriz[0].length; j++) {
				System.out.print(" " + matriz[i][j] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
	}

	public static int[][] SumarMatrices(int[][] a, int[][] b) {
		int m = a.length;  // numero de filas
		int n = a[0].length;   // numero de columnas
		int[][] c = new int[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				c[i][j] = a[i][j] + b[i][j];
		return c;
	}

	public static int[][] MultiplicarMatrices(int[][] a, int[][] b) {
		int m1 = a.length;  // filas de a
		int n1 = a[0].length;   // columnas de a
		int m2 = b.length;      // filas de b
		int n2 = b[0].length;    // columnas de b.
	        
	       /* System.out.println("Columnas de a: "+n1 );
	        System.out.println("filas de b: "+m2);
	        System.out.println("columnas de b: "+n2);*/


		if (n1 != m2) throw new RuntimeException("Dimensiones de matrices incompatibles.");
		int[][] c = new int[m1][n2];    //filas de a y columnas de b
		for (int i = 0; i < m1; i++) {
			//  for (int j = 0; j < n2; j++)
			for (int k = 0; k < n1; k++) {
				c[i][0] += a[i][k] * b[k][0];
			}
		}
		return c;
	}

	public static boolean EcuacionEstado(int[][] secuencia, boolean actualizar) {
		boolean disparar = true;
		int[][] multiplicacion = MultiplicarMatrices(MIncidencia, secuencia);
		int[][] NuevoMarcado = SumarMatrices(multiplicacion, MarcadoActual);
		for (int i = 0; i < NuevoMarcado.length; i++) {
			if (NuevoMarcado[i][0] < 0) {    //si algun elemento del nuevo marcado es negativo, no se puede efectuar el disparo
				disparar = false;
				break;
			}
		}

		int pos = isTemporal(secuencia); 
		//System.out.println("Disparar segun marcado: "+disparar+" Hilo: "+Thread.currentThread().getName());

		if(disparar && pos>=0 && actualizar){
			disparar=RdP.dispararTemporal(pos);	//si no cumplen las condiciones de una transicion temporal, disparar sera falso y no se efectuara el disparo
			//System.out.println("dispararTemporal: "+disparar+" Hilo: "+Thread.currentThread().getName());
		}
		if (disparar && actualizar) {
			MarcadoActual = NuevoMarcado;    //efectuo el disparo si se cumplen las condiciones, guardando el nuevo marcado
			RdP.Sensibilizados();			//se actualizan las transiciones sensibilizadas
			RdP.invariantesDePlaza();
			RdP.setTiempos();
		}
		//System.out.println("Vuelvo de ecuacion estado con disparar: "+disparar+Thread.currentThread().getName());
		return disparar;
	}

	public static void Sensibilizados() {
		boolean sensibilizado;
		int[][] vector = new int[17][1];
		int[][] sensibilizadas = new int[17][1];
		for (int i = 0; i < 17; i++) {
			vector[i][0] = 1;
			if (!(i == 0)) {
				vector[i - 1][0] = 0;
			}
			sensibilizado = RdP.EcuacionEstado(vector, false);
			if (sensibilizado) {
				sensibilizadas[i][0] = 1;
			}
		}
		TSensibilizadas = sensibilizadas;
	}

	public int[][] getSensibilizado() {
		return TSensibilizadas;
	}

	public static int[][] calcularAND(int[][] s, int[][] c) {
		int[][] resultado = new int[17][1];
		for (int i = 0; i < 17; i++) {
			resultado[i][0] = s[i][0] & c[i][0];
		}
		return resultado;
	}

	public static void setTiempos() {//mira quien de las sensibilizadas es temp y completa  la matriz
		int counter = 0;//posicion en matrizTemp
		int[][] sens = calcularAND(TSensibilizadas, temporales);//devuelve las que son temp y estan sensibilizadas
		for (int i = 0; i < 17; i++) {
			if (temporales[i][0] == 1){
				if(sens[i][0] == 1) {//esta sensibilizada
					if (matrizTemp[counter][4] == 0) {//no lo estaba antes
						long wiStart = System.currentTimeMillis();//wiStart = tiempo en ese instante en mili, se puede poner en nano
						matrizTemp[counter][0] = wiStart;//guarda wiStart en la matriz, en la transicion que corresponde
						//matrizTemp[counter][1]+= wiStart;//alfa + wiStart
						//matrizTemp[counter][2]+= wiStart;//beta + wiStart
						matrizTemp[counter][4] = 1;//set sensibilizada
					}
					//else->antes estaba sensibilizada
				}
				else{//no esta sensibilizada
					if(matrizTemp[counter][4] == 1){//estaba sensibilizada
						matrizTemp[counter][4] = 0;
						matrizTemp[counter][3] = 0;
						matrizTemp[counter][0] = 0;
					}
					//else->no estaba sensibilizada
				}
				counter++;
			}
		}
	}

	private static boolean dispararTemporal(int pos) {
		/* -Si llega antes de que este sensibilizada entonces se duerme en la cola del semaforo que corresponde
		   -Si llega entre el wi y el alfa entonces sleep(alfa-(tiempo actual-wi))
		   -Si llega en la ventana se dispara
		   -Si llega y ya hay un id en la matriz, se duerme en la cola del semaforo que corresponde
		*/
		
		long arrival = System.currentTimeMillis();//tiempo en el que llega
		if(matrizTemp[pos][3]==0 || matrizTemp[pos][3]==Thread.currentThread().getId()) {//si ya hay un id guardado y no es suyo devuelvo falso
			if (ventana(arrival,pos)){	//disparable=true; esta en la ventana, el id guardado es suyo
					return true;//se dispara
				}
				
			
		else if(arrival>=matrizTemp[pos][0]){// no esta en la ventana, esta entre wi y alfa
			matrizTemp[pos][3]=Thread.currentThread().getId();	//guardo el id
			long alfaWistart = matrizTemp[pos][1]+matrizTemp[pos][0];
			RdP.setDormirse(true);
			RdP.setSleepTime(alfaWistart-arrival);
			//System.out.println("Llegue antes de la ventana, deberia dormirme. Alfa: "+alfaWistart+"ms. Arrival: "+arrival+"ms.");
			return false;
			
		}
		else{//no esta en la ventana, tampoco entre wi y alfa entonces debe estar antes de wi
			System.out.println("No estoy en la ventana ni antes de alfa, sssarann");
			return false;//se debe ir a la cola de la transicion
			}
		}
		//System.out.println("puedo disparar temporal,hilo: "+Thread.currentThread().getName());
		return false;
	}
//
	private static boolean ventana(long arrival, int t) {//esta o no en la ventana
		return (matrizTemp[t][1]+ matrizTemp[t][0]) <= arrival && (matrizTemp[t][2] + matrizTemp[t][0]) >= arrival;
	}
//
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
//
	public boolean esTemporal(int[][] secuencia) {
		int[][] sens = calcularAND(secuencia, temporales);//mira si la transicion es temporal
		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1) {
				return true;
			}
		}
		return false;
	}
	
	private static int isTemporal(int[][] secuencia) {//devuelve -1 si no es temp, >=0 si es temp y la posicion en matrizTemp
		int pos=-1;
		int[][] sens = calcularAND(secuencia, temporales);//mira si la transicion es temporal
		for (int i = 0; i < sens.length; i++) {
			if (sens[i][0] == 1) {
				pos = posicion(i);
			}
		}
		return pos;//posicion en matrizTemp
	}

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

}