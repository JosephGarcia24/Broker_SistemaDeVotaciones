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
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VistaBitacora extends JDialog {

    private final JTextField txtBuscar;
    private final JTextArea txtContenido;
    private final JButton btnRefrescar;
    private final JLabel lblContadores;
    private final Supplier<List<String>> proveedorBitacora;

    private List<String> cache = new ArrayList<>();

    public VistaBitacora(Frame owner, Supplier<List<String>> proveedorBitacora) {
        super(owner, "Bitácora del servidor", true);
        this.proveedorBitacora = Objects.requireNonNull(proveedorBitacora, "proveedorBitacora es requerido");

        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Buscar texto en la bitácora...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> recargar());

        lblContadores = new JLabel("Registros: 0 | Coincidencias: 0");

        txtContenido = new JTextArea();
        txtContenido.setEditable(false);
        txtContenido.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtContenido.setLineWrap(false);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.add(btnRefrescar);
        right.add(lblContadores);

        top.add(new JLabel("Buscar:"), BorderLayout.WEST);
        top.add(txtBuscar, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        setLayout(new BorderLayout(8, 8));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(txtContenido), BorderLayout.CENTER);

        setPreferredSize(new Dimension(780, 480));
        setMinimumSize(new Dimension(600, 380));
        setLocationRelativeTo(owner);

        recargar();
    }

    private void recargar() {
        try {
            List<String> datos = proveedorBitacora.get();
            cache = (datos == null) ? new ArrayList<>() : new ArrayList<>(datos);
            actualizarTexto(cache);
            actualizarContadores(cache.size(), cache.size());
            aplicarFiltro();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener la bitácora del servidor.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        String q = txtBuscar.getText();
        if (q == null || q.isBlank()) {
            actualizarTexto(cache);
            actualizarContadores(cache.size(), cache.size());
            return;
        }
        final String query = q.trim().toLowerCase(Locale.ROOT);
        List<String> filtrados = cache.stream()
                .filter(l -> l != null && l.toLowerCase(Locale.ROOT).contains(query))
                .collect(Collectors.toList());
        actualizarTexto(filtrados);
        actualizarContadores(cache.size(), filtrados.size());
    }

    private void actualizarTexto(List<String> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            txtContenido.setText("(Sin registros)");
        } else {
            String contenido = String.join(System.lineSeparator(), lineas);
            txtContenido.setText(contenido);
            txtContenido.setCaretPosition(0);
        }
    }

    private void actualizarContadores(int total, int coincidencias) {
        lblContadores.setText("Registros: " + total + " | Coincidencias: " + coincidencias);
    }
}

