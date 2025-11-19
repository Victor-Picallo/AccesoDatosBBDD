package Importante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

// Define la clase principal que extiende JFrame para crear la ventana principal de la aplicación
public class FormularioJFrame extends JFrame {

    // Campo para escribir el nombre o IP del servidor
    private final JTextField servidorField = new JTextField();
    // Campo para el puerto, con valor inicial "3307"
    private final JTextField puertoField = new JTextField("3307");
    // Usuario de la base de datos
    private final JTextField usuarioField = new JTextField();
    // Campo para la contraseña (oculta el texto)
    private final JPasswordField passwordField = new JPasswordField();

    // Constructor del JFrame (ventana de conexión)
    public FormularioJFrame() {
        // Título de la ventana
        super("Conexión a Base de Datos Northwind");

        //GridLayout para organizar los componentes en una cuadrícula
        setLayout(new GridLayout(5, 2, 5, 5));
        // Etiqueta para el campo de servidor
        add(new JLabel("Servidor:"));
        // Campo de texto para el servidor
        add(servidorField);
        // Etiqueta para el puerto
        add(new JLabel("Puerto:"));
        // Campo de texto para el puerto
        add(puertoField);
        // Etiqueta para el usuario
        add(new JLabel("Usuario:"));
        // Campo de texto para el usuario
        add(usuarioField);
        // Etiqueta para la contraseña
        add(new JLabel("Contraseña:"));
        // Campo de contraseña
        add(passwordField);

        // Boton para conectar
        JButton conectarButton = new JButton("Conectar");
        add(new JLabel());
        add(conectarButton);

        // Asocia la acción del botón al método conectar
        conectarButton.addActionListener(this::conectar);

        // Cierra la aplicacion al cerrar ventanas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamano de la ventana
        setSize(350, 200);
        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        // Hace visible la ventana
        setVisible(true);
    }

    // Método para realizar la conexión cuando se pulsa el botón
    private void conectar(ActionEvent e) {
        // Obtiene y limpia el texto delos campos
        String servidor = servidorField.getText().trim();
        String puerto = puertoField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validaciones para asegurarse de que todos los campos se han llenado
        if (servidor.isEmpty() || puerto.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }
        // Verifica si el puerto es un número
        if (!puerto.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser numérico.");
            return;
        }

        // Construye la URL de conexión usando los datos del formulario
        String conn = "jdbc:mysql://" + servidor + ":" + puerto + "/NorthWind";

        // Intenta conectar con la base de datos
        try {
            // Establece conexion con los datos proporcionados en esta linea
            Connection connection = DriverManager.getConnection(conn, usuario, password);
            // Mensaje si la conexión es exitosa
            JOptionPane.showMessageDialog(this, "Conexión exitosa.");

            // Abre una nueva ventana (JFrame) para mostrar los productos usando la conexión a la base de datos
            new ProductosJFrame(connection);

            // Cierra la ventana de conexión porque ya se realizó la conexión
            this.dispose();

            // En caso de error con la conexión
        } catch (SQLException ex) {
            // Mensaje de error si la conexion falla
            JOptionPane.showMessageDialog(this, "Error al conectar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Ejecuta la creación del JFrame en el hilo de la interfaz gráfica
        SwingUtilities.invokeLater(FormularioJFrame::new);
    }
}
