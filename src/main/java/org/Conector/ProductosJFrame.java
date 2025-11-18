package org.Conector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.io.*;
import java.util.Vector;

public class ProductosJFrame extends JFrame {
    private final JTable tablaProductos = new JTable();
    private final DefaultTableModel modeloTabla = new DefaultTableModel();
    private final Connection conn;

    public ProductosJFrame(Connection conn) {
        super("Gestión de Productos Northwind");
        this.conn = conn;

        setLayout(new BorderLayout());

        // Panel de botones arriba
        JPanel panelBotones = new JPanel();
        JButton btnMostrar = new JButton("Mostrar productos");
        JButton btnImportar = new JButton("Importar desde fichero");
        JButton btnExportar = new JButton("Exportar a fichero");
        panelBotones.add(btnMostrar);
        panelBotones.add(btnImportar);
        panelBotones.add(btnExportar);
        add(panelBotones, BorderLayout.NORTH);

        // Tabla en scrollpane en el centro
        tablaProductos.setModel(modeloTabla);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Botón para mostrar todos los productos
        btnMostrar.addActionListener(e -> mostrarProductos());

        // Botón para exportar todos los productos a fichero
        btnExportar.addActionListener(e -> exportarProductos());

        // Botón para importar productos desde un fichero
        btnImportar.addActionListener(e -> importarProductos());
    }

    private void mostrarProductos() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products")) {

            // Limpiar el modelo de tabla
            modeloTabla.setRowCount(0);
            modeloTabla.setColumnCount(0);

            // Encabezados
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            Vector<String> headers = new Vector<>();
            for (int i = 1; i <= cols; i++) {
                headers.add(meta.getColumnName(i));
            }
            modeloTabla.setColumnIdentifiers(headers);

            // Filas
            int numRegistros = 0;
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= cols; i++) {
                    fila.add(rs.getObject(i));
                }
                modeloTabla.addRow(fila);
                numRegistros++;
            }
            // Mensaje opcional si quieres
            // JOptionPane.showMessageDialog(this, "Se han mostrado " + numRegistros + " registros.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al mostrar productos: " + ex.getMessage());
        }
    }

    private void exportarProductos() {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File fichero = chooser.getSelectedFile();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM products");
                 BufferedWriter bw = new BufferedWriter(new FileWriter(fichero))) {

                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();

                // Encabezados CSV
                for (int i = 1; i <= cols; i++) {
                    bw.write(meta.getColumnName(i));
                    if (i < cols) bw.write(",");
                }
                bw.newLine();

                int numExportados = 0;
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        bw.write(rs.getString(i) != null ? rs.getString(i) : "");
                        if (i < cols) bw.write(",");
                    }
                    bw.newLine();
                    numExportados++;
                }
                JOptionPane.showMessageDialog(this, "SE HAN EXPORTADO " + numExportados + " REGISTROS.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void importarProductos() {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File fichero = chooser.getSelectedFile();
            int numImportados = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
                // Leer encabezado para identificar el orden
                String encabezado = br.readLine();
                if (encabezado == null) throw new IOException("Fichero vacío");

                // Asume inserción comenzando por la segunda línea
                String linea;
                conn.setAutoCommit(false); // Mejorar rendimiento
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    for (int i = 0; i < datos.length; i++) {
                        ps.setString(i + 1, datos[i]);
                    }
                    ps.executeUpdate();
                    numImportados++;
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "SE HAN IMPORTADO " + numImportados + " REGISTROS.");
                mostrarProductos(); // Refrescar tabla
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar: " + ex.getMessage());
                try { conn.rollback(); } catch (SQLException ignored) {}
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }
}
