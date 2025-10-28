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
import java.nio.charset.StandardCharsets;

class Forwarder {
    static JsonObject forward(InetSocketAddress addr, JsonObject req) throws IOException {
        try (Socket s = new Socket()) {
            s.connect(addr, 4000); s.setSoTimeout(4000);
            try (var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                 var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {
                out.println(req.toString());
                String line = in.readLine(); if (line == null) throw new IOException("Servidor cerró");
                return JsonParser.parseString(line).getAsJsonObject();
            }
        }
    }
}

