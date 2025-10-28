/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package broker;

/**
 *
 * @author elika
 */
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class ServiceRegistry {
    static class Entry { final String servicio, host; final int port;
      Entry(String s, String h, int p){servicio=s;host=h;port=p;}
    }

    private final Map<String, List<InetSocketAddress>> map = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> rr = new ConcurrentHashMap<>();
    private final List<Entry> snapshot = Collections.synchronizedList(new ArrayList<>());

    public synchronized int registrar(String servicio, String host, int puerto) {
        map.computeIfAbsent(servicio, k -> new ArrayList<>())
           .add(new InetSocketAddress(host, puerto));
        rr.putIfAbsent(servicio, new AtomicInteger(0));
        snapshot.add(new Entry(servicio, host, puerto));
        System.out.println("[Broker] Registrado: " + servicio + " -> " + host + ":" + puerto);
        return map.get(servicio).size();
    }

    public List<Entry> listar() {
        return new ArrayList<>(snapshot);
    }

    public InetSocketAddress pick(String servicio) {
        var ends = map.getOrDefault(servicio, List.of());
        if (ends.isEmpty()) return null;
        int idx = Math.floorMod(rr.get(servicio).getAndIncrement(), ends.size());
        return ends.get(idx);
    }
}
