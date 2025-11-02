/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author joseph
 */


import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Punto de entrada del cliente Swing que consume servicios vía Broker.
 * Uso:
 *   java cliente_mvc.ClienteMain <brokerHost:brokerPort>
 *   ej: java cliente_mvc.ClienteMain localhost:5000
 *
 * Requisitos:
 * - Recibe exactamente un argumento con el formato {@code host:puerto}.
 * - Construye {@link BrokerClient}, consulta productos y levanta la UI.
 *
 * Notas:
 * - Si el Broker no responde lista de productos, se usa un conjunto de
 *   nombres por defecto (Windows, Linux, macOS) para que la UI arranque.
 * - El {@link Controlador} ya inicializa la interfaz en su constructor,
 *   por lo que NO se vuelve a llamar a ningún método de inicio aquí.
 */
public final class ClienteMain {

    private ClienteMain() {
        // Clase utilitaria: no instanciable
    }

    /**
     * Método principal.
     *
     * @param args argumentos de línea de comandos: {@code <brokerHost:brokerPort>}
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: cliente <brokerHost:brokerPort>");
            System.out.println("Ejemplo: cliente localhost:5000");
            return;
        }

        // Parseo básico de host:puerto
        final String[] hp = args[0].split(":");
        if (hp.length != 2) {
            System.err.println("Formato inválido. Se esperaba host:puerto (ej. localhost:5000)");
            return;
        }

        final String host = hp[0];
        final int port;
        try {
            port = Integer.parseInt(hp[1]);
        } catch (NumberFormatException nfe) {
            System.err.println("Puerto inválido: " + hp[1]);
            return;
        }

        // Arranque de UI en el hilo de despacho de eventos (EDT)
        SwingUtilities.invokeLater(() -> {
            BrokerClient broker = new BrokerClient(host, port);

            // Intentar obtener lista de productos desde el servidor vía Broker
            List<String> productos = broker.productos();
            if (productos == null || productos.isEmpty()) {
                // Fallback para que la interfaz sea operable
                productos = List.of("Windows", "Linux", "macOS");
            }

            // Modelo y vista
            ClienteModelo modelo = new ClienteModelo(broker, productos);
            Vista vista = new Vista();

            // El controlador ya configura e inicializa la UI en su constructor
            new Controlador(vista, modelo);
        });
    }
}
