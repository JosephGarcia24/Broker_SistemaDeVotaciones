/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author joseph
 */

import cliente_mvc.GraficaBarras;
import cliente_mvc.GraficaPastel;
import cliente_mvc.VistaBitacora;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import servidor_Votos.Bitacora;

/**
 *
 * @author joseph
 */
public class Vista extends javax.swing.JFrame {

    // ============================================
    // LOGGER Y ATRIBUTOS PÚBLICOS
    // ============================================
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Vista.class.getName());
    
    // Arreglos públicos para almacenar referencias a componentes de UI
    public JButton[] botonesVotar;              // Botones para votar por cada producto
    public JLabel[] etiquetasVotos;             // Etiquetas que muestran cantidad de votos
    public JLabel[] etiquetasProductos;         // Etiquetas con nombres de productos
    
    // ============================================
    // ATRIBUTOS PRIVADOS
    // ============================================
    
    private JButton botonGrafica;
    private GraficaBarras vistaGraficaBarras;
    private JPanel panelContenido;
    
    // ============================================
    // COMPONENTES DEL MENÚ
    // ============================================
    
    public JMenuBar menuBar;                    // Barra de menú principal
    public JMenu menuArchivo;                   // Menú "Archivo"
    public JMenu menuGraficos;                  // Menú "Gráficos"
    public JMenuItem itemBitacora;              // Opción para abrir Bitácora
    public JMenuItem itemGraficaBarras;         // Opción para mostrar gráfica de barras
    public JMenuItem itemGraficaPastel;         // Opción para mostrar gráfica de pastel

    // ============================================
    // CONSTRUCTOR
    // ============================================
    
    /**
     * Crea la ventana principal del sistema de votaciones
     * Inicializa la barra de menú con estilos personalizados
     */
    public Vista() {
        Bitacora.registrar(this.getClass(), "Inicializando vista");
        setTitle("Sistema de Votaciones");
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        // ============================================
        // CREACIÓN DE MENUBAR PERSONALIZADO
        // ============================================
        
        this.menuBar = new JMenuBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Aplicar gradiente oscuro como fondo de la barra de menú
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(50, 50, 50),
                    0, getHeight(), new Color(35, 35, 35)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Dibujar línea decorativa púrpura en la parte inferior
                g2d.setColor(new Color(106, 64, 166, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        menuBar.setOpaque(false);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Crear menús principales
        this.menuArchivo = crearMenuMinimalista("Archivo");
        this.menuGraficos = crearMenuMinimalista("Gráficos");

        // ============================================
        // CREACIÓN DE ITEMS DE MENÚ
        // ============================================
        
        this.itemBitacora = crearMenuItemMinimalista("Bitácora");
        this.itemGraficaBarras = crearMenuItemMinimalista("Gráfica de Barras");
        this.itemGraficaPastel = crearMenuItemMinimalista("Gráfica de Pastel");

        // Agregar items a sus menús respectivos
        this.menuArchivo.add(itemBitacora);
        this.menuGraficos.add(itemGraficaBarras);
        this.menuGraficos.add(itemGraficaPastel);

        // Agregar menús a la barra de menú
        this.menuBar.add(menuArchivo);
        this.menuBar.add(menuGraficos);

        this.setJMenuBar(menuBar);
    }

    // ============================================
    // CREACIÓN DE COMPONENTES DE MENÚ
    // ============================================
    
    /**
     * Crea un menú con estilo minimalista personalizado
     * Incluye efecto de cambio de color al pasar el mouse (hover)
     * @param texto nombre del menú a mostrar
     * @return JMenu configurado con estilos personalizados
     */
    private JMenu crearMenuMinimalista(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setFont(new Font("Poppins", Font.BOLD, 14));
        menu.setForeground(new Color(220, 220, 220));
        menu.setBackground(new Color(45, 45, 45));
        menu.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menu.setOpaque(false);
        
        // Agregar listener para cambio de color al activarse (armed state)
        menu.addPropertyChangeListener("armed", e -> {
            if ((boolean) e.getNewValue()) {
                // Cuando se activa: mostrar fondo morado vibrante
                menu.setBackground(new Color(180, 100, 230));
                menu.setOpaque(true);
            } else {
                // Cuando se desactiva: volver a fondo transparente
                menu.setOpaque(false);
            }
        });

        return menu;
    }

    /**
     * Crea un item de menú con estilo minimalista personalizado
     * Incluye efecto de cambio de color al pasar el mouse (hover)
     * @param texto texto del item a mostrar
     * @return JMenuItem configurado con estilos personalizados
     */
    private JMenuItem crearMenuItemMinimalista(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("Poppins", Font.PLAIN, 13));
        item.setForeground(new Color(220, 220, 220));
        item.setBackground(new Color(55, 55, 55));
        item.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        item.setOpaque(true);

        // Agregar listener para cambio de color al activarse (armed state)
        item.addPropertyChangeListener("armed", e -> {
            if ((boolean) e.getNewValue()) {
                // Cuando se activa: mostrar fondo morado puro
                item.setBackground(new Color(180, 100, 230));
            } else {
                // Cuando se desactiva: volver a fondo gris oscuro
                item.setBackground(new Color(55, 55, 55));
            }
        });

        return item;
    }

    // ============================================
    // MÉTODOS DE GESTIÓN DE PANEL
    // ============================================
    
    /**
     * Reemplaza el contenido del panel principal con un nuevo panel
     * @param panel el nuevo panel a mostrar
     */
    public void setPanelPrincipal(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ============================================
    // MÉTODOS DE ACCESO A COMPONENTES
    // ============================================
    
    /**
     * Obtiene el botón de votación para un producto específico
     * @param indiceProducto índice del producto
     * @return JButton del producto, o null si el índice es inválido
     */
    public JButton obtenerBotonProducto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "Obteniendo botón del producto");
        if (indiceProducto >= 0 && indiceProducto < botonesVotar.length)
            return botonesVotar[indiceProducto];

        return null;
    }

    /**
     * Actualiza el nombre mostrado de un producto
     * @param indiceProducto índice del producto a actualizar
     * @param nomProducto nuevo nombre del producto
     */
    public void actualizarNomProducto(int indiceProducto, String nomProducto) {
        Bitacora.registrar(this.getClass(), "actualizando nombre del producto");
        if (indiceProducto >= 0 && indiceProducto < etiquetasProductos.length)
            etiquetasProductos[indiceProducto].setText(nomProducto);
    }

    /**
     * Actualiza la cantidad de votos mostrada para un producto
     * @param indiceProducto índice del producto
     * @param numVotos nueva cantidad de votos
     */
    public void actualizarVotos(int indiceProducto, int numVotos) {
        Bitacora.registrar(this.getClass(), "actualizando votos");
        if (indiceProducto >= 0 && indiceProducto < etiquetasVotos.length)
            etiquetasVotos[indiceProducto].setText(numVotos + " votos");
    }

    // ============================================
    // MÉTODOS DE LISTENERS
    // ============================================
    
    /**
     * Agrega un listener (manejador de eventos) a un botón de votación
     * @param indiceProducto índice del producto
     * @param listener el ActionListener a agregar
     */
    public void agregarListenerBotonVoto(int indiceProducto, ActionListener listener) {
        Bitacora.registrar(this.getClass(), "agregando listeners");
        if (indiceProducto >= 0 && indiceProducto < botonesVotar.length) {
            botonesVotar[indiceProducto].addActionListener(listener);
        }
    }

    /**
     * Obtiene el botón para mostrar la gráfica de barras
     * @return JButton del botón de gráfica
     */
    public JButton obtenerBotonGraficaBarras() {
        Bitacora.registrar(this.getClass(), "obteniendo boton de grafica de barras");
        return botonGrafica;
    }

    /**
     * Agrega un listener al botón de gráfica de barras
     * @param listener el ActionListener a agregar
     */
    public void agregarListenerBotonGraficaBarras(ActionListener listener) {
        botonGrafica.addActionListener(listener);
    }

    // ============================================
    // MÉTODOS PARA MOSTRAR GRÁFICAS
    // ============================================
    
    /**
     * Muestra una ventana con la gráfica de barras
     * @param productos arreglo de nombres de productos
     * @param votos arreglo de cantidades de votos
     */
    public void mostrarGraficaBarras(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de barras");
        SwingUtilities.invokeLater(() -> new GraficaBarras(productos, votos));
    }

    /**
     * Muestra una ventana con la gráfica de pastel
     * @param productos arreglo de nombres de productos
     * @param votos arreglo de cantidades de votos
     */
    public void mostrarGraficaPastel(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de pastel");
        SwingUtilities.invokeLater(() -> new GraficaPastel(productos, votos));
    }

    // ============================================
    // MÉTODO PARA MOSTRAR BITÁCORA
    // ============================================
    
    /**
     * Muestra la ventana de Bitácora en el hilo de eventos de Swing
     */


    // ============================================
    // MÉTODOS DE ACCESO A GRÁFICAS
    // ============================================
    
    /**
     * Obtiene la referencia a la vista de gráfica de barras activa
     * @return GraficaBarras o null si no está disponible
     */
    public GraficaBarras obtenerVistaGraficaBarras() {
        Bitacora.registrar(this.getClass(), "obteniendo grafica de barras");
        return vistaGraficaBarras;
    }
    
    
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
