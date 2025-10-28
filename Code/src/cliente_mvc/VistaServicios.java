/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente_mvc;

/**
 *
 * @author elika
 */
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

public class VistaServicios extends JDialog {

    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final TableRowSorter<TableModel> sorter;
    private final JTextField txtBuscar;
    private final JButton btnRefrescar;
    private final JLabel lblTotal;
    private final Supplier<List<String[]>> proveedorServicios;

    public VistaServicios(Frame owner,
                          List<String[]> serviciosIniciales,
                          Supplier<List<String[]>> proveedorServicios) {
        super(owner, "Servicios registrados en el broker", true);
        this.proveedorServicios = Objects.requireNonNull(proveedorServicios, "proveedorServicios es requerido");

        modeloTabla = new DefaultTableModel(new Object[]{"Servicio", "Dirección"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(sorter);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.LEFT);
        tabla.getColumnModel().getColumn(0).setCellRenderer(center);
        tabla.getColumnModel().getColumn(1).setCellRenderer(center);

        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Buscar por servicio o dirección...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> recargarDesdeProveedor());

        lblTotal = new JLabel("Total: 0");

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.add(btnRefrescar);
        right.add(lblTotal);
        top.add(new JLabel("Buscar:"), BorderLayout.WEST);
        top.add(txtBuscar, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        setLayout(new BorderLayout(8, 8));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        setPreferredSize(new Dimension(720, 420));
        setMinimumSize(new Dimension(600, 360));
        setLocationRelativeTo(owner);

        cargar(serviciosIniciales);
    }

    private void cargar(List<String[]> servicios) {
        modeloTabla.setRowCount(0);
        if (servicios != null) {
            for (String[] s : servicios) {
                if (s == null || s.length < 2) continue;
                modeloTabla.addRow(new Object[]{s[0], s[1]});
            }
        }
        lblTotal.setText("Total: " + modeloTabla.getRowCount());
        filtrar();
    }

    private void recargarDesdeProveedor() {
        try {
            List<String[]> nuevos = proveedorServicios.get();
            cargar(nuevos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener la lista del broker.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrar() {
        String q = txtBuscar.getText();
        if (q == null || q.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }
        final String query = q.trim().toLowerCase(Locale.ROOT);
        sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                String servicio = String.valueOf(entry.getValue(0)).toLowerCase(Locale.ROOT);
                String direccion = String.valueOf(entry.getValue(1)).toLowerCase(Locale.ROOT);
                return servicio.contains(query) || direccion.contains(query);
            }
        });
    }
}
