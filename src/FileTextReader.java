/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author A20203174
 */
import java.io.*;
import java.util.ArrayList;

public class FileTextReader {

    /**
     * Método que lee un archivo de texto y devuelve su contenido en una lista de Strings.
     * @param path Ruta del archivo a leer.
     * @return Lista de cadenas con todas las líneas del archivo.
     */
    
    public static ArrayList<String> readFile(String path) {
        ArrayList<String> fileContent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            
            String line;
            // Se lee línea por línea hasta que no haya más (readLine devuelve null al final del archivo)
            while ((line = reader.readLine()) != null) 
                fileContent.add(line);
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return fileContent;
    }
}


