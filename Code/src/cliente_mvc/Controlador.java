/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author elika
 */
import servidor_Votos.Bitacora;
import servidor_Votos.DominioModelo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador principal del sistema de votaciones
 * Gestiona la interacción entre la vista y el modelo
 * Implementa ActionListener para manejar eventos de menú
 */
public final class Controlador implements ActionListener {

    // ============================================
    // CONSTANTES DE DISEÑO
    // ============================================
    
    // Colores base para gradientes y componentes
    private static final Color COLOR_GRADIENT_TOP = new Color(136, 84, 208);
    private static final Color COLOR_GRADIENT_BOTTOM = new Color(74, 144, 226);
    private static final Color COLOR_TITULO = Color.WHITE;
    private static final Color COLOR_SUBTITULO = new Color(255, 255, 255, 200);
    private static final Color COLOR_TEXTO_PRODUCTO = new Color(28, 28, 28);
    private static final Color COLOR_VOTOS = new Color(120, 120, 120);

    // Tamaños de fuentes para diferentes elementos
    private static final int TITULO_SIZE = 36;
    private static final int SUBTITULO_SIZE = 16;
    private static final int PRODUCTO_TITULO_SIZE = 22;
    private static final int PRODUCTO_VOTOS_SIZE = 16;
    private static final int BOTON_FUENTE_SIZE = 15;

    // ============================================
    // ATRIBUTOS
    // ============================================
    
    private final Vista vista;                  // Referencia a la vista
    private final Modelo modelo;                // Referencia al modelo
    private JPanel panelPrincipal;              // Panel central con los productos
    
    // Referencias a las gráficas abiertas actualmente
    private GraficaBarras graficaBarrasActiva;
    private GraficaPastel graficaPastelActiva;

    // ============================================
    // CONSTRUCTOR
    // ============================================
    
    /**
     * Inicializa el controlador con referencias a vista y modelo
     * Configura la interfaz gráfica y registra los listeners
     * @param vista referencia a la ventana principal
     * @param modelo referencia al modelo de datos
     */
    public Controlador(Vista vista, Modelo modelo) {
        Bitacora.registrar(this.getClass(), "inicializando controlador");
        this.vista = vista;
        this.modelo = modelo;

        inicializarVista();
        registrarListeners();
    }

    // ============================================
    // UTILIDADES DE FUENTES
    // ============================================
    
    /**
     * Crea una fuente con manejo de fallback a SansSerif
     * Si la fuente solicitada no está disponible, usa SansSerif como alternativa
     * @param nombre nombre de la fuente deseada
     * @param estilo estilo de fuente (Font.BOLD, Font.PLAIN, etc.)
     * @param tamaño tamaño de la fuente en puntos
     * @return Font configurada con el estilo especificado
     */
    private Font obtenerFuente(String nombre, int estilo, int tamaño) {
        Font f = new Font(nombre, estilo, tamaño);
        if ("Dialog".equals(f.getFamily())) {
            f = new Font("SansSerif", estilo, tamaño);
        }
        return f;
    }

    // ============================================
    // INICIALIZACIÓN PRINCIPAL DE LA UI
    // ============================================
    
    /**
     * Inicializa todos los componentes visuales de la interfaz
     * Crea el panel principal, título, productos y botones de votación
     * Configura el scroll y la responsividad
     */
    public void inicializarVista() {
        Bitacora.registrar(this.getClass(), "inicializando vista");

        int cantidadProductos = modelo.obtenerNumeroProductos();
        vista.botonesVotar = new JButton[cantidadProductos];
        vista.etiquetasVotos = new JLabel[cantidadProductos];
        vista.etiquetasProductos = new JLabel[cantidadProductos];

        // Crear panel principal con gradiente
        panelPrincipal = crearPanelPrincipal();

        // Agregar título
        JLabel titulo = crearTitulo("Sistema de Votación", TITULO_SIZE, Font.BOLD);
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createVerticalStrut(15));

        // Agregar separador decorativo
        panelPrincipal.add(crearSeparador(100, 4));
        panelPrincipal.add(Box.createVerticalStrut(15));

