
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author A20203174
 */
import java.util.ArrayList;

public class Printer {

    /**
     * Método que recibe un ArrayList de Strings y lo imprime en pantalla.
     * @param items ArrayList de cadenas a imprimir.
     */
    public static void printArrayList(ArrayList<String> items) {
        // Recorrer cada elemento del ArrayList y mostrarlo
        for (String item : items) {
            System.out.println(item);
        }
    }
    
}
