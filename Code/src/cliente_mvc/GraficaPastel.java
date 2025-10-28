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
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class GraficaPastel extends JFrame {
    
    // ============================================
    // ATRIBUTOS
    // ============================================
    
    private final String[] productos;
    private final int[] votos;
    private ChartPanel chartPanel;
    private DefaultPieDataset dataset;
    private JFreeChart chart;

    // ============================================
    // CONSTRUCTOR
    // ============================================
    
    /**
     * Crea una ventana con gráfica de pastel que muestra la distribución de votos
     * @param productos nombres de los productos a mostrar
     * @param votos cantidad de votos para cada producto
     * @throws IllegalArgumentException si los arreglos son nulos o tienen diferente longitud
     */
    public GraficaPastel(String[] productos, int[] votos) {
        Bitacora.registrar(this.getClass(), "Creando gráfica de pastel");

        // ============================================
        // VALIDACIÓN DE PARÁMETROS
        // ============================================
        
        if (productos == null || votos == null || productos.length != votos.length) {
            throw new IllegalArgumentException("Los arreglos de productos y votos deben tener la misma longitud y no ser nulos.");
        }

        this.productos = productos;
        this.votos = votos;

        // Configuración básica de la ventana
        setTitle("Gráfica de Pastel - Votos por Producto");
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
                Graphics2D g2d = (Graphics2D) g.create();
                // Aplicar gradiente de arriba a abajo con colores púrpura a azul
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(136, 84, 208),
                    0, getHeight(), new Color(74, 144, 226)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        panelPrincipal.setLayout(new BorderLayout());
        chartPanel = crearGraficaPastel();
        panelPrincipal.add(chartPanel, BorderLayout.CENTER);

        add(panelPrincipal);
        setVisible(true);
    }

    // ============================================
    // CREACIÓN DE LA GRÁFICA
    // ============================================
    
    /**
     * Construye y personaliza la gráfica de pastel con los datos de productos y votos
     * Solo incluye productos con votos positivos
     * @return ChartPanel que contiene la gráfica lista para mostrar
     */
    private ChartPanel crearGraficaPastel() {
        Bitacora.registrar(this.getClass(), "Inicializando gráfica de pastel");

        // Crear dataset para gráfica de pastel
        dataset = new DefaultPieDataset();

        // Agregar solo los productos que tienen votos positivos
        for (int i = 0; i < productos.length; i++) {
            if (votos[i] > 0) {
                dataset.setValue(productos[i], votos[i]);
            }
        }

        // Crear gráfica de pastel usando JFreeChart
        chart = ChartFactory.createPieChart(
            "Distribución de Votos por Producto",
            dataset,
            true,   // Con leyenda
            true,   // Con tooltips
            false
        );

        personalizarGrafica(chart);

        // Crear panel que contendrá la gráfica
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(980, 720));
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));  // Fondo transparente
        return panel;
    }

    // ============================================
    // ACTUALIZACIÓN DE DATOS
    // ============================================
    
    /**
     * Actualiza los datos de la gráfica cuando hay nuevos votos
     * Solo incluye productos con votos positivos
     * @param productosNuevos arreglo actualizado de nombres de productos
     * @param votosNuevos arreglo actualizado de votos
     */
    public void actualizarDatos(String[] productosNuevos, int[] votosNuevos) {
        // Ejecutar en el hilo de eventos de Swing para evitar problemas de concurrencia
        SwingUtilities.invokeLater(() -> {
            // Limpiar dataset anterior
            dataset.clear();
            
            // Agregar nuevos datos (solo los que tengan votos positivos)
            for (int i = 0; i < productosNuevos.length; i++) {
                if (votosNuevos[i] > 0) {
                    dataset.setValue(productosNuevos[i], votosNuevos[i]);
                }
            }
            
            // Forzar redibujado de la gráfica
            chartPanel.repaint();
        });
    }

    // ============================================
    // PERSONALIZACIÓN DE ESTILO
    // ============================================
    
    /**
     * Aplica personalización visual a la gráfica: colores, fuentes, leyenda
     * @param chart gráfica a personalizar
     */
    private void personalizarGrafica(JFreeChart chart) {
        
        // Paleta de colores para los segmentos del pastel
        Color[] colores = {
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

        // ============================================
        // CONFIGURACIÓN DEL PLOT (ÁREA DE GRÁFICA)
        // ============================================
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0, 0, 0, 0));  // Fondo transparente
        plot.setOutlineVisible(false);                   // Sin bordes
        plot.setLabelGenerator(null);                    // Sin etiquetas en segmentos

        // Asignar colores a cada segmento del pastel
        int colorIndex = 0;
        for (Object key : plot.getDataset().getKeys()) {
            plot.setSectionPaint((String) key, colores[colorIndex % colores.length]);
            colorIndex++;
        }

        // ============================================
        // CONFIGURACIÓN DE FUENTES
        // ============================================
        
        Font fontTitulo = new Font("Poppins", Font.BOLD, 28);
        if (fontTitulo.getFamily().equals("Dialog")) {
            fontTitulo = new Font("SansSerif", Font.BOLD, 28);
        }

        // Configurar título de la gráfica
        chart.getTitle().setFont(fontTitulo);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setMargin(20, 0, 20, 0);

        // ============================================
        // CONFIGURACIÓN DE LEYENDA
        // ============================================
        
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 14));
        chart.getLegend().setBackgroundPaint(new Color(0, 0, 0, 100));  // Fondo semi-transparente
        chart.getLegend().setItemPaint(Color.WHITE);                     // Texto en blanco
        chart.getLegend().setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);  // Leyenda abajo
        chart.getLegend().setMargin(0, 0, 20, 0);

        // Fondo de la gráfica transparente
        chart.setBackgroundPaint(new Color(0, 0, 0, 0));
    }
}