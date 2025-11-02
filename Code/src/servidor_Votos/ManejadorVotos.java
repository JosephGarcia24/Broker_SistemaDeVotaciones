/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package servidor_Votos;
/**
 *
 * @author joseph
 */

import java.io.*;

/**
 * Persistencia simple de votos por producto mediante archivos.
 * Por cada producto se mantiene un archivo en la carpeta {@code votos/}
 * y cada línea representa un voto registrado.
 */
public final class ManejadorVotos {

    /** Carpeta raíz donde se guardan archivos de votos. */
    private static final String CARPETA_VOTOS = "votos";

    /**
     * Crea el manejador y asegura la existencia de la carpeta de votos.
     * Si no existe, la crea.
     */
    public ManejadorVotos() {
        File carpeta = new File(CARPETA_VOTOS);
        if (!carpeta.exists()) {
            // mkdir() crea un nivel; mkdirs() cubriría rutas más profundas.
            carpeta.mkdir();
        }
    }

    /**
     * Devuelve la ruta del archivo asociado a un producto.
     * Reemplaza caracteres no alfanuméricos por '_'.
     *
     * @param nombreProducto nombre del producto
     * @return ruta de archivo donde se guardan sus votos
     */
    private String obtenerRutaArchivo(String nombreProducto) {
        String nombreSeguro = nombreProducto.replaceAll("[^a-zA-Z0-9]", "_");
        return CARPETA_VOTOS + File.separator + nombreSeguro + ".txt";
    }

    /**
     * Obtiene el número de votos de un producto (equivalente a contar líneas del archivo).
     *
     * @param nombreProducto nombre del producto
     * @return cantidad de votos; 0 si no hay archivo o no se puede leer
     */
    public int obtenerVotos(String nombreProducto) {
        String ruta = obtenerRutaArchivo(nombreProducto);
        int conteo = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            while (lector.readLine() != null) {
                conteo++;
            }
        } catch (IOException e) {
            // Si el archivo no existe o hay error de lectura, devolvemos 0.
            return 0;
        }

        return conteo;
    }

    /**
     * Registra un voto para el producto (agrega una línea al archivo).
     *
     * @param nombreProducto nombre del producto
     */
    public void registrarVoto(String nombreProducto) {
        String ruta = obtenerRutaArchivo(nombreProducto);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta, true))) {
            escritor.write("voto");
            escritor.newLine();
        } catch (IOException e) {
            System.err.println("Error al registrar voto para " + nombreProducto + ": " + e.getMessage());
        }
    }
}
