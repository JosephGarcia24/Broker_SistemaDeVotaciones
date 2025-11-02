/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package broker;

/**
 *
 * @author elika
 */


import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servidor Broker concurrente (multicliente).
 * Acepta "registrar" con autodetección:
 *  - valor1 String  -> registro individual
 *  - valor1 Array   -> registro en lote (usa valor2/valor3 como defaults)
 * Mantiene compatibilidad con "registrarLote".
 *
 * Protocolo:
 *  - registrar     : servicio="registrar", valor1=String|Array, valor2=hostDefault?, valor3=puertoDefault?
 *  - registrarLote : servicio="registrarLote", valor1=Array, valor2=hostDefault?, valor3=puertoDefault?
 *  - listar        : servicio="listar"
 *  - ejecutar      : servicio="ejecutar", valor1=destino, valorN (N>=2) como parámetros
 *
 * Respuestas:
 *  - ok=true  : respuesta1=<clave>, valor1=<valor>
 *  - ok=false : respuesta1="error", valor1=<mensaje>
 *
 */
public final class BrokerServer {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Gson GSON = new Gson();
    private static final String LOG = "[Broker] ";

    private final int port;
    private final ServiceRegistry registry = new ServiceRegistry();

    /** Pool de hilos para atender conexiones concurrentes. */
    private final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger seq = new AtomicInteger(1);
        @Override public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "broker-client-" + seq.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    });

    /**
     * Crea un Broker en el puerto especificado.
     * @param port puerto TCP donde el broker escuchará.
     */
    public BrokerServer(int port) {
        this.port = port;
    }

    /**
     * Punto de entrada del Broker.
     * Uso: java broker.BrokerServer <puerto>
     * @param args argumentos: un entero con el puerto.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: broker <puerto>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            new BrokerServer(port).start();
        } catch (IOException | NumberFormatException e) {
            System.err.println(LOG + "Error al iniciar: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Inicia el servidor y atiende conexiones concurrentes.
     * @throws IOException si falla al abrir el ServerSocket.
     */
    public void start() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(LOG + "Apagando pool de hilos...");
            executor.shutdownNow();
        }, "broker-shutdown"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(LOG + "Escuchando en puerto " + port);
            while (!executor.isShutdown()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> {
                    try {
                        // clientSocket.setSoTimeout(15000); // opcional
                        handle(clientSocket);
                    } catch (Exception ex) {
                        System.err.println(LOG + "Conexión fallida: " + ex.getMessage());
                    } finally {
                        try { clientSocket.close(); } catch (IOException ignore) {}
                    }
                });
            }
        }
    }

    /**
     * Maneja una conexión: lee una línea JSON y responde.
     * @param socket socket conectado al cliente.
     */
    private void handle(Socket socket) {
        try (var in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
             var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET), true)) {

            String jsonLine = in.readLine();
            if (jsonLine == null || jsonLine.isBlank()) return;

            JsonObject request;
            try {
                request = JsonParser.parseString(jsonLine).getAsJsonObject();
            } catch (JsonSyntaxException parseEx) {
                out.println(error("JSON inválido: " + parseEx.getMessage()));
                return;
            }

            String servicio = getString(request, "servicio");
            if (servicio == null || servicio.isBlank()) {
                out.println(error("Falta 'servicio'"));
                return;
            }

            JsonObject response;
            response = switch (servicio) {
                case "registrar" -> handleRegistrarAuto(request);
                case "registrarLote" -> handleRegistrarBatch(request);
                case "listar" -> handleListar();
                case "ejecutar" -> handleEjecutar(request);
                default -> errorObj("Servicio broker no soportado: " + servicio);
            }; 

            out.println(GSON.toJson(response));

        } catch (Exception e) {
            System.err.println(LOG + "Error atendiendo conexión: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Manejo unificado de "registrar":
     * valor1 String  -> registro individual
     * valor1 Array   -> registro en lote
     * Otro tipo      -> error
     * @param request JSON con valor1, valor2?, valor3?
     * @return respuesta ok/error
     */
    private JsonObject handleRegistrarAuto(JsonObject request) {
        JsonElement v1 = request.get("valor1");
        if (v1 == null || v1.isJsonNull()) {
            return errorObj("registrar requiere 'valor1' (String o Array)");
        }
        if (v1.isJsonPrimitive() && v1.getAsJsonPrimitive().isString()) {
            return handleRegistrarSingle(request);
        }
        if (v1.isJsonArray()) {
            return handleRegistrarBatch(request);
        }
        return errorObj("registrar: 'valor1' debe ser String (nombre) o Array de servicios");
    }

    /**
     * Registro individual: valor1=nombre, valor2=host, valor3=puerto.
     * @param request JSON de la solicitud.
     * @return respuesta con identificador o error si faltan datos.
     */
    private JsonObject handleRegistrarSingle(JsonObject request) {
        String serviceName = getString(request, "valor1");
        String host = getString(request, "valor2");
        Integer servicePort = getInt(request, "valor3");

        if (serviceName == null || host == null || servicePort == null) {
            return errorObj("Solicitud 'registrar' incompleta (falta valor1/2/3)");
        }
        int id = registry.registrar(serviceName, host, servicePort);
        return okKV("identificador", new JsonPrimitive(id));
    }

    /**
     * Registro en lote: valor1=[{nombre, host?, puerto?}, ...].
     * Usa valor2 (host) y valor3 (puerto) como defaults si un item no los trae.
     * @param request JSON con arreglo en valor1 y defaults opcionales.
     * @return resumen con éxitos, fallos y detalles.
     */
    private JsonObject handleRegistrarBatch(JsonObject request) {
        String defaultHost = getString(request, "valor2");
        Integer defaultPort = getInt(request, "valor3");

        JsonElement v1 = request.get("valor1");
        if (v1 == null || !v1.isJsonArray()) {
            return errorObj("registrar(lote) requiere valor1 como arreglo de servicios");
        }

        JsonArray servicesArray = v1.getAsJsonArray();
        JsonArray registeredArray = new JsonArray();
        int successCount = 0, failCount = 0;

        for (JsonElement element : servicesArray) {
            try {
                JsonObject svc = element.getAsJsonObject();

                String name = svc.has("nombre") && !svc.get("nombre").isJsonNull()
                        ? svc.get("nombre").getAsString()
                        : null;

                String host = svc.has("host") && !svc.get("host").isJsonNull()
                        ? svc.get("host").getAsString()
                        : defaultHost;

                Integer port = (svc.has("puerto") && !svc.get("puerto").isJsonNull())
                        ? svc.get("puerto").getAsInt()
                        : defaultPort;

                if (name == null || host == null || port == null) {
                    failCount++;
                    continue;
                }

                int id = registry.registrar(name, host, port);

                JsonObject okItem = new JsonObject();
                okItem.addProperty("nombre", name);
                okItem.addProperty("id", id);
                okItem.addProperty("host", host);
                okItem.addProperty("puerto", port);
                registeredArray.add(okItem);
                successCount++;
            } catch (Exception ex) {
                failCount++;
            }
        }

        JsonObject summary = new JsonObject();
        summary.addProperty("exitos", successCount);
        summary.addProperty("fallidos", failCount);
        summary.add("detalles", registeredArray);
        return okKV("registrados", summary);
    }

    /**
     * Devuelve los servicios registrados.
     * @return respuesta con arreglo de servicios y direcciones.
     */
    private JsonObject handleListar() {
        JsonArray services = new JsonArray();
        for (ServiceRegistry.Entry entry : registry.listar()) {
            JsonObject item = new JsonObject();
            item.addProperty("servicio", entry.servicio);
            item.addProperty("direccion", entry.host + ":" + entry.port);
            services.add(item);
        }
        return okKV("servicios", services);
    }

    /**
     * Reenvía la solicitud al servidor que implementa el destino.
     * @param request JSON de la solicitud con valor1=destino y demás valorN.
     * @return respuesta del servidor destino o error si no existe.
     */
    private JsonObject handleEjecutar(JsonObject request) {
        String targetService = getString(request, "valor1");
        if (targetService == null || targetService.isBlank()) {
            return errorObj("Falta 'valor1' (destino) en ejecutar");
        }

        InetSocketAddress targetAddress = registry.pick(targetService);
        if (targetAddress == null) {
            return errorObj("Servicio no disponible: " + targetService);
        }

        JsonObject forwardRequest = buildForwardRequest(request, targetService);
        try {
            return Forwarder.forward(targetAddress, forwardRequest);
        } catch (IOException forwardEx) {
            return errorObj("Fallo al reenviar a " + targetAddress + ": " + forwardEx.getMessage());
        }
    }

    /**
     * Construye la solicitud a reenviar copiando todos los "valorN" excepto "valor1".
     * @param originalRequest JSON original del cliente.
     * @param targetService nombre del servicio destino.
     * @return JSON listo para reenviar.
     */
    private static JsonObject buildForwardRequest(JsonObject originalRequest, String targetService) {
        JsonObject forward = new JsonObject();
        forward.addProperty("servicio", targetService);

        for (Map.Entry<String, JsonElement> entry : originalRequest.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("valor")) {
                try {
                    int index = Integer.parseInt(key.substring("valor".length()));
                    if (index == 1) continue; // omitir destino
                } catch (NumberFormatException ignore) {}
                forward.add(key, entry.getValue());
            }
        }
        return forward;
    }

    /** Utilidades JSON y respuestas */

    private static String getString(JsonObject json, String key) {
        return (json.has(key) && !json.get(key).isJsonNull()) ? json.get(key).getAsString() : null;
    }

    private static Integer getInt(JsonObject json, String key) {
        try {
            return (json.has(key) && !json.get(key).isJsonNull()) ? json.get(key).getAsInt() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static JsonObject okKV(String responseKey, JsonElement responseValue) {
        JsonObject response = new JsonObject();
        response.addProperty("ok", true);
        response.addProperty("respuesta1", responseKey);
        response.add("valor1", responseValue);
        return response;
    }

    private static JsonObject errorObj(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("ok", false);
        response.addProperty("respuesta1", "error");
        response.addProperty("valor1", message == null ? "error desconocido" : message);
        return response;
    }

    private static String error(String message) {
        return GSON.toJson(errorObj(message));
    }
}
