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

/**
 * Fachada de dominio para el sistema de votaciones.
 * Mantiene la lista de productos y delega la persistencia de votos a {@link ManejadorVotos}.
 *
 * Flujo típico:
 *  - Construcción con lista de nombres → se crean objetos {@link Producto}
 *  - registrarVoto(idx) → incrementa voto del producto en índice
 *  - obtenerVotosProducto(idx) → lee votos desde almacenamiento
 *  - obtenerNombresProductos() → devuelve nombres en orden
 */
public final class DominioModelo {

    /** Lista ordenada de productos disponibles para votar. */
    private final List<Producto> productos;

    /** Componente de persistencia de votos (archivo por producto). */
    private final ManejadorVotos manejadorVotos;

    /**
     * Crea el dominio con una lista de nombres de productos.
     * Cada nombre genera un {@link Producto} asociado al mismo {@link ManejadorVotos}.
     *
     * @param nombresProductos nombres a registrar
     */
    public DominioModelo(List<String> nombresProductos) {
        this.manejadorVotos = new ManejadorVotos();
        this.productos = new ArrayList<>();
        for (String nombre : nombresProductos) {
            productos.add(new Producto(nombre, manejadorVotos));
        }
    }

    /**
     * Verifica si el índice corresponde a un producto existente.
     *
     * @param indiceProducto índice a validar
     * @return true si existe; false en caso contrario
     */
    public boolean existeProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Comprobando existencia del producto");
        return indiceProducto >= 0 && indiceProducto < productos.size();
    }

    /**
     * Obtiene el índice del producto dado su nombre (búsqueda case-insensitive).
     *
     * @param nombreProducto nombre a buscar
     * @return índice o -1 si no se encuentra
     */
    public int obtenerIndiceProducto(String nombreProducto) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getNombre().equalsIgnoreCase(nombreProducto)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Registra un voto para el producto en el índice dado.
     * Si el índice no es válido, no realiza ninguna acción.
     *
     * @param indiceProducto índice del producto
     */
    public void registrarVoto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Registrando voto");
        if (existeProducto(indiceProducto)) {
            productos.get(indiceProducto).sumarVoto();
        }
    }

    /**
     * Obtiene el número de votos del producto en el índice dado.
     *
     * @param indiceProducto índice del producto
     * @return votos actuales o 0 si el índice es inválido
     */
    public int obtenerVotosProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Obteniendo número de votos");
        if (existeProducto(indiceProducto)) {
            return productos.get(indiceProducto).getNumVotos();
        }
        return 0;
    }

    /**
     * Devuelve el nombre del producto en el índice dado.
     *
     * @param indiceProducto índice del producto
     * @return nombre o cadena vacía si el índice es inválido
     */
    public String obtenerNombreProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Obteniendo nombre de producto");
        if (existeProducto(indiceProducto)) {
            return productos.get(indiceProducto).getNombre();
        }
        return "";
    }

    /**
     * Devuelve la lista de nombres de todos los productos, en orden.
     * Se retorna una nueva lista para evitar modificaciones externas.
     *
     * @return lista de nombres
     */
    public List<String> obtenerNombresProductos() {
        Bitacora.registrar(this.getClass(), "Obteniendo lista de productos");
        List<String> nombres = new ArrayList<>(productos.size());
        for (Producto p : productos) {
            nombres.add(p.getNombre());
        }
        return nombres;
    }

    /**
     * Devuelve el número total de productos administrados por el dominio.
     *
     * @return cantidad de productos
     */
    public int obtenerNumeroProductos() {
        Bitacora.registrar(this.getClass(), "Obteniendo número total de productos");
        return productos.size();
    }
}
