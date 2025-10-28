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

class ServiceHandler {
    static JsonObject okKV(String k, JsonElement v){var o=new JsonObject();o.addProperty("ok",true);o.addProperty("respuesta1",k);o.add("valor1",v);return o;}
    static JsonObject error(String m){var o=new JsonObject();o.addProperty("ok",false);o.addProperty("respuesta1","error");o.addProperty("valor1",m);return o;}

    static JsonObject procesar(JsonObject req, DominioModelo modelo) {
        String servicio = req.get("servicio").getAsString();
        switch (servicio) {
            case "productos": {
                JsonArray arr = new JsonArray();
                for (String n : modelo.obtenerNombresProductos()) arr.add(new JsonPrimitive(n));
                return okKV("productos", arr);
            }
            case "contar": {
                JsonObject counts = new JsonObject();
                for (String nombre : modelo.obtenerNombresProductos()) {
                    int idx = modelo.obtenerIndiceProducto(nombre);
                    counts.addProperty(nombre, modelo.obtenerVotosProducto(idx));
                }
                return okKV("conteos", counts);
            }
            case "votar": {
                String producto = req.has("valor2") ? req.get("valor2").getAsString()
                                  : (req.has("valor1") ? req.get("valor1").getAsString() : null);
                if (producto == null) return error("Falta parámetro producto");
                int idx = modelo.obtenerIndiceProducto(producto);
                if (idx < 0) return error("Producto no encontrado");
                modelo.registrarVoto(idx);
                int votos = modelo.obtenerVotosProducto(idx);
                Bitacora.registrar(ServiceHandler.class, "Voto a " + producto + " -> " + votos);
                return okKV("votos", new JsonPrimitive(votos));
            }
            case "bitacora.listar": {
                var arr = new JsonArray();
                for (String l : Bitacora.leer()) arr.add(new JsonPrimitive(l));
                return okKV("bitacora", arr);
            }
            default: return error("Servicio no soportado: " + servicio);
        }
    }
}
