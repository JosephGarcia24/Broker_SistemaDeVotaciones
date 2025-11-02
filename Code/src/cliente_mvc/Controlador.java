/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cliente_mvc;

/**
 *
 * @author joseph
 */

/*
 * Controlador de la UI (cliente)
 * - Conecta Vista y Modelo
 * - Pinta la pantalla principal y maneja eventos
 * - Mantiene estilos de gradiente y tarjetas
 */


import servidor_Votos.Bitacora;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Controlador principal del sistema de votaciones.
 * Gestiona la interacción entre la vista y el modelo.
 */
public final class Controlador {

    // =========================================================
    // PALETA / TIPOGRAFÍA
    // =========================================================
    private static final Color COLOR_GRADIENT_TOP    = new Color(136, 84, 208);
    private static final Color COLOR_GRADIENT_BOTTOM = new Color(74, 144, 226);
    private static final Color COLOR_TITULO          = Color.WHITE;
    private static final Color COLOR_SUBTITULO       = new Color(255, 255, 255, 200);
    private static final Color COLOR_TEXTO_PRODUCTO  = new Color(28, 28, 28);
    private static final Color COLOR_VOTOS           = new Color(120, 120, 120);

    private static final int TITULO_SIZE            = 36;
    private static final int SUBTITULO_SIZE         = 16;
    private static final int PRODUCTO_TITULO_SIZE   = 22;
    private static final int PRODUCTO_VOTOS_SIZE    = 16;
    private static final int BOTON_FUENTE_SIZE      = 15;

    // =========================================================
    // REFERENCIAS / ESTADO DE UI
    // =========================================================
    private final Vista vista;
    private final ClienteModelo modelo;

    private JPanel panelPrincipal;                 // raíz scrollable con gradiente
    private GraficaBarras graficaBarrasActiva;     // instancias visibles de gráficas
    private GraficaPastel graficaPastelActiva;
    private VistaBitacora dlgBitacora;             // diálogo reutilizable de bitácora

    // =========================================================
    // CONSTRUCCIÓN
    // =========================================================
    public Controlador(Vista vista, ClienteModelo modelo) {
        Bitacora.registrar(this.getClass(), "inicializando controlador");
        this.vista = vista;
        this.modelo = modelo;
        iniciar();
        registrarListeners();
    }

    // =========================================================
    // INICIALIZACIÓN DE LA UI
    // =========================================================
    public void iniciar() {
        Bitacora.registrar(this.getClass(), "inicializando vista");

        // Reservar arreglos en vista (evita NPE si se consulta antes de poblar)
        final int cantidadProductos = modelo.obtenerNombres().size();
        vista.botonesVotar        = new JButton[cantidadProductos];
        vista.etiquetasVotos      = new JLabel[cantidadProductos];
        vista.etiquetasProductos  = new JLabel[cantidadProductos];

        // Panel principal con gradiente y layout vertical centrado
        panelPrincipal = crearPanelPrincipal();

        // Encabezado
        panelPrincipal.add(crearTitulo("Sistema de Votación", TITULO_SIZE, Font.BOLD));
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeparador(100, 4));
        panelPrincipal.add(Box.createVerticalStrut(15));

