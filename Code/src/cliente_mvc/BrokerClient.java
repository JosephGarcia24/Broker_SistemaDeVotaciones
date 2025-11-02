package cliente_mvc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joseph
 */


import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Cliente ligero para comunicarse con el Broker.
 * Envía una línea JSON por petición y espera una línea JSON por respuesta.
 *
 * Protocolo usado:
 * - Para ejecutar servicios del backend vía Broker: servicio = "ejecutar", valor1 = destino
 * - Para listar servicios registrados en el Broker: servicio = "listar"
 */
public class BrokerClient {

    /** Timeout de conexión y lectura en milisegundos. */
    private static final int TIMEOUT_MS = 4000;

    private final String host;
    private final int port;

    /**
     * Crea un cliente apuntando al host/puerto del Broker.
     *
     * @param host nombre o IP del Broker
     * @param port puerto TCP del Broker
     */
    public BrokerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // ---------------------------------------------------------------------
    // Helpers internos
    // ---------------------------------------------------------------------

    /**
     * Envía una petición JSON al Broker y devuelve la respuesta como JsonObject.
     * Lanza IOException si hay problema de red o si la respuesta viene vacía.
     */
    private JsonObject call(JsonObject request) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                out.println(request.toString());

                String line = in.readLine();
                if (line == null) {
                    throw new IOException("El Broker cerró la conexión sin responder");
                }
                return JsonParser.parseString(line).getAsJsonObject();
            }
        }
    }

    /**
     * Construye una petición estándar para "ejecutar" un servicio destino.
     *
     * @param destino nombre del servicio destino (p.ej. "votar", "contar")
     * @return objeto JSON listo para enviarse al Broker
     */
    private static JsonObject buildExecuteRequest(String destino) {
        JsonObject req = new JsonObject();
        req.addProperty("servicio", "ejecutar");
        // campos paramN/valorN son opcionales para el broker; aquí se usa patrón conocido
        req.addProperty("param1", "destino");
        req.addProperty("valor1", destino);
        return req;
    }

    /**
     * Verifica bandera "ok" en la respuesta del Broker.
     */
    private static boolean isOk(JsonObject resp) {
        return resp != null && resp.has("ok") && resp.get("ok").getAsBoolean();
    }

    /**
     * Obtiene "valor1" como JsonObject si existe y es objeto.
     */
    private static JsonObject getValor1AsObject(JsonObject resp) {
        if (resp == null || !resp.has("valor1") || resp.get("valor1").isJsonNull()) return null;
        JsonElement el = resp.get("valor1");
        return el.isJsonObject() ? el.getAsJsonObject() : null;
    }

    /**
     * Obtiene "valor1" como JsonArray si existe y es arreglo.
     */
    private static JsonArray getValor1AsArray(JsonObject resp) {
        if (resp == null || !resp.has("valor1") || resp.get("valor1").isJsonNull()) return null;
        JsonElement el = resp.get("valor1");
        return el.isJsonArray() ? el.getAsJsonArray() : null;
    }

    // ---------------------------------------------------------------------
    // API pública 
    // ---------------------------------------------------------------------

    /**
     * Solicita al Broker ejecutar el servicio "votar" con el nombre de producto.
     *
     * @param producto nombre del producto
     * @return true si el Broker respondió ok=true; false en caso de error
     */
    public boolean votar(String producto) {
        try {
            JsonObject req = buildExecuteRequest("votar");
            // Adjunta el parámetro de negocio
            req.addProperty("param2", "producto");
            req.addProperty("valor2", Objects.toString(producto, ""));

            JsonObject resp = call(req);
            return isOk(resp);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Pide al Broker ejecutar "contar" y devuelve un mapa nombre->votos.
     *
     * @return mapa con conteos por producto; vacío si hay error
     */
    public Map<String, Integer> contar() {
        try {
            JsonObject req = buildExecuteRequest("contar");
            JsonObject resp = call(req);
            if (!isOk(resp)) return Map.of();

            JsonObject counts = getValor1AsObject(resp);
            if (counts == null) return Map.of();

            Map<String, Integer> out = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> e : counts.entrySet()) {
                out.put(e.getKey(), e.getValue().getAsInt());
            }
            return out;
        } catch (IOException e) {
            return Map.of();
        }
    }

    /**
     * Pide al Broker ejecutar "productos" y devuelve la lista de nombres.
     *
     * @return lista de productos; vacía si hay error
     */
    public List<String> productos() {
        try {
            JsonObject req = buildExecuteRequest("productos");
            JsonObject resp = call(req);
            if (!isOk(resp)) return List.of();

            JsonArray arr = getValor1AsArray(resp);
            if (arr == null) return List.of();

            List<String> out = new ArrayList<>(arr.size());
            for (JsonElement el : arr) out.add(el.getAsString());
            return out;
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Lista los servicios registrados en el Broker (directo al Broker).
     *
     * @return lista de pares {servicio, direccion}; vacía si hay error
     */
    public List<String[]> listarServicios() {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("servicio", "listar");

            JsonObject resp = call(req);
            if (!isOk(resp)) return List.of();

            JsonArray arr = getValor1AsArray(resp);
            if (arr == null) return List.of();

            List<String[]> out = new ArrayList<>(arr.size());
            for (JsonElement el : arr) {
                JsonObject o = el.getAsJsonObject();
                String servicio = o.has("servicio") ? o.get("servicio").getAsString() : "";
                String direccion = o.has("direccion") ? o.get("direccion").getAsString() : "";
                out.add(new String[]{servicio, direccion});
            }
            return out;
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Pide al Broker ejecutar "bitacora.listar" y devuelve las líneas.
     *
     * @return líneas de bitácora; lista vacía si hay error
     */
    public List<String> bitacora() {
        try {
            JsonObject req = buildExecuteRequest("bitacora.listar");
            JsonObject resp = call(req);
            if (!isOk(resp)) return List.of();

            JsonArray arr = getValor1AsArray(resp);
            if (arr == null) return List.of();

            List<String> out = new ArrayList<>(arr.size());
            for (JsonElement el : arr) out.add(el.getAsString());
            return out;
        } catch (IOException e) {
            return List.of();
        }
    }
}
