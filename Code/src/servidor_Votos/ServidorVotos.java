/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author elika
 */
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServidorVotos {
    private final int port;
    private final DominioModelo modelo;

    public ServidorVotos(int port, DominioModelo modelo) {
        this.port = port; this.modelo = modelo;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) { System.out.println("Uso: servidor <puerto> <brokerHost:brokerPort>"); return; }
        int port = Integer.parseInt(args[0]);
        String[] bh = args[1].split(":");
        String brokerHost = bh[0];
        int brokerPort    = Integer.parseInt(bh[1]);

        List<String> productos = LectorArchivo.leerProductos("productos.txt");
        if (productos.isEmpty()) productos = List.of("Windows","Linux","macOS");
        DominioModelo modelo = new DominioModelo(productos);

        registrar(brokerHost, brokerPort, "productos",port);
        registrar(brokerHost, brokerPort, "contar",port);
        registrar(brokerHost, brokerPort, "votar",port);
        registrar(brokerHost, brokerPort, "bitacora.listar",port);

        new ServidorVotos(port, modelo).start();
    }

    private static void registrar(String host, int port, String nombre, int miPuerto) {
        try (Socket s = new Socket(host, port);
            var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {
            JsonObject req = new JsonObject();
            req.addProperty("servicio","registrar");
            req.addProperty("param1","nombre"); req.addProperty("valor1", nombre);
            req.addProperty("param2","host");   req.addProperty("valor2", InetAddress.getLocalHost().getHostAddress());
            req.addProperty("param3","puerto"); req.addProperty("valor3", miPuerto);
            out.println(req.toString());
            in.readLine(); // ignorar respuesta
            System.out.println("[Servidor] Registrado servicio '" + nombre + "' en broker " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("[Servidor] No se pudo registrar '" + nombre + "': " + e.getMessage());
        }
    }

    public void start() throws IOException {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[Servidor] Votos escuchando en puerto " + port);
            while (true) {
              Socket s = ss.accept();
              new Thread(() -> handle(s)).start();
            }
        }
    }

    private void handle(Socket s) {
        try (s;
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine(); if (line == null) return;
            JsonObject req = JsonParser.parseString(line).getAsJsonObject();
            JsonObject resp = ServiceHandler.procesar(req, modelo);
            out.println(resp.toString());

        } catch (Exception e) {
          // ignora para mantener el servidor vivo
        }
    }
}