        final JLabel subtitulo = new JLabel("Vota por tu producto favorito");
        subtitulo.setFont(obtenerFuente("Poppins", Font.PLAIN, SUBTITULO_SIZE));
        subtitulo.setForeground(COLOR_SUBTITULO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(subtitulo);
        panelPrincipal.add(Box.createVerticalStrut(40));

        // Tarjetas de productos
        for (int i = 0; i < cantidadProductos; i++) {
            panelPrincipal.add(crearTarjetaProducto(i));
            panelPrincipal.add(Box.createVerticalStrut(20));
        }

        // Scroll conteniendo el panel principal (se inserta en panelContenido de Vista)
        final JScrollPane scrollPane = crearScrollPara(panelPrincipal);
        insertarEnPanelContenido(scrollPane);

        // Ventana
        vista.setMinimumSize(new Dimension(500, 600));
        vista.setPreferredSize(new Dimension(900, 800));
        vista.setLocationRelativeTo(null);
        vista.setResizable(true);
        vista.setVisible(true);

        // Responsividad (ajusta padding al redimensionar)
        vista.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { actualizarEspaciosResponsive(); }
        });
        actualizarEspaciosResponsive();
    }

    // =========================================================
    // CONSTRUCCIÓN DE COMPONENTES
    // =========================================================

    /** Panel raíz con gradiente vertical y BoxLayout Y centrado. */
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2d = (Graphics2D) g.create();
                final GradientPaint gp = new GradientPaint(
                        0, 0, COLOR_GRADIENT_TOP,
                        0, getHeight(), COLOR_GRADIENT_BOTTOM
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    /** Título centrado con color y tipografía de cabecera. */
    private JLabel crearTitulo(String texto, int tamaño, int estilo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(obtenerFuente("Poppins", estilo, tamaño));
        lbl.setForeground(COLOR_TITULO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /** Separador decorativo redondeado. */
    private JComponent crearSeparador(int ancho, int alto) {
        JPanel linea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
            }
        };
        linea.setOpaque(false);
        linea.setPreferredSize(new Dimension(ancho, alto));
        linea.setMaximumSize(new Dimension(ancho, alto));
        linea.setAlignmentX(Component.CENTER_ALIGNMENT);
        return linea;
    }

    /** Tarjeta visual de producto con nombre, votos y botón de votar. */
    private JPanel crearTarjetaProducto(int index) {
        final String nombre   = toTitleCase(modelo.obtenerNombres().get(index));
        final int    numVotos = modelo.obtenerVotosProducto(index);

        JPanel panelTarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // sombra
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 8, getWidth() - 8, getHeight() - 10, 20, 20);
                // tarjeta
                g2.setColor(new Color(255, 255, 255, 250));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 10, 20, 20);
                g2.dispose();
            }
        };
        panelTarjeta.setLayout(new BoxLayout(panelTarjeta, BoxLayout.X_AXIS));
        panelTarjeta.setOpaque(false);
        panelTarjeta.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelTarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTarjeta.setMaximumSize(new Dimension(700, 80)); // ancho máximo “bonito”

        // Nombre
        JLabel lblProducto = new JLabel(nombre);
        lblProducto.setFont(obtenerFuente("Poppins", Font.BOLD, PRODUCTO_TITULO_SIZE));
        lblProducto.setForeground(COLOR_TEXTO_PRODUCTO);
        lblProducto.setVerticalAlignment(SwingConstants.CENTER);
        lblProducto.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Votos
        JLabel lblVotos = new JLabel(numVotos + " votos");
        lblVotos.setFont(obtenerFuente("Poppins", Font.PLAIN, PRODUCTO_VOTOS_SIZE));
        lblVotos.setForeground(COLOR_VOTOS);
        lblVotos.setVerticalAlignment(SwingConstants.CENTER);
        lblVotos.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Botón
        JButton boton = crearBotonVotar(index, nombre);

        // Exponer referencias a Vista (para actualizaciones rápidas)
        vista.etiquetasProductos[index] = lblProducto;
        vista.etiquetasVotos[index]     = lblVotos;
        vista.botonesVotar[index]       = boton;

        // Ensamble
        panelTarjeta.add(lblProducto);
        panelTarjeta.add(Box.createHorizontalGlue());
        panelTarjeta.add(lblVotos);
        panelTarjeta.add(Box.createHorizontalStrut(25));
        panelTarjeta.add(boton);
        return panelTarjeta;
    }

    /** Botón “Votar” con gradiente y listener que delega en el modelo. */
    private JButton crearBotonVotar(int index, String nombreProducto) {
        JButton boton = new JButton("Votar") {
            @Override protected void paintComponent(Graphics g) {
                final Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final int offset = getModel().isPressed() ? 2 : 0;

                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 15, 15);
                }

                final GradientPaint gp = getModel().isPressed()
                        ? new GradientPaint(0, 0, new Color(95, 55, 155), 0, getHeight(), new Color(75, 45, 135))
                        : (getModel().isRollover()
                            ? new GradientPaint(0, 0, new Color(125, 85, 175), 0, getHeight(), new Color(105, 65, 155))
                            : new GradientPaint(0, 0, new Color(106, 64, 166), 0, getHeight(), new Color(90, 54, 142)));
                g2.setPaint(gp);
                g2.fillRoundRect(offset, offset, getWidth() - 6, getHeight() - 6, 15, 15);

                g2.setColor(Color.WHITE);
                g2.setFont(obtenerFuente("Poppins", Font.BOLD, BOTON_FUENTE_SIZE));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + offset;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() + offset;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {/* sin borde */}
        };

        boton.setFont(obtenerFuente("Poppins", Font.BOLD, BOTON_FUENTE_SIZE));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(100, 40));
        boton.setMaximumSize(new Dimension(100, 40));
        boton.setAlignmentY(Component.CENTER_ALIGNMENT);
        boton.setToolTipText("Haz clic para votar por " + nombreProducto);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Intenta usar el método de la Vista (si existe) y, como fallback, agrega el listener directo
        try {
            vista.agregarListenerBotonVoto(index, e -> manejarVoto(index));
        } catch (Exception ex) {
            for (var a : boton.getActionListeners()) boton.removeActionListener(a);
            boton.addActionListener(e -> manejarVoto(index));
        }
        return boton;
    }

    /** Crea un JScrollPane estilizado para el panel principal. */
    private JScrollPane crearScrollPara(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setOpaque(true);
        sp.setOpaque(true);
        sp.getViewport().setBackground(COLOR_GRADIENT_BOTTOM);
        sp.setBackground(COLOR_GRADIENT_BOTTOM);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** Inserta un componente en el panelContenido de la Vista (evita “pantalla doble”). */
    private void insertarEnPanelContenido(JComponent c) {
        JPanel root = vista.getPanelContenido();
        root.removeAll();
        root.add(c, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    // =========================================================
    // RESPONSIVIDAD
    // =========================================================
    /** Padding responsivo con límites conservadores (no empuja contenido al maximizar). */
    private void actualizarEspaciosResponsive() {
        if (panelPrincipal == null || vista == null) return;

        int ancho = Math.max(500, vista.getWidth());
        int alto  = Math.max(500, vista.getHeight());

        int paddingHorizontal = Math.min(48, Math.max(24, ancho / 40));
        int paddingVertical   = Math.min(36, Math.max(16, alto  / 40));

        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(
                paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal
        ));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    // =========================================================
    // LISTENERS DE MENÚ / ACCIONES
    // =========================================================
    private void registrarListeners() {
        Bitacora.registrar(this.getClass(), "registrando listeners");

        // Gráficas
        setSingleAction(vista.itemGraficaBarras, e -> mostrarGraficaBarras());
        setSingleAction(vista.itemGraficaPastel, e -> mostrarGraficaPastel());

        // Bitácora (cliente / servidor comparten la misma vista)
        setSingleAction(vista.itemBitacora, e -> abrirBitacora());
        setSingleAction(vista.getMenuBitacoraServidor(), e -> abrirBitacora());

        // Servicios del Broker
        setSingleAction(vista.getMenuServiciosBroker(), e -> {
            var snapshot = modelo.serviciosBroker();
            new VistaServicios(vista, snapshot, () -> modelo.serviciosBroker()).setVisible(true);
        });
    }

    /** Evita listeners duplicados en un botón/ítem. */
    private void setSingleAction(AbstractButton b, java.awt.event.ActionListener l) {
        if (b == null) return;
        for (var a : b.getActionListeners()) b.removeActionListener(a);
        b.addActionListener(l);
    }

    // =========================================================
    // ACCIONES
    // =========================================================
    /** Abre (o reusa) el diálogo de Bitácora con el proveedor del modelo. */
    private void abrirBitacora() {
        if (dlgBitacora == null || !dlgBitacora.isDisplayable()) {
            dlgBitacora = new VistaBitacora(vista, () -> modelo.bitacora());
            dlgBitacora.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
        dlgBitacora.setLocationRelativeTo(vista);
        dlgBitacora.setVisible(true);
    }

    /** Muestra/actualiza la gráfica de barras usando conteos actuales. */
    private void mostrarGraficaBarras() {
        Conteo c = cargarConteoActual();
        if (graficaBarrasActiva == null || !graficaBarrasActiva.isVisible()) {
            graficaBarrasActiva = new GraficaBarras(c.nombres, c.votos);
        } else {
            graficaBarrasActiva.actualizarDatos(c.nombres, c.votos);
            graficaBarrasActiva.toFront();
            graficaBarrasActiva.requestFocus();
        }
    }

    /** Muestra/actualiza la gráfica de pastel usando conteos actuales. */
    private void mostrarGraficaPastel() {
        Conteo c = cargarConteoActual();
        if (graficaPastelActiva == null || !graficaPastelActiva.isVisible()) {
            graficaPastelActiva = new GraficaPastel(c.nombres, c.votos);
        } else {
            graficaPastelActiva.actualizarDatos(c.nombres, c.votos);
            graficaPastelActiva.toFront();
            graficaPastelActiva.requestFocus();
        }
    }

    // =========================================================
    // VOTACIÓN / REFRESCO
    // =========================================================
    /** Registra voto, actualiza etiqueta del producto y refresca gráficas abiertas. */
    private void manejarVoto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "realizando manejo de votos");
        modelo.registrarVoto(indiceProducto);
        final int votosActualizados = modelo.obtenerVotosProducto(indiceProducto);

        if (vista != null && vista.etiquetasVotos != null && indiceProducto < vista.etiquetasVotos.length) {
            SwingUtilities.invokeLater(() -> vista.actualizarVotos(indiceProducto, votosActualizados));
        }
        actualizarGraficasAbiertas();
    }

    /** Recalcula conteos y, si hay gráficas visibles, las sincroniza. */
    private void actualizarGraficasAbiertas() {
        Conteo c = cargarConteoActual();
        if (graficaBarrasActiva != null && graficaBarrasActiva.isVisible()) {
            graficaBarrasActiva.actualizarDatos(c.nombres, c.votos);
        }
        if (graficaPastelActiva != null && graficaPastelActiva.isVisible()) {
            graficaPastelActiva.actualizarDatos(c.nombres, c.votos);
        }
    }

    // =========================================================
    // UTILIDADES
    // =========================================================
    /** Fuente con fallback a SansSerif si la familia no está disponible. */
    private Font obtenerFuente(String nombre, int estilo, int tamaño) {
        Font f = new Font(nombre, estilo, tamaño);
        if ("Dialog".equals(f.getFamily())) {
            f = new Font("SansSerif", estilo, tamaño);
        }
        return f;
    }

    /** Title Case básico para nombres de productos. */
    private String toTitleCase(String str) {
        if (str == null || str.isEmpty()) return str;
        final String[] words = str.split("\\s+");
        final StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0)))
                                .append(w.substring(1).toLowerCase())
                                .append(" ");
        }
        return sb.toString().trim();
    }

    /** Carga nombres y votos actuales en una sola pasada (evita código duplicado). */
    private Conteo cargarConteoActual() {
        List<String> nombres = modelo.obtenerNombres();
        int n = nombres.size();
        String[] arrNombres = nombres.toArray(new String[0]);
        int[] arrVotos = new int[n];
        for (int i = 0; i < n; i++) arrVotos[i] = modelo.obtenerVotosProducto(i);
        return new Conteo(arrNombres, arrVotos);
    }

    /** Contenedor simple para (nombres, votos). */
    private static final class Conteo {
        final String[] nombres;
        final int[] votos;
        Conteo(String[] nombres, int[] votos) { this.nombres = nombres; this.votos = votos; }
    }
}

