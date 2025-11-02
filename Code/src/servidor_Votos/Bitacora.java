/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author elika
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Bitácora mínima en archivo de texto.
 * Escribe una línea por evento y permite leer el contenido completo.
 *
 * Formato de línea:
 *   [yyyy-MM-dd HH:mm:ss] NombreClase | mensaje
 *
 * Archivo:
 *   bitácora por defecto en "bitacora.txt" en el directorio de ejecución.
 *
 * Nota: usa {@link SimpleDateFormat}; al estar los métodos sincronizados,
 * su uso es seguro en este contexto.
 */
public final class Bitacora {

    /** Ruta del archivo de bitácora. */
    private static final Path ARCHIVO = Paths.get("bitacora.txt");

    /** Formato de fecha para cada línea de log. */
    private static final SimpleDateFormat FORMATO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Bitacora() {
        // Clase utilitaria: no instanciable
    }

    /**
     * Registra una línea en la bitácora.
     * Ejemplo:
     *   [2025-11-02 10:15:23] MiClase | Mensaje
     *
     * @param origen  clase de origen del mensaje
     * @param mensaje mensaje a registrar
     */
    public static synchronized void registrar(Class<?> origen, String mensaje) {
        try {
            // Asegura que el directorio exista (si hubiera directorio padre)
            Path parent = ARCHIVO.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String clase = (origen == null) ? "Desconocido" : origen.getSimpleName();
            String linea = "[" + FORMATO.format(new Date()) + "] " + clase + " | " + mensaje + System.lineSeparator();

            Files.writeString(
                ARCHIVO,
                linea,
                StandardCharsets.UTF_8,
                Files.exists(ARCHIVO) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE
            );
        } catch (IOException ignored) {
            // En bitácora preferimos no propagar; fallo silencioso para no interrumpir la app.
        }
    }

    /**
     * Lee todas las líneas de la bitácora.
     *
     * @return lista de líneas; si no existe, lista vacía; si falla, una línea con el error.
     */
    public static synchronized List<String> leer() {
        try {
            if (!Files.exists(ARCHIVO)) {
                return List.of();
            }
            return Files.readAllLines(ARCHIVO, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return List.of("(Error al leer bitácora: " + e.getMessage() + ")");
        }
    }
}
