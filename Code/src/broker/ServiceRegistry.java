/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package broker;


/**
 *
 * @author elika
 */

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Registro concurrente de servicios y sus endpoints.
 * Seguro para acceso desde múltiples hilos.
 */
public final class ServiceRegistry {

    /** Entrada de servicio registrada en el broker. */
    public static final class Entry {
        public final int id;
        public final String servicio;
        public final String host;
        public final int port;

        Entry(int id, String servicio, String host, int port) {
            this.id = id;
            this.servicio = servicio;
            this.host = host;
            this.port = port;
        }
    }

    // serviceName -> lista de endpoints
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Entry>> services = new ConcurrentHashMap<>();
    // serviceName -> índice round-robin
    private final ConcurrentHashMap<String, AtomicInteger> rrIndex = new ConcurrentHashMap<>();
    // generador de IDs
    private final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Registra un endpoint para un servicio.
     * @param serviceName nombre lógico del servicio.
     * @param host hostname o IP del proveedor.
     * @param port puerto del proveedor.
     * @return identificador interno asignado a esa entrada.
     */
    public int registrar(String serviceName, String host, int port) {
        Objects.requireNonNull(serviceName, "serviceName");
        Objects.requireNonNull(host, "host");

        int newId = idCounter.getAndIncrement();
        Entry entry = new Entry(newId, serviceName, host, port);
        services.computeIfAbsent(serviceName, k -> new CopyOnWriteArrayList<>()).add(entry);
        rrIndex.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
        return newId;
    }

    /**
     * Devuelve todas las entradas registradas en un snapshot inmutable.
     * @return lista inmutable de entradas.
     */
    public List<Entry> listar() {
        ArrayList<Entry> all = new ArrayList<>();
        for (CopyOnWriteArrayList<Entry> lst : services.values()) {
            all.addAll(lst);
        }
        return Collections.unmodifiableList(all);
    }

    /**
     * Selecciona un endpoint para el servicio dado usando round-robin.
     * @param serviceName nombre lógico del servicio.
     * @return dirección InetSocketAddress seleccionada o null si no hay endpoints.
     */
    public InetSocketAddress pick(String serviceName) {
        CopyOnWriteArrayList<Entry> lst = services.get(serviceName);
        if (lst == null || lst.isEmpty()) return null;
        AtomicInteger idx = rrIndex.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
        Entry e = lst.get(Math.floorMod(idx.getAndIncrement(), lst.size()));
        return new InetSocketAddress(e.host, e.port);
    }
}
