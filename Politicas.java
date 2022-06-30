import java.util.Random;

public class Politicas {
	private RdP Red;
//	private int[][] conflictos;
	
	public Politicas(RdP red) {
		this.Red=red;
		
//		conflictos=new int[][]{{0},{1},{1},{0},{0},{1},{1},{0},{0},{1},{1},{1},{1},{1},{1},{0},{0}};
	}


	/**
	 * Metodo que resuelve los conflicos estructurales.
	 * @param secuencia: secuencia que contiene la tansicion con conflicto que quiere ser ejecutada
	 * @return secuencia a ejecutar
	 */
	public int[][] HayConflicto(int[][] secuencia){

		Random random = new Random();
		boolean hayConflicto=false;
		int indice=0;
//
// 		int[][] transicion= new int[17][1];
//		int[][] conflicto= new int[17][0];
//
//		conflicto= RdP.calcularAND(secuencia, conflictos);	//la transicion a disparar tiene conflicto?
//		for(int i=0;i< conflicto.length;i++) {
//			if(conflicto[i][0]==1) {
//				indice=i;
//				hayConflicto=true;
//				break;
//			}
//		}

		indice = RdP.tieneConflicto(secuencia);

		// coloca true si >= 0 y false si == -1
		hayConflicto = indice != -1;

		if(hayConflicto) {
			if(indice==1 || indice==2) {	//conflicto para elegir el procesador
				 secuencia[this.ConflictoProcesador()][0]=1;
				 return secuencia;
			}
			
			if(indice==5 || indice==13) {	//conflicto Tareas en procesador1- 50% de probabilidad cada uno
				int n = random.nextInt(2);
				if(n==0) {
					secuencia[5][0]=1;
					return secuencia;
				}
				else {
					secuencia[13][0]=1;
					return secuencia;
				}
			}
			
			if(indice==6 || indice==14) {	//conflicto en tareas procesador2- 50% de probabilidad cada uno
				int n = random.nextInt(2);
				if(n==0) {
					secuencia[6][0]=1;
					return secuencia;
				}
				else {
					secuencia[14][0]=1;
					return secuencia;
				}
			
			}
			
			if(indice==9 || indice==10) {	//conflicto en memorias desde procesador1
				secuencia[this.ConflictoMemoriasP1()][0]=1;
				return secuencia;
			}
			
			if(indice==11 || indice==12) {	//conflicto en memorias desde procesador2
				secuencia[this.ConflictoMemoriasP2()][0]=1;
				return secuencia;
			}
		}
		return secuencia;	//si no hay conflicto,  devuelvo la transicion original
	}
	
	public int ConflictoProcesador() {
		int n=0;
		int marca[][]=Red.getMarcado();
		if(marca[0][0]<marca[1][0]) {
			return n=1;//devolver la transicion AsignarP1
		}
		if(marca[0][0]>marca[1][0]) {
			return n=2;//devolver la transicion AsignarP2
		}
			// Obtain a number between [1 - 2].
		n = (int) (Math.random() * 2) + 1;
		return n;
		
	}
	
	public int ConflictoMemoriasP1() {
		Random rand = new Random();

		int n=0;
		int marca[][]=Red.getMarcado();
		if(marca[9][0]<marca[10][0]) {
			return n=9;		//devolver la transicion P1M1	
		}
		if(marca[9][0]>marca[10][0]) {
			return n=10;	//devolver la transicion P1M2
		}
			// Obtain a number between [0 - 1].
		int s = rand.nextInt(2);
			if(s==0) {
				return 9;
			}
			else {
				return 10;
			}
	}
	
	public int ConflictoMemoriasP2() {
		Random rand = new Random();
		int n=0;
		int marca[][]=Red.getMarcado();
		
		if(marca[9][0]<marca[10][0]) {
			return n=11;	//devolver la transicion P2M1
		}
		if(marca[9][0]>marca[10][0]) {
			return n=12;//devolver la transicion P2M2
		}
			// Obtain a number between [0 - 1].
		int s = rand.nextInt(2);
			if(s==0) {
				return 11;
			}
			else {
				return 12;
			}
	}
}
	
