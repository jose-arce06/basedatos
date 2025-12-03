import javax.swing.*;

public class Permissions {
    public static boolean tienePermiso(String rolUsuarioActual, String operacion) {
        if ("ADMIN".equals(rolUsuarioActual)) {
            return true;
        }
        switch (operacion) {
            case "VER_PRODUCTOS":
            case "AGREGAR_PRODUCTOS":
            case "MODIFICAR_PRODUCTOS":
            case "ELIMINAR_PRODUCTOS":
                return "PRODUCTOS".equals(rolUsuarioActual);
            case "VER_ALMACENES":
            case "AGREGAR_ALMACENES":
            case "ELIMINAR_ALMACENES":
                return "ALMACENES".equals(rolUsuarioActual);
            default:
                return false;
        }
    }
}
