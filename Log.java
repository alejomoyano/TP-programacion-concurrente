import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {
    private Memoria m1, m2;
    private static int p1, p2, t1p2, t1p1;
    //private static PrintWriter pw;

    public Log(Memoria m1, Memoria m2) {
        this.m1 = m1;
        this.m2 = m2;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("Tlog.txt"));
            pw.println(""); // Borro el texto del log anterior
            pw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void logTareas(int[][] sec) {//revisa si es una t1p1  o una t1p2
        if (sec[5][0] == 1) {
            t1p1++;
        } else if (sec[6][0] == 1) {
            t1p2++;
        }
    }

    public static void logProcesadores(int[][] sec) {//revisa que procesador se ejecuta
        if (sec[3][0] == 1) {
            p1++;
        } else {
            p2++;
        }
    }

    public void logger() {
        int pp = p1 * 100 / (p1 + p2);
        int mp = m1.getHistorial() * 100 / (m1.getHistorial() + m2.getHistorial());
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("log.txt", true));
            pw.println("-----------------------------------");
            pw.println("+Procesador 1: " + p1 + "/1000" + "(" + pp + "%)\n" + "   Tarea 1: " + t1p1 + "\n   Tarea 2: " + (p1 - t1p1));
            pw.println("+Procesador 2: " + p2 + "/1000" + "(" + (100 - pp) + "%)\n" + "   Tarea 1: " + t1p2 + "\n   Tarea 2: " + (p2 - t1p2));
            pw.println("+Datos almacenados en memoria 1: " + m1.getHistorial() + "/1000" + "(" + mp + "%)");
            pw.println("+Datos almacenados en memoria 2: " + m2.getHistorial() + "/1000" + "(" + (100 - mp) + "%)");
            pw.println("-----------------------------------");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void Tlogger(int[][] secuencia) { //esto se ejecuta adentro del monitor, hace falta que sea synchronized?
        for (int i = 0; i < 17; i++) {
            if (secuencia[i][0] == 1) {
                PrintWriter pw;
                try {
                    pw = new PrintWriter(new FileWriter("Tlog.txt", true));
                    pw.print("T" + i);
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }

        }
    }
}
