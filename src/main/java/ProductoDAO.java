import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ProductoDAO {
    public static void cargarProductos(Connection connection, DefaultTableModel productosModel) {
        productosModel.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.id, p.nombre, p.precio, p.cantidad, p.departamento, " +
                     "p.almacen, a.nombre as almacen_nombre, " +
                     "p.fecha_ultima_modificacion, p.ultimo_usuario_modificacion " +
                     "FROM productos p LEFT JOIN almacenes a ON p.almacen = a.id")) {
            while (rs.next()) {
                productosModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad"),
                        rs.getString("departamento"),
                        rs.getString("almacen_nombre"),
                        rs.getString("fecha_ultima_modificacion"),
                        rs.getString("ultimo_usuario_modificacion")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar productos: " + e.getMessage(), e);
        }
    }

    public static void agregarProducto(Connection connection, String nombre, double precio, int cantidad,
                                       String departamento, int idAlmacen, String fechaActual, String usuarioActual) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO productos (nombre, precio, cantidad, departamento, almacen, fecha_ultima_modificacion, ultimo_usuario_modificacion) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setInt(3, cantidad);
            ps.setString(4, departamento);
            ps.setInt(5, idAlmacen);
            ps.setString(6, fechaActual);
            ps.setString(7, usuarioActual);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar producto: " + e.getMessage(), e);
        }
    }

    public static void modificarProducto(Connection connection, int id, String nombre, double precio, int cantidad,
                                         String departamento, int idAlmacen, String fechaActual, String usuarioActual) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE productos SET nombre=?, precio=?, cantidad=?, departamento=?, almacen=?, fecha_ultima_modificacion=?, ultimo_usuario_modificacion=? WHERE id=?"
            );
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setInt(3, cantidad);
            ps.setString(4, departamento);
            ps.setInt(5, idAlmacen);
            ps.setString(6, fechaActual);
            ps.setString(7, usuarioActual);
            ps.setInt(8, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar producto: " + e.getMessage(), e);
        }
    }

    public static void eliminarProducto(Connection connection, int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM productos WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }
}
