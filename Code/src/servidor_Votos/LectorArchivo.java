/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para leer archivos de configuración
 * Se utiliza para cargar la lista de productos desde un archivo de texto
 * 
 * @author joseph
 */
public class LectorArchivo {
    
    // ============================================
    // MÉTODO DE LECTURA DE ARCHIVOS
    // ============================================
    
    /**
     * Lee los nombres de productos desde un archivo de texto
     * Cada producto debe estar en una línea separada del archivo
     * Se ignoran líneas vacías y espacios en blanco al inicio y final
     * 
     * Ejemplo de contenido del archivo:
     * --------
     * Producto A
     * Producto B
     * Producto C
     * --------
     * 
     * @param rutaArchivo ruta completa o relativa al archivo a leer
     *                    Ejemplo: "recursos/productos.txt" o "C:\\datos\\productos.txt"
     * @return List<String> lista con los nombres de productos leídos del archivo
     *         Retorna una lista vacía si el archivo no existe o no contiene datos válidos
     *         No retorna null, siempre retorna una lista (posiblemente vacía)
     */
    public static List<String> leerProductos(String rutaArchivo) {
        
        // ============================================
        // INICIALIZACIÓN DE ESTRUCTURAS
        // ============================================
        
        // Crear lista para almacenar los productos
        List<String> productos = new ArrayList<>();
        
        // ============================================
        // LECTURA DEL ARCHIVO CON MANEJO DE RECURSOS
        // ============================================
        
        // Try-with-resources: cierra automáticamente el BufferedReader
        // incluso si ocurre una excepción
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            
            // ============================================
            // LECTURA LÍNEA POR LÍNEA
            // ============================================
            
            // Leer línea por línea hasta el final del archivo (readLine() retorna null)
            while ((linea = lector.readLine()) != null) {
                
                // Eliminar espacios en blanco al inicio y final de cada línea
                // Ejemplo: "  Producto A  " -> "Producto A"
                linea = linea.trim();
                
                // ============================================
                // VALIDACIÓN Y AGREGACIÓN
                // ============================================
                
                // Agregar la línea a la lista solo si no está vacía
                // Esto evita agregar líneas en blanco al resultado
                if (!linea.isEmpty()) {
                    productos.add(linea);
                }
            }
            
        } catch (IOException e) {
            // ============================================
            // MANEJO DE ERRORES
            // ============================================
            
            // Capturar excepciones relacionadas con lectura/escritura de archivos
            // Posibles causas:
            // - Archivo no encontrado (FileNotFoundException)
            // - Permisos insuficientes para leer el archivo
            // - Error de lectura del disco
            // - Ruta del archivo inválida
            
            // Mostrar mensaje de error descriptivo en la consola de errores
            System.err.println("Error al leer el archivo: " + e.getMessage());
            
            // La lista se retornará vacía en caso de error
        }
        
        // ============================================
        // RETORNO DE RESULTADOS
        // ============================================
        
        // Retornar lista con productos cargados
        // Si hubo error, la lista estará vacía
        return productos;
    }
}