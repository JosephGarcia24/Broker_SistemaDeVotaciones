/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joseph
 */

package servidor_Votos;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ManejadorVotos {
    private static final String CARPETA_VOTOS = "votos";
    
    // Constructor que asegura que existe la carpeta de votos
    public ManejadorVotos() {
        File carpeta = new File(CARPETA_VOTOS);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }
    }
    
      // Obtener la ruta del archivo de votos para un producto
    private String obtenerRutaArchivo(String nombreProducto) {
        // Nombre seguro: reemplaza caracteres especiales
        String nombreSeguro = nombreProducto.replaceAll("[^a-zA-Z0-9]", "_");
        return CARPETA_VOTOS + File.separator + nombreSeguro + ".txt";
    }
    
    // Obtener el número de votos de un producto (contando líneas)
    public int obtenerVotos(String nombreProducto) {
        String rutaArchivo = obtenerRutaArchivo(nombreProducto);
        int conteo = 0;
        
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            while (lector.readLine() != null) {
                conteo++;
            }
        } catch (IOException e) {
            // Si el archivo no existe, retorna 0
            return 0;
        }
        
        return conteo;
    }
    
     public void registrarVoto(String nombreProducto) {
        String rutaArchivo = obtenerRutaArchivo(nombreProducto);
        
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            // Escribir una línea con la marca de voto (puede ser cualquier cosa)
            escritor.write("voto");
            escritor.newLine();
        } catch (IOException e) {
            System.err.println("Error al registrar voto para " + nombreProducto + ": " + e.getMessage());
        }
    }
}
