package broker;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author elika
 */

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class BrokerServer {
    private final int port;
    private final ServiceRegistry registry = new ServiceRegistry();

    public BrokerServer(int port) { this.port = port; }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) { System.out.println("Uso: broker <puerto>"); return; }
        int port = Integer.parseInt(args[0]);
        new BrokerServer(port).start();
    }

    public void start() throws IOException {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[Broker] Escuchando en puerto " + port);
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
            String servicio = req.get("servicio").getAsString();

            JsonObject resp;
            switch (servicio) {
                case "registrar": { // valor1=nombre, valor2=host, valor3=puerto
                    String name = req.get("valor1").getAsString();
                    String host = req.get("valor2").getAsString();
                    int    port = req.get("valor3").getAsInt();
                    int id = registry.registrar(name, host, port);
                    resp = okKV("identificador", new JsonPrimitive(id));
                    break;
                }
                case "listar": {
                    JsonArray arr = new JsonArray();
                    for (var m : registry.listar()) {
                        JsonObject o = new JsonObject();
                        o.addProperty("servicio", m.servicio);
                        o.addProperty("direccion", m.host + ":" + m.port);
                        arr.add(o);
                    }
                    resp = okKV("servicios", arr);
                    break;
                }
                case "ejecutar": {
                    // 1) Leer el destino
                    String destino = req.get("valor1").getAsString(); // p.ej. "votar", "contar", etc.
                    InetSocketAddress addr = registry.pick(destino);
                    if (addr == null) { resp = error("Servicio no disponible: " + destino); break; }

                    // 2) Construir una NUEVA solicitud para el servidor destino:
                    //    - Cambiar "servicio" por el destino
                    //    - Copiar todos los "valorN" EXCEPTO el valor1 (que era el 'destino')
                    JsonObject fwd = new JsonObject();
                    fwd.addProperty("servicio", destino);

                    // Copiar pares valorN (y si quieres paramN) excepto N=1
                    for (String key : req.keySet()) {
                        if (key.startsWith("valor")) {
                            try {
                                int n = Integer.parseInt(key.substring("valor".length()));
                                if (n == 1) continue; // saltar el destino
                            } catch (NumberFormatException ignore) {}
                            fwd.add(key, req.get(key));
                        }
                        // (Opcional) si te interesa conservar paramN (no los usa el server)
                        // if (key.startsWith("param")) { ... }
                    }

                    // 3) Reenviar ya con el servicio correcto
                    resp = Forwarder.forward(addr, fwd);
                    break;
                }
                default: resp = error("Servicio broker no soportado: " + servicio);
            }

            out.println(resp.toString());
        } catch (Exception e) {
          // respuesta de error, pero sin romper el ciclo del servidor
        }
    }

    private JsonObject okKV(String k, JsonElement v){var o=new JsonObject();o.addProperty("ok",true);o.addProperty("respuesta1",k);o.add("valor1",v);return o;}
    private JsonObject error(String m){var o=new JsonObject();o.addProperty("ok",false);o.addProperty("respuesta1","error");o.addProperty("valor1",m);return o;}
}
