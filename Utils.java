import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Utils {
    public Utils(){}


    /**
     * Metodo que devuelve la indice de la transicion
     * @param transicion transicion de la que queremos saber el indice
     * @return -1 si se paso una secuencia vacia, >=0 si tiene transicion
     */
    public static int getIndex(int[][] transicion){
        for (int i = 0; i < transicion.length; i++) {
            if(transicion[i][0] == 1)
                return i;
        }
        return -1;
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

    /**
     * And entre dos matrices
     * @param primeraMatriz
     * @param segundaMatriz
     * @return transiciones sensibilizadas que son temporales
     */
    public static int[][] calcularAND(int[][] primeraMatriz, int[][] segundaMatriz) {
        int[][] resultado = new int[17][1];
        for (int i = 0; i < 17; i++) {
            resultado[i][0] = primeraMatriz[i][0] & segundaMatriz[i][0];
        }
        return resultado;
    }

}
