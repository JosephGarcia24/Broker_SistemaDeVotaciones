/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author joseph
 */


import java.util.*;

/**
 * Modelo del cliente en el patrón MVC.
 * Interactúa con el {@link BrokerClient} para obtener información o enviar votos
 * y mantiene una lista local inmutable de nombres de productos.
 *
 * Métodos principales:
 * - {@link #registrarVoto(int)} para enviar un voto al servidor.
 * - {@link #obtenerVotosProducto(int)} para consultar los votos de un producto.
 * - {@link #serviciosBroker()} y {@link #bitacora()} para acceder a datos del Broker.
 *
 */
public final class ClienteModelo {

    /** Cliente de comunicación con el Broker. */
    private final BrokerClient broker;

    /** Lista de nombres de productos disponibles para votar. */
    private final List<String> productos;

    /**
     * Crea un nuevo modelo cliente.
     *
     * @param broker cliente del broker (ya conectado o preparado)
     * @param productos lista de nombres de productos disponibles
     */
    public ClienteModelo(BrokerClient broker, List<String> productos) {
        this.broker = Objects.requireNonNull(broker, "broker no puede ser null");
        this.productos = new ArrayList<>(Objects.requireNonNull(productos, "productos no puede ser null"));
    }

    /**
     * Devuelve una lista inmutable con los nombres de los productos disponibles.
     *
     * @return lista de nombres de productos
     */
    public List<String> obtenerNombres() {
        return Collections.unmodifiableList(productos);
    }

    /**
     * Registra un voto para el producto con el índice indicado.
     * Si el índice es inválido, no realiza ninguna acción.
     *
     * @param idx índice del producto a votar
     */
    public void registrarVoto(int idx) {
        if (idx < 0 || idx >= productos.size()) return;
        broker.votar(productos.get(idx));
    }

    /**
     * Devuelve el número actual de votos del producto indicado.
     *
     * @param idx índice del producto
     * @return número de votos actuales (0 si no se encuentra)
     */
    public int obtenerVotosProducto(int idx) {
        if (idx < 0 || idx >= productos.size()) return 0;
        return broker.contar().getOrDefault(productos.get(idx), 0);
    }

    /**
     * Devuelve un mapa con todos los productos y sus votos actuales.
     *
     * @return mapa nombreProducto -> votos
     */
    public Map<String, Integer> obtenerTodos() {
        return broker.contar();
    }

    /**
     * Consulta los servicios registrados actualmente en el Broker.
     *
     * @return lista de pares {servicio, dirección}
     */
    public List<String[]> serviciosBroker() {
        return broker.listarServicios();
    }

    /**
     * Obtiene la bitácora del servidor remoto a través del Broker.
     *
     * @return lista de líneas de bitácora
     */
    public List<String> bitacora() {
        return broker.bitacora();
    }
}
