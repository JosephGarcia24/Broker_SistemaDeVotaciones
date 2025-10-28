/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author elika
 */
import javax.swing.*;
import java.util.*;

public class ClienteMain {
    public static void main(String[] args) {
        if (args.length != 1) { System.out.println("Uso: cliente <brokerHost:brokerPort>"); return; }
        String[] hp = args[0].split(":");
        String host = hp[0]; int port = Integer.parseInt(hp[1]);

        SwingUtilities.invokeLater(() -> {
            BrokerClient bc = new BrokerClient(host, port);
            List<String> productos = bc.productos();
            if (productos.isEmpty()) productos = List.of("Windows","Linux","macOS"); // fallback

            ClienteModelo modelo = new ClienteModelo(bc, productos);
            Vista vista = new Vista(); // tu JFrame existente
            Controlador c = new Controlador(vista, modelo); // usa ClienteModelo
            c.iniciar(); // tu método de arranque de UI
        });
    }
}
