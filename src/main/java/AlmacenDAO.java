import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;

public class AlmacenDAO {
    public static void cargarAlmacenes(Connection connection, DefaultTableModel almacenesModel) {
        almacenesModel.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, nombre, fecha_ultima_modificacion, ultimo_usuario_modificacion FROM almacenes")) {
            while (rs.next()) {
                almacenesModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("fecha_ultima_modificacion"),
                        rs.getString("ultimo_usuario_modificacion")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar almacenes: " + e.getMessage(), e);
        }
    }

    public static void agregarAlmacen(Connection connection, String nombre, String fechaActual, String usuarioActual) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO almacenes (nombre, fecha_ultima_modificacion, ultimo_usuario_modificacion) VALUES (?, ?, ?)"
            );
            ps.setString(1, nombre);
            ps.setString(2, fechaActual);
            ps.setString(3, usuarioActual);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar almacén: " + e.getMessage(), e);
        }
    }

    public static boolean tieneProductos(Connection connection, int idAlmacen) {
        try {
            PreparedStatement checkPs = connection.prepareStatement(
                "SELECT COUNT(*) FROM productos WHERE almacen = ?"
            );
            checkPs.setInt(1, idAlmacen);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar productos asociados: " + e.getMessage(), e);
        }
    }

    public static void eliminarAlmacen(Connection connection, int idAlmacen) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM almacenes WHERE id=?");
            ps.setInt(1, idAlmacen);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar almacén: " + e.getMessage(), e);
        }
    }

    public static Map<String, Integer> obtenerMapaAlmacenes(Connection connection) {
        Map<String, Integer> mapa = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre FROM almacenes ORDER BY nombre")) {
            while (rs.next()) {
                mapa.put(rs.getString("nombre"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar almacenes: " + e.getMessage(), e);
        }
        return mapa;
    }
}