        // Agregar subtítulo
        JLabel subtitulo = new JLabel("Vota por tu producto favorito");
        subtitulo.setFont(obtenerFuente("Poppins", Font.PLAIN, SUBTITULO_SIZE));
        subtitulo.setForeground(COLOR_SUBTITULO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(subtitulo);
        panelPrincipal.add(Box.createVerticalStrut(40));

        // Crear tarjeta para cada producto con sus componentes
        for (int i = 0; i < cantidadProductos; i++) {
            JPanel tarjeta = crearTarjetaProducto(i);
            panelPrincipal.add(tarjeta);
            panelPrincipal.add(Box.createVerticalStrut(20));
        }

        // Agregar scroll pane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        vista.getContentPane().add(scrollPane);

        // Configurar tamaño y posición de la ventana
        vista.setMinimumSize(new Dimension(500, 600));
        vista.setPreferredSize(new Dimension(900, 800));
        vista.setLocationRelativeTo(null);
        vista.setResizable(true);
        vista.setVisible(true);

        // Agregar listener para responsividad al redimensionar
        vista.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                actualizarEspaciosResponsive();
            }
        });

        actualizarEspaciosResponsive();
    }

    // ============================================
    // COMPONENTES DE DISEÑO (PANEL PRINCIPAL)
    // ============================================
    
    /**
     * Crea el panel principal con gradiente de color
     * @return JPanel configurado con BoxLayout vertical y fondo gradiente
     */
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Aplicar gradiente de púrpura a azul de arriba a abajo
                GradientPaint gp = new GradientPaint(
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
        return panel;
    }

    /**
     * Crea un título personalizado con fuente y color especificados
     * @param texto contenido del título
     * @param tamaño tamaño de la fuente
     * @param estilo estilo de la fuente (Font.BOLD, etc.)
     * @return JLabel configurado como título
     */
    private JLabel crearTitulo(String texto, int tamaño, int estilo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(obtenerFuente("Poppins", estilo, tamaño));
        lbl.setForeground(COLOR_TITULO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un separador visual (línea decorativa redondeada)
     * @param ancho ancho del separador en píxeles
     * @param alto alto del separador en píxeles
     * @return JComponent que funciona como separador visual
     */
    private JComponent crearSeparador(int ancho, int alto) {
        JPanel linea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
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

    // ============================================
    // TARJETAS DE PRODUCTOS
    // ============================================
    
    /**
     * Crea una tarjeta visual para un producto específico
     * Contiene: nombre del producto, cantidad de votos y botón de votación
     * @param index índice del producto en el modelo
     * @return JPanel con la tarjeta del producto
     */
    private JPanel crearTarjetaProducto(int index) {
        String nombre = toTitleCase(modelo.obtenerNombreProducto(index));
        int numVotos = modelo.obtenerVotosProducto(index);

        // Crear panel con fondo personalizado (sombra y efecto de tarjeta)
        JPanel panelTarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar sombra (rectángulo oscuro desplazado)
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 8, getWidth() - 8, getHeight() - 10, 20, 20);

                // Dibujar fondo principal (rectángulo blanco)
                g2.setColor(new Color(255, 255, 255, 250));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 10, 20, 20);

                g2.dispose();
            }
        };
        panelTarjeta.setLayout(new BoxLayout(panelTarjeta, BoxLayout.X_AXIS));
        panelTarjeta.setOpaque(false);
        panelTarjeta.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelTarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTarjeta.setMaximumSize(new Dimension(700, 80));

        // Crear etiqueta del nombre del producto
        JLabel lblProducto = new JLabel(nombre);
        lblProducto.setFont(obtenerFuente("Poppins", Font.BOLD, PRODUCTO_TITULO_SIZE));
        lblProducto.setForeground(COLOR_TEXTO_PRODUCTO);
        lblProducto.setVerticalAlignment(SwingConstants.CENTER);
        lblProducto.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Crear etiqueta de votos
        JLabel lblVotos = new JLabel(numVotos + " votos");
        lblVotos.setFont(obtenerFuente("Poppins", Font.PLAIN, PRODUCTO_VOTOS_SIZE));
        lblVotos.setForeground(COLOR_VOTOS);
        lblVotos.setVerticalAlignment(SwingConstants.CENTER);
        lblVotos.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Crear botón de votación
        JButton boton = crearBotonVotar(index, nombre);

        // Guardar referencias a los componentes en la vista
        vista.etiquetasProductos[index] = lblProducto;
        vista.etiquetasVotos[index] = lblVotos;
        vista.botonesVotar[index] = boton;

        // Agregar componentes a la tarjeta
        panelTarjeta.add(lblProducto);
        panelTarjeta.add(Box.createHorizontalGlue());
        panelTarjeta.add(lblVotos);
        panelTarjeta.add(Box.createHorizontalStrut(25));
        panelTarjeta.add(boton);

        return panelTarjeta;
    }

    // ============================================
    // BOTONES DE VOTACIÓN
    // ============================================
    
    /**
     * Crea un botón personalizado para votar por un producto
     * Incluye efecto de gradiente, sombra y cambios de color al presionar/pasar mouse
     * @param index índice del producto
     * @param nombreProducto nombre del producto para el tooltip
     * @return JButton personalizado con comportamiento de votación
     */
    private JButton crearBotonVotar(int index, String nombreProducto) {
        JButton boton = new JButton("Votar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Calcular desplazamiento para efecto de presión
                int offset = getModel().isPressed() ? 2 : 0;

                // Dibujar sombra solo si no está presionado
                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 15, 15);
                }

                // Aplicar gradiente según estado del botón
                GradientPaint gp;
                if (getModel().isPressed()) {
                    // Gradiente oscuro cuando está presionado
                    gp = new GradientPaint(0, 0, new Color(95, 55, 155), 0, getHeight(), new Color(75, 45, 135));
                } else if (getModel().isRollover()) {
                    // Gradiente más claro cuando el mouse está sobre el botón
                    gp = new GradientPaint(0, 0, new Color(125, 85, 175), 0, getHeight(), new Color(105, 65, 155));
                } else {
                    // Gradiente normal en estado reposo
                    gp = new GradientPaint(0, 0, new Color(106, 64, 166), 0, getHeight(), new Color(90, 54, 142));
                }
                g2.setPaint(gp);
                g2.fillRoundRect(offset, offset, getWidth() - 6, getHeight() - 6, 15, 15);

                // Dibujar texto del botón centrado
                g2.setColor(Color.WHITE);
                g2.setFont(obtenerFuente("Poppins", Font.BOLD, BOTON_FUENTE_SIZE));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + offset;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() + offset;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Sin borde personalizado
            }
        };

        // Configurar propiedades del botón
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

        // Agregar listener para manejar el voto
        try {
            vista.agregarListenerBotonVoto(index, e -> manejarVoto(index));
        } catch (Exception ex) {
            boton.addActionListener(e -> manejarVoto(index));
        }

        return boton;
    }

    // ============================================
    // RESPONSIVIDAD
    // ============================================
    
    /**
     * Actualiza los espacios y márgenes cuando la ventana se redimensiona
     * Ajusta dinámicamente el padding basado en el tamaño de la ventana
     */
    private void actualizarEspaciosResponsive() {
        if (panelPrincipal == null || vista == null) return;
        int ancho = Math.max(500, vista.getWidth());
        int alto = Math.max(500, vista.getHeight());

        // Calcular padding proporcional al tamaño de la ventana
        int paddingHorizontal = Math.max(20, ancho / 20);
        int paddingVertical = Math.max(20, alto / 15);

        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(
                paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal
        ));
    }

    // ============================================
    // LISTENERS Y MANEJO DE EVENTOS
    // ============================================
    
    /**
     * Registra los listeners para los items del menú
     * Conecta los elementos del menú con el controlador
     */
    private void registrarListeners() {
        Bitacora.registrar(this.getClass(), "registrando listeners");
        try {
            vista.itemGraficaBarras.addActionListener(this);
            vista.itemGraficaPastel.addActionListener(this);
            vista.itemBitacora.addActionListener(this);
        } catch (Exception ex) {
            // Ignorar si los componentes no están disponibles aún
        }
    }

    /**
     * Maneja el evento de votación cuando un usuario hace clic en "Votar"
     * Registra el voto en el modelo y actualiza la vista
     * También actualiza las gráficas abiertas
     * @param indiceProducto índice del producto votado
     */
    private void manejarVoto(int indiceProducto) {
        Bitacora.registrar(this.getClass(), "realizando manejo de votos");
        // Registrar el voto en el modelo
        modelo.registrarVoto(indiceProducto);
        int votosActualizados = modelo.obtenerVotosProducto(indiceProducto);
        
        // Actualizar la etiqueta de votos en la vista
        if (vista != null && vista.etiquetasVotos != null && indiceProducto < vista.etiquetasVotos.length) {
            SwingUtilities.invokeLater(() -> {
                vista.actualizarVotos(indiceProducto, votosActualizados);
            });
        }
        
        // Actualizar gráficas si están abiertas
        actualizarGraficasAbiertas();
    }

    /**
     * Actualiza los datos de las gráficas activas (barras y pastel)
     * Se ejecuta cada vez que hay un nuevo voto
     */
    private void actualizarGraficasAbiertas() {
        int cantidad = modelo.obtenerNumeroProductos();
        String[] nombreProductos = new String[cantidad];
        int[] votos = new int[cantidad];

        // Recopilar datos actualizados del modelo
        for (int i = 0; i < cantidad; i++) {
            nombreProductos[i] = modelo.obtenerNombreProducto(i);
            votos[i] = modelo.obtenerVotosProducto(i);
        }

        // Actualizar gráfica de barras si está visible
        if (graficaBarrasActiva != null && graficaBarrasActiva.isVisible()) {
            graficaBarrasActiva.actualizarDatos(nombreProductos, votos);
        }

        // Actualizar gráfica de pastel si está visible
        if (graficaPastelActiva != null && graficaPastelActiva.isVisible()) {
            graficaPastelActiva.actualizarDatos(nombreProductos, votos);
        }
    }

    // ============================================
    // HANDLER DE EVENTOS DEL MENÚ
    // ============================================
    
    /**
     * Maneja los eventos de acción de los items del menú
     * Implementa ActionListener para responder a clics en menús
     * @param e ActionEvent que contiene la información del evento
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Bitacora.registrar(this.getClass(), "invocando actionPerformed para botones del menú");

        // Recopilar datos actualizados para las gráficas
        int cantidad = modelo.obtenerNumeroProductos();
        String[] nombreProductos = new String[cantidad];
        int[] votos = new int[cantidad];

        for (int i = 0; i < cantidad; i++) {
            nombreProductos[i] = modelo.obtenerNombreProducto(i);
            votos[i] = modelo.obtenerVotosProducto(i);
        }

        // Manejar clic en "Gráfica de Barras"
        if (e.getSource() == vista.itemGraficaBarras) {
            if (graficaBarrasActiva == null || !graficaBarrasActiva.isVisible()) {
                // Crear nueva gráfica si no existe o fue cerrada
                graficaBarrasActiva = new GraficaBarras(nombreProductos, votos);
            } else {
                // Si ya existe, traerla al frente
                graficaBarrasActiva.toFront();
                graficaBarrasActiva.requestFocus();
            }
        }
        // Manejar clic en "Gráfica de Pastel"
        else if (e.getSource() == vista.itemGraficaPastel) {
            if (graficaPastelActiva == null || !graficaPastelActiva.isVisible()) {
                // Crear nueva gráfica si no existe o fue cerrada
                graficaPastelActiva = new GraficaPastel(nombreProductos, votos);
            } else {
                // Si ya existe, traerla al frente
                graficaPastelActiva.toFront();
                graficaPastelActiva.requestFocus();
            }
        }
        // Manejar clic en "Bitácora"
        else if (e.getSource() == vista.itemBitacora) {
            vista.mostrarBitacora();
        }
    }

    // ============================================
    // UTILIDADES
    // ============================================
    
    /**
     * Convierte un string a formato Title Case (primera letra en mayúscula)
     * Ejemplo: "producto uno" -> "Producto Uno"
     * @param str string a convertir
     * @return string en formato Title Case
     */
    private String toTitleCase(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}