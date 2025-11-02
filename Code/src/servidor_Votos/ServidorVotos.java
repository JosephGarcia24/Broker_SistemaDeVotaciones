/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
* @author elika
*/

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Servidor de votaciones.
 * - Expone servicios: productos, contar, votar, bitacora.listar
 * - Se registra en el Broker en una sola petición (registro por lote).
 * - Si el lote falla, intenta registrar cada servicio de manera individual.
 *
 * Uso:
 *   java servidor_Votos.ServidorVotos <puerto> <brokerHost:brokerPort> [rutaProductosOpcional]
 *
 * Ejemplos:
 *   java servidor_Votos.ServidorVotos 5091 127.0.0.1:5090
 *   java servidor_Votos.ServidorVotos 5091 127.0.0.1:5090 recursos/productos.txt
 */
public class ServidorVotos {

    private final int port;
    private final DominioModelo modelo;

    /**
     * Crea el servidor con el puerto y el modelo de dominio.
     * @param port puerto TCP para escuchar.
     * @param modelo dominio con productos y votos.
     */
    public ServidorVotos(int port, DominioModelo modelo) {
        this.port = port;
        this.modelo = modelo;
    }

    /**
     * Punto de entrada.
     * @param args arg0=puerto, arg1=brokerHost:brokerPort, arg2=rutaProductos opcional.
     * @throws Exception en errores de inicialización.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: servidor <puerto> <brokerHost:brokerPort> [rutaProductos]");
            return;
        }

        int port = Integer.parseInt(args[0]);

        String[] bh = args[1].split(":");
        String brokerHost = bh[0];
        int brokerPort = Integer.parseInt(bh[1]);

        // Carga de productos: si hay tercer argumento úsalo; si no, productos.txt en directorio actual.
        String rutaProductos = (args.length >= 3) ? args[2] : "productos.txt";
        List<String> productos = LectorArchivo.leerProductos(rutaProductos);
        if (productos.isEmpty()) productos = List.of("Windows", "Linux", "macOS");

        DominioModelo modelo = new DominioModelo(productos);

        // Registro en el broker por lote (un solo paquete). Si falla, fallback a individual.
        boolean ok = registrarLote(brokerHost, brokerPort, port);
        if (!ok) {
            registrarIndividual(brokerHost, brokerPort, "productos", port);
            registrarIndividual(brokerHost, brokerPort, "contar", port);
            registrarIndividual(brokerHost, brokerPort, "votar", port);
            registrarIndividual(brokerHost, brokerPort, "bitacora.listar", port);
        }

        new ServidorVotos(port, modelo).start();
    }

    /**
     * Inicia el servidor y atiende conexiones (un hilo por conexión).
     * @throws IOException si falla el ServerSocket.
     */
    public void start() throws IOException {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[Servidor] Votos escuchando en puerto " + port);
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handle(s), "srvvotos-" + s.getPort()).start();
            }
        }
    }

    /**
     * Maneja una conexión entrante: lee una línea JSON, procesa y responde.
     * @param s socket del cliente.
     */
    private void handle(Socket s) {
        try (s;
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) return;

            JsonObject req = JsonParser.parseString(line).getAsJsonObject();
            JsonObject resp = ServiceHandler.procesar(req, modelo);
            out.println(resp.toString());

        } catch (Exception e) {
            // Mantener el servidor vivo ante errores por conexión
        }
    }

    // ------------------------------------------------------------
    // Registro en Broker
    // ------------------------------------------------------------

    /**
     * Registro por lote usando "registrar" con valor1 como arreglo (lo que el Broker espera).
     * Usa valor2 y valor3 como defaults para host y puerto.
     * @param host broker host.
     * @param port broker port.
     * @param miPuerto puerto de este servidor.
     * @return true si el lote fue aceptado, false si hubo error (para hacer fallback).
     */
    private static boolean registrarLote(String host, int port, int miPuerto) {
        try (Socket s = new Socket(host, port);
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String local = InetAddress.getLocalHost().getHostAddress();

            JsonArray servicios = new JsonArray();
            for (String nombre : List.of("productos", "contar", "votar", "bitacora.listar")) {
                JsonObject o = new JsonObject();
                o.addProperty("nombre", nombre);
                servicios.add(o);
            }

            JsonObject req = new JsonObject();
            req.addProperty("servicio", "registrar");  // también podría ser "registrarLote"
            req.add("valor1", servicios);              // arreglo en valor1 (clave que el Broker autodetecta)
            req.addProperty("valor2", local);          // default host opcional
            req.addProperty("valor3", miPuerto);       // default puerto opcional

            out.println(req.toString());
            String respuesta = in.readLine(); // opcional: inspeccionar si quieres
            System.out.println("[Servidor] Registro por lote OK: " + respuesta);
            return true;

        } catch (Exception e) {
            System.err.println("[Servidor] Error en registro por lote: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registro individual de un servicio.
     * @param host broker host.
     * @param port broker port.
     * @param nombre nombre del servicio (productos, contar, votar, bitacora.listar).
     * @param miPuerto puerto de este servidor.
     */
    private static void registrarIndividual(String host, int port, String nombre, int miPuerto) {
        try (Socket s = new Socket(host, port);
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String local = InetAddress.getLocalHost().getHostAddress();

            JsonObject req = new JsonObject();
            req.addProperty("servicio", "registrar");
            req.addProperty("valor1", nombre);
            req.addProperty("valor2", local);
            req.addProperty("valor3", miPuerto);

            out.println(req.toString());
            String resp = in.readLine(); // se puede ignorar
            System.out.println("[Servidor] Registrado servicio '" + nombre + "' en " + host + ":" + port + " -> " + resp);

        } catch (Exception e) {
            System.err.println("[Servidor] No se pudo registrar '" + nombre + "': " + e.getMessage());
        }
    }
}
