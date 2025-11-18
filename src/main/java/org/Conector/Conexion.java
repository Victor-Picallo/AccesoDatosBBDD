package org.Conector;

import java.sql.*;

public class Conexion {

    public static void main(String[] args) {

        String conn = "jdbc:mysql://localhost:3307/NorthWind";
        String root = "root";
        String pass = "";

        try {
            Connection connection = DriverManager.getConnection(conn, root, pass);

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM products");
            ResultSetMetaData rsmd = rs.getMetaData();

            for(int i = 1; i < rsmd.getColumnCount(); i++) {
                System.out.print(rsmd.getColumnName(i) + " ");
            }
            System.out.println();
            while(rs.next()) {
                for(int i = 1; i<rsmd.getColumnCount(); i++) {
                    System.out.print(rs.getObject(i) + " ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
