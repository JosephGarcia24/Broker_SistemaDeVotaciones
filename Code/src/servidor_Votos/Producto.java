/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor_Votos;

/**
 *
 * @author joseph
 */
public class Producto {
    public String nombre;
    private ManejadorVotos manejadorVotos;

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
    
    public void sumarVoto(){
        this.manejadorVotos.registrarVoto(nombre);
    }  
}
