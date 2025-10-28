/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author joseph
 */
public class Vista {
    // En la construcción del menú:
    private JMenuItem menuServiciosBroker;
    private JMenuItem menuBitacoraServidor;

    private void construirMenu() {
      JMenu menu = new JMenu("Herramientas");
      menuServiciosBroker = new JMenuItem("Servicios del Broker...");
      menuBitacoraServidor = new JMenuItem("Bitácora (Servidor)...");
      menu.add(menuServiciosBroker);
      menu.add(menuBitacoraServidor);
      // añade 'menu' a la barra de menús
    }

    public JMenuItem getMenuServiciosBroker() { return menuServiciosBroker; }
    public JMenuItem getMenuBitacoraServidor() { return menuBitacoraServidor; }

    
}
