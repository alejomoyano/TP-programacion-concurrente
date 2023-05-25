package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {
    private final Memoria memoria_uno, memoria_dos;
    private static int p1, p2, t1p2, t1p1;



    public Log(Memoria memoria_uno, Memoria memoria_dos) {
        this.memoria_uno = memoria_uno;
        this.memoria_dos = memoria_dos;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("logs/Tlog.txt"));
            pw.println("");
            pw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static synchronized void loggeo(int transicion){
        if(transicion == 5)
            t1p1++;
        else if(transicion == 6)
            t1p2++;
        else if(transicion == 3)
            p1++;
        else if(transicion == 4)
            p2++;
    }


    public void logger() {
        int pp = p1 * 100 / (p1 + p2);
        int mp = memoria_uno.getHistorial() * 100 / (memoria_uno.getHistorial() + memoria_dos.getHistorial());
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("logs/log.txt", true));
            pw.println("-----------------------------------");
            pw.println("+Procesador 1: " + p1 + "/1000" + "(" + pp + "%)\n" + "   Tarea 1: " + t1p1 + " (" + t1p1 * 100 / p1 +"%)\n   Tarea 2: " + (p1 - t1p1) + " (" + (p1 - t1p1) * 100 / p1 + "%)");
            pw.println("+Procesador 2: " + p2 + "/1000" + "(" + (100 - pp) + "%)\n" + "   Tarea 1: " + t1p2 + " (" + t1p2 * 100 / p2 +"%)\n   Tarea 2: " + (p2 - t1p2) + " (" + (p2 - t1p2) * 100 / p2 + "%)");
            pw.println("+Datos almacenados en memoria 1: " + memoria_uno.getHistorial() + "/1000" + "(" + mp + "%)");
            pw.println("+Datos almacenados en memoria 2: " + memoria_dos.getHistorial() + "/1000" + "(" + (100 - mp) + "%)");
            pw.println("-----------------------------------");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void Tlogger(int transicion) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileWriter("logs/Tlog.txt", true));
            pw.print("T" + transicion + "-");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
