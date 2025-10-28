/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author elika
 */
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bitacora {
    private static final Path LOG = Paths.get("bitacora.txt");
    private static final SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static synchronized void registrar(Class<?> src, String mensaje) {
        try {
            Files.createDirectories(LOG.getParent() == null ? Paths.get(".") : LOG.getParent());
            String line = "[" + F.format(new Date()) + "] " + src.getSimpleName() + " | " + mensaje + System.lineSeparator();
            Files.writeString(LOG, line, StandardCharsets.UTF_8,
            Files.exists(LOG) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException ignored) {}
    }

    public static synchronized List<String> leer() {
        try {
            if (!Files.exists(LOG)) return List.of();
            return Files.readAllLines(LOG, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return List.of("(Error al leer bitácora: " + e.getMessage() + ")");
        }
    }
}

