import java.sql.*;

public class Database {
    public static Connection conectarBD() throws Exception {
        Class.forName("org.sqlite.JDBC");
        // Mantener la misma ruta de la BD que el código original
        return DriverManager.getConnection(
            "jdbc:sqlite:C:/Users/Home/Documents/Shool/Desarrollo de sistemas 2/intellij/basedatos/src/main/resources/InventarioBD_2.db"
        );
    }
}
