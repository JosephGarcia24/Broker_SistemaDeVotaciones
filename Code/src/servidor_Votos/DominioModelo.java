/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;



/**
 *
 * @author joseph
 */

import java.util.ArrayList;
import java.util.List;

public class DominioModelo {
    private final List<Producto> productos;
    private final ManejadorVotos manejadorVotos;

    public DominioModelo(List<String> nombresProductos) {
        this.manejadorVotos = new ManejadorVotos();
        this.productos = new ArrayList<>();

        for (String nombre : nombresProductos)
            productos.add(new Producto(nombre, manejadorVotos));
    }

    // Comprueba si un índice es válido
    public boolean existeProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Comprobando existencia del producto");
        return indiceProducto >= 0 && indiceProducto < productos.size();
    }

    // Nuevo: permite buscar producto por nombre (útil para solicitudes JSON)
    public int obtenerIndiceProducto(String nombreProducto) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getNombre().equalsIgnoreCase(nombreProducto))
                return i;
        }
        return -1;
    }

    // Registrar voto por índice
    public void registrarVoto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Registrando voto");
        if (existeProducto(indiceProducto)) {
            productos.get(indiceProducto).sumarVoto();
        }
    }

    // Obtener número de votos de un producto
    public int obtenerVotosProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Obteniendo número de votos");
        if (existeProducto(indiceProducto))
            return productos.get(indiceProducto).getNumVotos();
        return 0;
    }

    // Obtener nombre de un producto
    public String obtenerNombreProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Obteniendo nombre de producto");
        if (existeProducto(indiceProducto))
            return productos.get(indiceProducto).getNombre();
        return "";
    }

    // Obtener lista completa de nombres
    public List<String> obtenerNombresProductos() {
        Bitacora.registrar(this.getClass(), "Obteniendo lista de productos");
        List<String> nombres = new ArrayList<>();
        for (Producto p : productos)
            nombres.add(p.getNombre());
        return nombres;
    }

    // Obtener total de productos
    public int obtenerNumeroProductos() {
        Bitacora.registrar(this.getClass(), "Obteniendo número total de productos");
        return productos.size();
    }
}
