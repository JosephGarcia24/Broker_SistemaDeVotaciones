/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author joseph
 */

/**
 * Entidad de dominio que representa un producto votable.
 * No almacena el conteo en memoria; delega en {@link ManejadorVotos} para leer/escribir.
 */
public final class Producto {

    private String nombre;
    private final ManejadorVotos manejadorVotos;

    /**
     * Crea un producto con nombre y manejador de votos.
     *
     * @param nombre nombre del producto
     * @param manejadorVotos componente de persistencia de votos
     */
    public Producto(String nombre, ManejadorVotos manejadorVotos) {
        this.nombre = nombre;
        this.manejadorVotos = manejadorVotos;
    }


    public String getNombre() {
        return nombre;
    }

   
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

   
    public int getNumVotos() {
        return manejadorVotos.obtenerVotos(nombre);
    }

   
    public void sumarVoto() {
        manejadorVotos.registrarVoto(nombre);
    }
}
