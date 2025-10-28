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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class GraficaBarras extends JFrame {
    
    // ============================================
    // ATRIBUTOS
    // ============================================
    
    private final String[] productos;
    private final int[] votos;
    private ChartPanel chartPanel;
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;

    // ============================================
    // CONSTRUCTOR
    // ============================================
    
    /**
     * Crea una ventana con gráfica de barras que muestra los votos por producto
     * @param productos nombres de los productos a mostrar
     * @param votos cantidad de votos para cada producto
     */
    public GraficaBarras(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "Creando gráfica de barras");
        this.productos = productos;
        this.votos = votos;

        // Configuración básica de la ventana
        setTitle("Gráfica de Barras - Votos por Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(500, 450));

        // ============================================
        // PANEL PRINCIPAL CON GRADIENTE
        // ============================================
        
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Aplicar gradiente de arriba a abajo con colores púrpura a azul
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(136, 84, 208),
                        0, getHeight(), new Color(74, 144, 226)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panelPrincipal.setLayout(new BorderLayout());
        chartPanel = crearGraficaBarras();
        panelPrincipal.add(chartPanel, BorderLayout.CENTER);

        add(panelPrincipal);
        setVisible(true);
    }

    // ============================================
    // CREACIÓN DE LA GRÁFICA
    // ============================================
    
    /**
     * Construye y personaliza la gráfica de barras con los datos de productos y votos
     * @return ChartPanel que contiene la gráfica lista para mostrar
     */
    private ChartPanel crearGraficaBarras() {
        Bitacora.registrar(this.getClass(), "Inicializando gráfica de barras");

        // Crear dataset y agregar datos de productos y votos
        dataset = new DefaultCategoryDataset();
        for (int i = 0; i < productos.length; i++) {
            dataset.addValue(votos[i], "Votos", productos[i]);
        }

        // Crear gráfica de barras verticales usando JFreeChart
        chart = ChartFactory.createBarChart(
                "Votos por Producto",
                "Productos",
                "Cantidad de Votos",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false,  // Sin leyenda
                true,   // Con tooltips
                false
        );

        personalizarGrafica(chart);
        
        // Crear panel que contendrá la gráfica
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(980, 720));
        panel.setOpaque(false);
        return panel;
    }

    // ============================================
    // ACTUALIZACIÓN DE DATOS
    // ============================================
    
    /**
     * Actualiza los datos de la gráfica cuando hay nuevos votos
     * @param productosNuevos arreglo actualizado de nombres de productos
     * @param votosNuevos arreglo actualizado de votos
     */
    public void actualizarDatos(String[] productosNuevos, int[] votosNuevos) {
        // Ejecutar en el hilo de eventos de Swing para evitar problemas de concurrencia
        SwingUtilities.invokeLater(() -> {
            // Limpiar dataset anterior
            dataset.clear();
            
            // Agregar nuevos datos al dataset
            for (int i = 0; i < productosNuevos.length; i++) {
                dataset.setValue(votosNuevos[i], "Votos", productosNuevos[i]);
            }
            
            // Forzar redibujado de la gráfica
            chartPanel.repaint();
        });
    }

    // ============================================
    // PERSONALIZACIÓN DE ESTILO
    // ============================================
    
    /**
     * Aplica personalización visual a la gráfica: colores, fuentes, gradientes
     * @param chart gráfica a personalizar
     */
    private void personalizarGrafica(JFreeChart chart) {
        
        // Paleta de colores para las barras
        Color[] coloresBase = {
                new Color(106, 64, 166),    // Púrpura oscuro
                new Color(255, 107, 107),   // Rojo coral
                new Color(75, 192, 192),    // Verde menta
                new Color(255, 159, 64),    // Naranja
                new Color(153, 102, 255),   // Púrpura claro
                new Color(201, 203, 207),   // Gris claro
                new Color(255, 205, 86),    // Amarillo
                new Color(231, 233, 237),   // Gris muy claro
                new Color(100, 200, 150),   // Verde suave
                new Color(220, 150, 100)    // Marrón suave
        };

        // Configurar área de gráfica (plot)
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setDomainGridlinePaint(new Color(255, 255, 255, 60));      // Líneas horizontales sutiles
        plot.setRangeGridlinePaint(new Color(255, 255, 255, 60));       // Líneas verticales sutiles

        // ============================================
        // CONFIGURACIÓN DE FUENTES
        // ============================================
        
        Font fontTitulo = new Font("Poppins", Font.BOLD, 28);
        if (fontTitulo.getFamily().equals("Dialog")) {
            fontTitulo = new Font("SansSerif", Font.BOLD, 28);
        }

        Font fontEjes = new Font("Poppins", Font.PLAIN, 14);
        if (fontEjes.getFamily().equals("Dialog")) {
            fontEjes = new Font("SansSerif", Font.PLAIN, 14);
        }

        // Configurar eje X (categorías de productos)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(fontEjes);
        domainAxis.setLabelFont(fontEjes);
        domainAxis.setTickLabelPaint(Color.WHITE);
        domainAxis.setLabelPaint(Color.WHITE);

        // Configurar eje Y (cantidad de votos)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(fontEjes);
        rangeAxis.setLabelFont(fontEjes);
        rangeAxis.setTickLabelPaint(Color.WHITE);
        rangeAxis.setLabelPaint(Color.WHITE);

        // ============================================
        // RENDERIZADOR DE BARRAS CON GRADIENTES
        // ============================================
        
        /**
         * Clase interna que extiende BarRenderer para aplicar gradientes a las barras
         */
        class ShadedBarRenderer extends BarRenderer {
            private final Color[] colors;

            public ShadedBarRenderer(Color[] colors) {
                this.colors = colors;
                setBarPainter(new StandardBarPainter());
                // Generar tooltips con formato: "Producto: X votos"
                setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                        "{1}: {2} votos", java.text.NumberFormat.getInstance()));
            }

            /**
             * Retorna un gradiente para cada barra alternando colores
             */
            @Override
            public Paint getItemPaint(int row, int column) {
                Color base = colors[column % colors.length];
                // Crear gradiente de color más claro a más oscuro
                return new GradientPaint(0f, 0f, base.brighter(), 0f, 30f, base.darker());
            }
        }

        // Crear y configurar el renderizador
        ShadedBarRenderer renderer = new ShadedBarRenderer(coloresBase);
        renderer.setItemMargin(0.2);              // Espacio entre barras
        renderer.setShadowVisible(true);          // Mostrar sombras
        renderer.setShadowXOffset(3);
        renderer.setShadowYOffset(4);
        renderer.setShadowPaint(new Color(120, 80, 50, 100));

        plot.setRenderer(renderer);

        // ============================================
        // CONFIGURACIÓN DE TÍTULO
        // ============================================
        
        chart.getTitle().setFont(fontTitulo);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setMargin(15, 0, 20, 0);

        chart.setBackgroundPaint(null);
    }
}