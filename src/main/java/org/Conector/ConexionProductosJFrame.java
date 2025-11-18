package org.Conector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ConexionProductosJFrame extends JFrame {

    private final JTextField servidorField = new JTextField();
    private final JTextField puertoField = new JTextField("3307");
    private final JTextField usuarioField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public ConexionProductosJFrame() {
        super("Conexión a Base de Datos Northwind");

        setLayout(new GridLayout(5, 2, 5, 5));
        add(new JLabel("Servidor:"));
        add(servidorField);
        add(new JLabel("Puerto:"));
        add(puertoField);
        add(new JLabel("Usuario:"));
        add(usuarioField);
        add(new JLabel("Contraseña:"));
        add(passwordField);

        JButton conectarButton = new JButton("Conectar");
        add(new JLabel());
        add(conectarButton);

        conectarButton.addActionListener(this::conectar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // <<< CAMBIOS REALIZADOS ABAJO >>>
    private void conectar(ActionEvent e) {
        String servidor = servidorField.getText().trim();
        String puerto = puertoField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validaciones
        if (servidor.isEmpty() || puerto.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }
        if (!puerto.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser numérico.");
            return;
        }

        // Cambio: Construir la URL de conexión usando los datos del formulario
        String conn = "jdbc:mysql://localhost:3307/NorthWind";

        // Cambio: Intentar conectar y mostrar mensaje según éxito o error
        try (Connection connection = DriverManager.getConnection(conn, usuario, password)) {
            JOptionPane.showMessageDialog(this, "Conexión exitosa.");
            // JFrame de productos
            new ProductosJFrame(connection);
            // Cerramos el JFrame de conexión
            this.dispose();
            // pasando 'connection' o los parámetros para una nueva conexión.
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConexionProductosJFrame::new);
    }
}
