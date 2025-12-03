import java.sql.*;

public class AuthService {
    public static String validarUsuarioYObtenerRol(Connection connection, String nombre, String contrasena) {
        try {
            String hash = SecurityUtils.encriptarSHA256(contrasena);
            PreparedStatement ps = connection.prepareStatement(
                "SELECT rol FROM usuarios WHERE nombre = ? AND contraseña = ?"
            );
            ps.setString(1, nombre);
            ps.setString(2, hash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("rol");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void actualizarUltimoInicio(Connection connection, String nombre) {
        try {
            String fecha = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE usuarios SET ultima_vez_que_inicio_sesion = ? WHERE nombre = ?"
            );
            ps.setString(1, fecha);
            ps.setString(2, nombre);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error actualizando último inicio: " + e.getMessage());
        }
    }
}
