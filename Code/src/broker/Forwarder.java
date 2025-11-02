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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Utilidad para reenviar una solicitud JSON a un servidor de servicio
 * y devolver la respuesta JSON resultante.
 */
public final class Forwarder {

    private Forwarder() {}

    /**
     * Envía una petición JSON (una línea) a la dirección dada y
     * devuelve la respuesta parseada como JsonObject.
     *
     * @param address dirección del servidor destino.
     * @param request objeto JSON con el campo "servicio" y sus parámetros.
     * @return respuesta JSON del servidor destino.
     * @throws IOException si hay un error de red o de E/S.
     */
    public static JsonObject forward(InetSocketAddress address, JsonObject request) throws IOException {
        try (Socket socket = new Socket(address.getHostString(), address.getPort());
             var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             var in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.println(request.toString());
            String line = in.readLine();
            if (line == null || line.isBlank()) {
                throw new IOException("Respuesta vacía del servidor destino");
            }
            return JsonParser.parseString(line).getAsJsonObject();
        }
    }
}

