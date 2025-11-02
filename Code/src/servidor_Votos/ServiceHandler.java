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

/**
 * Despachador de servicios del dominio de votaciones.
 * Soporta:
 *   productos → devuelve arreglo de nombres
 *   contar → devuelve objeto {producto: votos}
 *   votar → incrementa votos; acepta el nombre en {@code valor2} o {@code valor1}
 *   bitacora.listar → devuelve líneas de bitácora
 * 
 * Respuesta común:
 *   ok=true  : respuesta1=&lt;clave&gt;, valor1=&lt;valor&gt;
 *   ok=false : respuesta1="error", valor1=&lt;mensaje&gt;
 */
final class ServiceHandler {

    private ServiceHandler() {
        // Utilitaria: no instanciable
    }

    /** Construye una respuesta exitosa (ok=true) tipo clave-valor. */
    private static JsonObject okKV(String responseKey, JsonElement value) {
        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        o.addProperty("respuesta1", responseKey);
        o.add("valor1", value);
        return o;
    }

    /** Construye una respuesta de error (ok=false). */
    private static JsonObject error(String message) {
        JsonObject o = new JsonObject();
        o.addProperty("ok", false);
        o.addProperty("respuesta1", "error");
        o.addProperty("valor1", message);
        return o;
    }

    /**
     * Procesa la solicitud JSON contra el modelo de dominio.
     *
     * @param req    solicitud entrante (debe contener "servicio")
     * @param modelo fachada del dominio de votaciones
     * @return respuesta JSON del servicio
     */
    static JsonObject procesar(JsonObject req, DominioModelo modelo) {
        if (req == null || !req.has("servicio")) {
            return error("Solicitud inválida: falta 'servicio'");
        }

        String servicio = req.get("servicio").getAsString();
        switch (servicio) {
            case "productos" -> {
                JsonArray arr = new JsonArray();
                for (String n : modelo.obtenerNombresProductos()) {
                    arr.add(new JsonPrimitive(n));
                }
                return okKV("productos", arr);
            }

            case "contar" -> {
                JsonObject counts = new JsonObject();
                for (String nombre : modelo.obtenerNombresProductos()) {
                    int idx = modelo.obtenerIndiceProducto(nombre);
                    counts.addProperty(nombre, modelo.obtenerVotosProducto(idx));
                }
                return okKV("conteos", counts);
            }

            case "votar" -> {
                // Acepta el nombre del producto en valor2 (preferido) o valor1 (retrocompatibilidad)
                String producto =
                        req.has("valor2") ? req.get("valor2").getAsString()
                        : (req.has("valor1") ? req.get("valor1").getAsString() : null);

                if (producto == null || producto.isBlank()) {
                    return error("Falta parámetro producto");
                }

                int idx = modelo.obtenerIndiceProducto(producto);
                if (idx < 0) {
                    return error("Producto no encontrado");
                }

                modelo.registrarVoto(idx);
                int votos = modelo.obtenerVotosProducto(idx);

                Bitacora.registrar(ServiceHandler.class, "Voto a " + producto + " -> " + votos);
                return okKV("votos", new JsonPrimitive(votos));
            }

            case "bitacora.listar" -> {
                JsonArray arr = new JsonArray();
                for (String l : Bitacora.leer()) {
                    arr.add(new JsonPrimitive(l));
                }
                return okKV("bitacora", arr);
            }

            default -> {
                return error("Servicio no soportado: " + servicio);
            }
        }
    }
}
