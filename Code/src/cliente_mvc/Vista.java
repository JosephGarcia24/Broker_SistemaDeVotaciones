/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author joseph
 */


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
 * Vista principal (JFrame) del cliente MVC.
 * Expone referencias públicas mínimas que el {@link Controlador} necesita para:
 * - Insertar tarjetas de productos (en el panel central)
 * - Conectar listeners a los ítems de menú
 * - Actualizar etiquetas de votos y nombres
 * Estilo:
 * - Barra de menú con gradiente oscuro
 * - Ítems minimalistas con fondo oscuro y hover morado
 *
 * Nota: Esta clase no contiene lógica de negocio; solo UI.
 * El controlador se encarga de poblarla y reaccionar a eventos.
 */
public final class Vista extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------------
    // Componentes usados por el Controlador (API de la vista)
    // ---------------------------------------------------------------------

    /** Botones "Votar" por producto (el Controlador los crea y llena). */
    public JButton[] botonesVotar;

    /** Etiquetas con el conteo de votos (una por producto). */
    public JLabel[] etiquetasVotos;

    /** Etiquetas con el nombre del producto. */
    public JLabel[] etiquetasProductos;

    // ---------------------------------------------------------------------
    // Componentes internos
    // ---------------------------------------------------------------------

    private GraficaBarras vistaGraficaBarras;  
    private final JPanel panelContenido;       // contenedor central (BorderLayout.CENTER)

    // Menú principal y submenús
    public JMenuBar menuBar;
    public JMenu menuHistorial;
    public JMenu menuGraficos;

    // Ítems (que el Controlador consulta para agregar listeners)
    public JMenuItem itemBitacora;
    public JMenuItem itemGraficaBarras;
    public JMenuItem itemGraficaPastel;

    // Menú Herramientas → ítems expuestos vía getters
    private JMenuItem menuServiciosBroker;
    private JMenuItem menuBitacoraServidor;

    //Crea la ventana principal.
    public Vista() {
        Bitacora.registrar(this.getClass(), "Inicializando vista");
        setTitle("Sistema de Votaciones");
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Panel central donde el Controlador colocará el contenido (ScrollPane con tarjetas)
        panelContenido = new JPanel();
        panelContenido.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelContenido, java.awt.BorderLayout.CENTER);

        // Barra de menú
        initMenuBar();

        // Inicializar arrays vacíos para evitar NPE si el Controlador pregunta antes de poblar
        botonesVotar = new JButton[0];
        etiquetasVotos = new JLabel[0];
        etiquetasProductos = new JLabel[0];
    }

    // ---------------------------------------------------------------------
    // Menú y estilos
    // ---------------------------------------------------------------------

    /** Construye y aplica la barra de menú con estilo. */
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

        // Menús superiores
        this.menuHistorial = crearMenuMinimalista("Historial");
        this.menuGraficos  = crearMenuMinimalista("Gráficos");

        // Ítems
        this.itemBitacora      = crearMenuItemMinimalista("Bitácora");
        this.itemGraficaBarras = crearMenuItemMinimalista("Gráfica de Barras");
        this.itemGraficaPastel = crearMenuItemMinimalista("Gráfica de Pastel");

        // Ensamblar
        this.menuHistorial.add(itemBitacora);
        this.menuGraficos.add(itemGraficaBarras);
        this.menuGraficos.add(itemGraficaPastel);

        // Menú Herramientas
        construirMenuHerramientas();

        // Agregar menús a la barra
        this.menuBar.add(menuHistorial);
        this.menuBar.add(menuGraficos);

        // Aplicar barra
        this.setJMenuBar(menuBar);
    }

    /** Crea un JMenu con estilo oscuro/hover morado. */
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

    /** Crea un JMenuItem con estilo oscuro y hover morado. */
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
     * Construye el menú "Herramientas" y añade los ítems esperados por el Controlador:
     * - Servicios del Broker...
     * - Bitácora (Servidor)...
     */
    private void construirMenuHerramientas() {
        JMenu menu = crearMenuMinimalista("Herramientas");

        // Ítems con mismo estilo que los de "Gráficos"
        menuServiciosBroker  = crearMenuItemMinimalista("Servicios del Broker...");
        menuBitacoraServidor = crearMenuItemMinimalista("Bitácora (Servidor)...");

        menu.add(menuServiciosBroker);
        menu.add(menuBitacoraServidor);

        // Inyectar en la barra principal
        this.menuBar.add(menu);
    }

    // ---------------------------------------------------------------------
    // Getters usados por el Controlador
    // ---------------------------------------------------------------------

    /** Devuelve el panel central donde el Controlador inserta el contenido.
     * @return  */
    public JPanel getPanelContenido() { return panelContenido; }

    /** Devuelve el ítem de menú "Servicios del Broker..." (para agregar listener).
     * @return  */
    public JMenuItem getMenuServiciosBroker() { return menuServiciosBroker; }

    /** Devuelve el ítem de menú "Bitácora (Servidor)..." (para agregar listener).
     * @return  */
    public JMenuItem getMenuBitacoraServidor() { return menuBitacoraServidor; }

    // ---------------------------------------------------------------------
    // Acciones auxiliares expuestas (por compatibilidad con tu código)
    // ---------------------------------------------------------------------

    /** Referencia a gráfica de barras si decides cachearla (no obligatorio).
     * @return  */
    public GraficaBarras obtenerVistaGraficaBarras() {
        Bitacora.registrar(this.getClass(), "obteniendo grafica de barras");
        return vistaGraficaBarras;
    }

    /** Muestra la gráfica de barras (crea y muestra en EDT).
     * @param productos
     * @param votos */
    public void mostrarGraficaBarras(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de barras");
        SwingUtilities.invokeLater(() -> new GraficaBarras(productos, votos));
    }

    /** Muestra la gráfica de pastel (crea y muestra en EDT).
     * @param productos
     * @param votos */
    public void mostrarGraficaPastel(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "mostrando grafica de pastel");
        SwingUtilities.invokeLater(() -> new GraficaPastel(productos, votos));
    }

    // ---------------------------------------------------------------------
    // API para que el Controlador conecte listeners a botones dinámicos
    // ---------------------------------------------------------------------

    /**
     * Agrega un listener al botón "Votar" del índice indicado.
     * El Controlador se encarga de invocar este método al construir las tarjetas.
     *
     * @param indiceProducto índice del producto
     * @param listener       acción a ejecutar cuando se presione el botón
     */
    public void agregarListenerBotonVoto(int indiceProducto, ActionListener listener) {
        Bitacora.registrar(this.getClass(), "agregando listeners");
        if (indiceProducto >= 0 && botonesVotar != null && indiceProducto < botonesVotar.length) {
            botonesVotar[indiceProducto].addActionListener(listener);
        }
    }

    // ---------------------------------------------------------------------
    // Actualizaciones de UI invocadas por el Controlador
    // ---------------------------------------------------------------------

    /** Actualiza el nombre visual de un producto.
     * @param indiceProducto
     * @param nomProducto */
    public void actualizarNomProducto(int indiceProducto, String nomProducto) {
        Bitacora.registrar(this.getClass(), "actualizando nombre del producto");
        if (etiquetasProductos != null && indiceProducto >= 0 && indiceProducto < etiquetasProductos.length) {
            etiquetasProductos[indiceProducto].setText(nomProducto);
        }
    }

    /** Actualiza el texto de votos de un producto (e.g., "5 votos").
     * @param indiceProducto
     * @param numVotos */
    public void actualizarVotos(int indiceProducto, int numVotos) {
        Bitacora.registrar(this.getClass(), "actualizando votos");
        if (etiquetasVotos != null && indiceProducto >= 0 && indiceProducto < etiquetasVotos.length) {
            etiquetasVotos[indiceProducto].setText(numVotos + " votos");
        }
    }
}
