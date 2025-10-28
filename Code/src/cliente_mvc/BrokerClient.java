/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author joseph
 */
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BrokerClient {
  private final String host; private final int port;
  public BrokerClient(String host, int port){ this.host=host; this.port=port; }

  private JsonObject call(JsonObject req) throws IOException {
    try (Socket s = new Socket()) {
      s.connect(new InetSocketAddress(host, port), 4000);
      s.setSoTimeout(4000);
      try (var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
           var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {
        out.println(req.toString());
        String line = in.readLine(); if (line == null) throw new IOException("Broker cerró");
        return JsonParser.parseString(line).getAsJsonObject();
      }
    }
  }

  public boolean votar(String producto){
    try {
      JsonObject req = new JsonObject();
      req.addProperty("servicio","ejecutar");
      req.addProperty("param1","destino"); req.addProperty("valor1","votar");
      req.addProperty("param2","producto"); req.addProperty("valor2",producto);
      JsonObject r = call(req);
      return r.has("ok") && r.get("ok").getAsBoolean();
    } catch (Exception e) { return false; }
  }

  public Map<String,Integer> contar(){
    try {
      JsonObject req = new JsonObject();
      req.addProperty("servicio","ejecutar");
      req.addProperty("param1","destino"); req.addProperty("valor1","contar");
      JsonObject resp = call(req);
      if (!resp.get("ok").getAsBoolean()) return Map.of();
      JsonObject counts = resp.get("valor1").getAsJsonObject();
      Map<String,Integer> out = new LinkedHashMap<>();
      for (var e: counts.entrySet()) out.put(e.getKey(), e.getValue().getAsInt());
      return out;
    } catch (Exception e) { return Map.of(); }
  }

  public List<String> productos(){
    try {
      JsonObject req = new JsonObject();
      req.addProperty("servicio","ejecutar");
      req.addProperty("param1","destino"); req.addProperty("valor1","productos");
      JsonObject resp = call(req);
      if (!resp.get("ok").getAsBoolean()) return List.of();
      List<String> out = new ArrayList<>();
      for (var el: resp.get("valor1").getAsJsonArray()) out.add(el.getAsString());
      return out;
    } catch (Exception e) { return List.of(); }
  }

  public List<String[]> listarServicios(){
    try {
      JsonObject req = new JsonObject();
      req.addProperty("servicio","listar"); // directo al broker
      JsonObject resp = call(req);
      if (!resp.get("ok").getAsBoolean()) return List.of();
      List<String[]> out = new ArrayList<>();
      for (var el: resp.get("valor1").getAsJsonArray()) {
        var o = el.getAsJsonObject();
        out.add(new String[]{ o.get("servicio").getAsString(), o.get("direccion").getAsString() });
      }
      return out;
    } catch (Exception e) { return List.of(); }
  }

  public List<String> bitacora() {
    try {
      JsonObject req = new JsonObject();
      req.addProperty("servicio","ejecutar");
      req.addProperty("param1","destino"); req.addProperty("valor1","bitacora.listar");
      JsonObject resp = call(req);
      if (!resp.get("ok").getAsBoolean()) return List.of();
      List<String> out = new ArrayList<>();
      for (var el: resp.get("valor1").getAsJsonArray()) out.add(el.getAsString());
      return out;
    } catch (Exception e) { return List.of(); }
  }
}

