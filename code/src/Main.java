import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author joseph
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = "src/Input/names.txt";
        ArrayList<String> namesList = FileTextReader.readFile(path); //Se obtiene los datos del archivo de texto y se pasan a un ArrayList
        namesList = TextFormatter.formatNames(namesList); //Se formatea la lista de nombres y se ordena
        Printer.printArrayList(namesList); //Se imprimen los datos en consola 

     
    }
    
}
