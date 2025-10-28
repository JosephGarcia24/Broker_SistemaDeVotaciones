/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author elika
 */
import java.util.*;

public class ClienteModelo {
    private final BrokerClient broker;
    private final List<String> productos;

    public ClienteModelo(BrokerClient broker, List<String> productos){
        this.broker = broker;
        this.productos = new ArrayList<>(productos);
    }

    public List<String> obtenerNombres(){ return Collections.unmodifiableList(productos); }

    public void registrarVoto(int idx){
        if (idx < 0 || idx >= productos.size()) return;
        broker.votar(productos.get(idx));
    }

    public int obtenerVotosProducto(int idx){
        if (idx < 0 || idx >= productos.size()) return 0;
        return broker.contar().getOrDefault(productos.get(idx), 0);
    }

    public Map<String,Integer> obtenerTodos(){ return broker.contar(); }

    public List<String[]> serviciosBroker(){ return broker.listarServicios(); }

    public List<String> bitacora(){ return broker.bitacora(); }
}
