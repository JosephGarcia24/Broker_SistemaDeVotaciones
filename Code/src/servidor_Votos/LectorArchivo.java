/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author joseph
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitario de lectura de archivos de texto para obtener nombres de productos.
 * Cada línea no vacía del archivo se considera un nombre de producto.
 *
 * Ejemplo de archivo:
 *   Producto A
 *   Producto B
 *   Producto C
 *
 * Siempre retorna una lista (posiblemente vacía).
 */
public final class LectorArchivo {

    private LectorArchivo() {
        // Clase utilitaria: no instanciable
    }

    /**
     * Lee nombres de productos desde el archivo indicado.
     * Se ignoran líneas vacías y se hace {@code trim()} a cada línea.
     *
     * @param rutaArchivo ruta del archivo (relativa o absoluta)
     * @return lista de nombres de productos; vacía si hay error o no hay datos válidos
     */
    public static List<String> leerProductos(String rutaArchivo) {
        List<String> productos = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    productos.add(linea);
                }
            }
        } catch (IOException e) {
            // Log simple; se retorna lista vacía para ser tolerante a fallos.
            System.err.println("Error al leer el archivo '" + rutaArchivo + "': " + e.getMessage());
        }

        return productos;
    }
}
