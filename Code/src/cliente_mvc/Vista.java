/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

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
 * Vista principal del cliente (UI)
 */
public class Vista extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Vista.class.getName());

    // Componentes públicos que el controlador usa
    public JButton[] botonesVotar;              // Botones para votar por cada producto
    public JLabel[] etiquetasVotos;             // Etiquetas que muestran cantidad de votos
    public JLabel[] etiquetasProductos;         // Etiquetas con nombres de productos

    // Componentes privados
    private JButton botonGrafica;
    private GraficaBarras vistaGraficaBarras;
    private JPanel panelContenido;

    // Barra de menú y elementos
    public JMenuBar menuBar;
    public JMenu menuArchivo;
    public JMenu menuGraficos;
    public JMenuItem itemBitacora;
    public JMenuItem itemGraficaBarras;
    public JMenuItem itemGraficaPastel;

    // Items específicos (herramientas)
    private JMenuItem menuServiciosBroker;
    private JMenuItem menuBitacoraServidor;

    /**
     * Constructor
     */
    public Vista() {
        Bitacora.registrar(this.getClass(), "Inicializando vista");
        setTitle("Sistema de Votaciones");
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Panel principal de contenido
        panelContenido = new JPanel();
        panelContenido.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelContenido, java.awt.BorderLayout.CENTER);

        // Botón de ejemplo para abrir gráfica (puede usarse desde controlador también)
        botonGrafica = new JButton("Mostrar gráfica");
        // lo colocamos al sur por defecto
        getContentPane().add(botonGrafica, java.awt.BorderLayout.SOUTH);

        // Inicializar la barra de menú personalizada
        initMenuBar();

        // Inicializar arrays vacíos (evita NPE si controlador pregunta antes de poblar)
        botonesVotar = new JButton[0];
        etiquetasVotos = new JLabel[0];
        etiquetasProductos = new JLabel[0];
    }

    // ===========================
    // Menú y componentes
    // ===========================
    private void initMenuBar() {
        this.menuBar = new JMenuBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(50, 50, 50),
                    0, getHeight(), new Color(35, 35, 35)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(106, 64, 166, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        menuBar.setOpaque(false);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Menús principales
        this.menuArchivo = crearMenuMinimalista("Archivo");
        this.menuGraficos = crearMenuMinimalista("Gráficos");

        // Items de los menús principales
        this.itemBitacora = crearMenuItemMinimalista("Bitácora");
        this.itemGraficaBarras = crearMenuItemMinimalista("Gráfica de Barras");
        this.itemGraficaPastel = crearMenuItemMinimalista("Gráfica de Pastel");

        // Añadir items a sus menús
        this.menuArchivo.add(itemBitacora);
        this.menuGraficos.add(itemGraficaBarras);
        this.menuGraficos.add(itemGraficaPastel);

        // Menú 'Herramientas' (servicios / bitácora remota)
        construirMenuHerramientas();

        // Añadir menús a la barra de menú
        this.menuBar.add(menuArchivo);
        this.menuBar.add(menuGraficos);

        // Finalmente fija la barra
        this.setJMenuBar(menuBar);
    }

    private JMenu crearMenuMinimalista(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setFont(new Font("Poppins", Font.BOLD, 14));
        menu.setForeground(new Color(220, 220, 220));
        menu.setBackground(new Color(45, 45, 45));
        menu.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menu.setOpaque(false);

        menu.addPropertyChangeListener("armed", e -> {
            if ((boolean) e.getNewValue()) {
                menu.setBackground(new Color(180, 100, 230));
                menu.setOpaque(true);
            } else {
                menu.setOpaque(false);
            }
        });

        return menu;
    }

    private JMenuItem crearMenuItemMinimalista(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("Poppins", Font.PLAIN, 13));
        item.setForeground(new Color(220, 220, 220));
        item.setBackground(new Color(55, 55, 55));
        item.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        item.setOpaque(true);

        item.addPropertyChangeListener("armed", e -> {
            if ((boolean) e.getNewValue()) {
                item.setBackground(new Color(180, 100, 230));
            } else {
                item.setBackground(new Color(55, 55, 55));
            }
        });

        return item;
    }

    /**
     * Construye el menú "Herramientas" con los items que el Controlador espera
     */
    private void construirMenuHerramientas() {
        JMenu menu = crearMenuMinimalista("Herramientas");

        // Inicializar aquí para que getMenu... no devuelva null
        menuServiciosBroker = new JMenuItem("Servicios del Broker...");
        menuBitacoraServidor = new JMenuItem("Bitácora (Servidor)...");

        // Estilizar brevemente (o puedes usar crearMenuItemMinimalista)
        menuServiciosBroker.setFont(new Font("Poppins", Font.PLAIN, 13));
        menuBitacoraServidor.setFont(new Font("Poppins", Font.PLAIN, 13));

        // Añadir al menú
        menu.add(menuServiciosBroker);
        menu.add(menuBitacoraServidor);

        // Añadir el menú de herramientas a la barra
        this.menuBar.add(menu);
    }

    // ===========================
    // Métodos de acceso (getters) para el controlador
    // ===========================
    public JMenuItem getMenuServiciosBroker() { return menuServiciosBroker; }
    public JMenuItem getMenuBitacoraServidor() { return menuBitacoraServidor; }

    /**
     * Obtiene el botón para mostrar la gráfica de barras
     * @return JButton del botón de gráfica
     */
    public JButton obtenerBotonGraficaBarras() {
        Bitacora.registrar(this.getClass(), "obteniendo boton de grafica de barras");
        return botonGrafica;
    }

    public void agregarListenerBotonGraficaBarras(ActionListener listener) {
        if (botonGrafica != null) botonGrafica.addActionListener(listener);
    }

    /**
     * Agregar listener a un botón de voto (el controlador debe llamar a este método)
     */
    public void agregarListenerBotonVoto(int indiceProducto, ActionListener listener) {
        Bitacora.registrar(this.getClass(), "agregando listeners");
        if (indiceProducto >= 0 && indiceProducto < botonesVotar.length) {
            botonesVotar[indiceProducto].addActionListener(listener);
        }
    }

    // Métodos para actualizar UI desde el controlador
    public void actualizarNomProducto(int indiceProducto, String nomProducto) {
        Bitacora.registrar(this.getClass(), "actualizando nombre del producto");
        if (etiquetasProductos != null && indiceProducto >= 0 && indiceProducto < etiquetasProductos.length)
            etiquetasProductos[indiceProducto].setText(nomProducto);
    }

    public void actualizarVotos(int indiceProducto, int numVotos) {
        Bitacora.registrar(this.getClass(), "actualizando votos");
        if (etiquetasVotos != null && indiceProducto >= 0 && indiceProducto < etiquetasVotos.length)
            etiquetasVotos[indiceProducto].setText(numVotos + " votos");
    }

    public GraficaBarras obtenerVistaGraficaBarras() {
        Bitacora.registrar(this.getClass(), "obteniendo grafica de barras");
        return vistaGraficaBarras;
    }

    public void mostrarGraficaBarras(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de barras");
        SwingUtilities.invokeLater(() -> new GraficaBarras(productos, votos));
    }

    public void mostrarGraficaPastel(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de pastel");
        SwingUtilities.invokeLater(() -> new GraficaPastel(productos, votos));
    }
}
